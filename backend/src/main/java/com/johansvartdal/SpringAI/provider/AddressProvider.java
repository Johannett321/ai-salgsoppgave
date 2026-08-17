package com.johansvartdal.SpringAI.provider;

import com.johansvartdal.SpringAI.model.Salgsoppgave;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveRepo;
import com.johansvartdal.SpringAI.service.DataProvidersManagerService;
import com.johansvartdal.SpringAI.service.GeocodingService;
import com.johansvartdal.SpringAI.service.SalgsoppgaveJobService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AddressProvider extends DataProvider{

    private final SalgsoppgaveRepo salgsoppgaveRepo;
    private final GeocodingService geocodingService;

    public AddressProvider(SalgsoppgaveJobService salgsoppgaveJobService, SalgsoppgaveRepo salgsoppgaveRepo, ChatModel chatModel, GeocodingService geocodingService, PgVectorStore vectorStore, DataProvidersManagerService dataProvidersManagerService) {
        super(salgsoppgaveJobService, chatModel, vectorStore, dataProvidersManagerService);
        this.salgsoppgaveRepo = salgsoppgaveRepo;
        this.geocodingService = geocodingService;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void onSalgsoppgaveCreated(SalgsoppgaveJob salgsoppgaveJob) {
        Salgsoppgave salgsoppgave = salgsoppgaveJob.getSalgsoppgave();
        Document doc = Jsoup.parse(salgsoppgaveJob.getFinnContent());

        Element adressElement = doc.select("span[data-testid=object-address]").first();
        String address;
        if (adressElement == null || !adressElement.hasText()) {
            log.debug("Could not find address on page");
           return;
        }
        address = adressElement.text();


        String[] adressSplit = address.split(",");
        if (adressSplit.length > 1) {
            String[] del1 = adressSplit[0].trim().split(" ");
            String[] del2 = adressSplit[1].trim().split(" ");

            if (del1.length > 1 && del2.length > 1) {
                String gateNavn = Arrays.stream(del1)
                        .limit(del1.length-1)
                        .collect(Collectors.joining(" "))
                        .trim();

                salgsoppgave.setGateNavn(gateNavn);
                salgsoppgave.setGateNummer(del1[del1.length-1].trim());

                if (!del2[0].isEmpty()) {
                    salgsoppgave.setPostNummer(del2[0].trim());
                }else {
                    log.debug("Cannot create postnummer from " + del2[0].trim());
                }

                salgsoppgave.setPostSted(del2[1]);
            }
        }

        String fullAddress = salgsoppgave.getGateNavn() + " " +
                salgsoppgave.getGateNummer() + ", " +
                salgsoppgave.getPostNummer() + " " +
                salgsoppgave.getPostSted() + ", Norway";

        geocodingService.geocodeAddress(fullAddress)
                .subscribe(geocodeResponse -> {
                    if (geocodeResponse != null) {
                        salgsoppgave.setLatitude(Double.parseDouble(geocodeResponse.getLat()));
                        salgsoppgave.setLongtitude(Double.parseDouble(geocodeResponse.getLon()));

                        // Save the updated salgsoppgave with coordinates
                        salgsoppgaveRepo.save(salgsoppgave);
                        log.debug("Coordinates saved for address: " + fullAddress);
                    } else {
                        log.warn("Could not geocode address: " + fullAddress);
                    }
                });

        salgsoppgaveRepo.save(salgsoppgave);
        markSalgsoppgaveJobAsFinished(salgsoppgaveJob);
    }

    private void findAdressDetailsUsingLLM(SalgsoppgaveJob salgsoppgaveJob) {
        Salgsoppgave salgsoppgave = salgsoppgaveJob.getSalgsoppgave();

        String gateNavnText = getTextUsingSearchTerm(salgsoppgaveJob, 25, "adresse");

        String gateNavn = llmExtractToString("Hva er gatenavnet? Svar kun med gatenavnet (uten gatenummeret). Ingen forklaring", gateNavnText, 5);
        String gateNummer = llmExtractToString("Hva er gatenummeret? Svar kun med gatenummeret (uten gatenavnet). Ingen forklaring", gateNavnText, 5);
        salgsoppgave.setGateNavn(gateNavn);
        salgsoppgave.setGateNummer(gateNummer);
        salgsoppgaveRepo.save(salgsoppgave);

        String postNummerText = getTextUsingSearchTerm(salgsoppgaveJob, 25, "postnummer");
        //Double postNummer = llmExtractToDouble("Hva er postnummeret? Svar kun med postnummeret (uten poststedet). Ingen forklaring ", postNummerText);
        //salgsoppgave.setPostNummer(postNummer);
        salgsoppgaveRepo.save(salgsoppgave);

        String postStedText = getTextUsingSearchTerm(salgsoppgaveJob, 25, "poststed");
        String postSted = llmExtractToString("Hva er poststedet? Svar kun med poststedet (uten postnummeret). Ingen forklaring ", postStedText + "\n" + postNummerText, 5);
        salgsoppgave.setPostSted(postSted);
        salgsoppgaveRepo.save(salgsoppgave);
    }
}
