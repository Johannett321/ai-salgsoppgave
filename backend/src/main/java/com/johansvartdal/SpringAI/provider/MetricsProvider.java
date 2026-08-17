package com.johansvartdal.SpringAI.provider;

import com.johansvartdal.SpringAI.model.Salgsoppgave;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveRepo;
import com.johansvartdal.SpringAI.service.DataProvidersManagerService;
import com.johansvartdal.SpringAI.service.SalgsoppgaveJobService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

@Component
public class MetricsProvider extends DataProvider {

    private final SalgsoppgaveRepo salgsoppgaveRepo;

    public MetricsProvider(SalgsoppgaveJobService salgsoppgaveJobService, SalgsoppgaveRepo salgsoppgaveRepo, ChatModel chatModel, PgVectorStore vectorStore, DataProvidersManagerService dataProvidersManagerService) {
        super(salgsoppgaveJobService, chatModel, vectorStore, dataProvidersManagerService);
        this.salgsoppgaveRepo = salgsoppgaveRepo;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void onSalgsoppgaveCreated(SalgsoppgaveJob salgsoppgaveJob) {
        Salgsoppgave salgsoppgave = salgsoppgaveJob.getSalgsoppgave();
        Document doc = Jsoup.parse(salgsoppgaveJob.getFinnContent());

        Double prisAntydning = getNumericMetric(doc, "div.pb-24[data-testid=pricing-incicative-price] span.text-28.font-bold");
        salgsoppgave.setPrisAntydning(prisAntydning);

        Double totalPris = getNumericMetric(doc, "div[data-testid=pricing-total-price] dd.m-0.font-bold");
        salgsoppgave.setTotalPris(totalPris);

        Double bruksAreal = getNumericMetric(doc, "div[data-testid=info-usable-i-area] dd.m-0.font-bold");
        salgsoppgave.setBruksAreal(bruksAreal);

        Double byggeAar = getNumericMetric(doc, "div[data-testid=info-construction-year] dd");
        salgsoppgave.setByggeAar(byggeAar);

        salgsoppgaveRepo.save(salgsoppgave);
        markSalgsoppgaveJobAsFinished(salgsoppgaveJob);
    }

    private Double getNumericMetric(Document doc, String query) {
        try {
            Elements element = doc.select(query);
            if (element.isEmpty()) {
                return null;
            }
            String elementText = element.text();
            elementText = elementText.toLowerCase();

            if (elementText.contains("kr")) {
                elementText = elementText.replace("kr", "");
            }

            if (elementText.contains("(bra-i)")) {
                elementText = elementText.replace("(bra-i)", "");
            }

            if (elementText.contains("m²")) {
                elementText = elementText.replace("m²", "");
            }

            elementText = elementText.replace(" ", "");
            return Double.parseDouble(elementText);
        }catch (Exception e) {
            return null;
        }
    }

    private void findMetricsUsingLLM(SalgsoppgaveJob salgsoppgaveJob) {
        Salgsoppgave salgsoppgave = salgsoppgaveJob.getSalgsoppgave();
        String totalPrisText = getTextUsingSearchTerm(salgsoppgaveJob, 25, "totalpris");
        String prisAntydningText = getTextUsingSearchTerm(salgsoppgaveJob, 25, "prisantydning");
        String bruksArealText = getTextUsingSearchTerm(salgsoppgaveJob, 25, "bruksareal");
        String bruksArealText2 = getTextUsingSearchTerm(salgsoppgaveJob, 25, "bra total");
        String byggeAarText = getTextUsingSearchTerm(salgsoppgaveJob, 25, "byggeår");

        Double totalPris = llmExtractToDouble("Hva er totalprisen? Svar kun med totalprisen. Ingen forklaring " +
                "eller valuta fortegn", totalPrisText);
        salgsoppgave.setTotalPris(totalPris);
        salgsoppgaveRepo.save(salgsoppgave);

        Double prisAntydning = llmExtractToDouble("Hva er prisantydningen? Svar kun med prisantydningen. Ingen forklaring " +
                "eller valuta fortegn", prisAntydningText);
        salgsoppgave.setPrisAntydning(prisAntydning);
        salgsoppgaveRepo.save(salgsoppgave);

        Double bruksAreal = llmExtractToDouble("Hva er bruksarealen/BRA totalt på? Svar kun med bruksArealen. Ingen forklaring " +
                "eller valuta fortegn", bruksArealText + "\n" + bruksArealText2);
        salgsoppgave.setBruksAreal(bruksAreal);
        salgsoppgaveRepo.save(salgsoppgave);

        Double byggeAar = llmExtractToDouble("Når ble boligen bygget? Svar kun med byggeåret. Ingen forklaring " +
                "eller betegnelse", byggeAarText);
        salgsoppgave.setByggeAar(byggeAar);
        salgsoppgaveRepo.save(salgsoppgave);

        getFinished().add(salgsoppgaveJob);
    }
}
