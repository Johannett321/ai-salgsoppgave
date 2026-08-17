package com.johansvartdal.SpringAI.provider;

import com.johansvartdal.SpringAI.model.Salgsoppgave;
import com.johansvartdal.SpringAI.model.SalgsoppgaveBemerkning;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveBemerkningRepository;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveRepo;
import com.johansvartdal.SpringAI.service.DataProvidersManagerService;
import com.johansvartdal.SpringAI.service.SalgsoppgaveJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RemarksProvider extends DataProvider{

    private final SalgsoppgaveBemerkningRepository salgsoppgaveBemerkningRepository;
    private final SalgsoppgaveRepo salgsoppgaveRepo;

    public RemarksProvider(SalgsoppgaveJobService salgsoppgaveJobService, ChatModel chatModel, SalgsoppgaveBemerkningRepository salgsoppgaveBemerkningRepository, SalgsoppgaveRepo salgsoppgaveRepo, PgVectorStore vectorStore, DataProvidersManagerService dataProvidersManagerService) {
        super(salgsoppgaveJobService, chatModel, vectorStore, dataProvidersManagerService);
        this.salgsoppgaveBemerkningRepository = salgsoppgaveBemerkningRepository;
        this.salgsoppgaveRepo = salgsoppgaveRepo;
    }

    @Override
    public int getOrder() {
        return 101;
    }

    @Override
    public void onSalgsoppgaveCreated(SalgsoppgaveJob salgsoppgaveJob) {
        /*
        if (EnvironmentUtils.getEnvironment() == Environment.DEVELOPMENT) {
            markSalgsoppgaveJobAsFinished(salgsoppgaveJob);
            return;
        }
         */

        log.debug("Finding remarks...");

        List<Document> documents = searchVectorStoreFor("Hva sier tilstandsrapporten om boligen?", salgsoppgaveJob, 2);
        documents.addAll(searchVectorStoreFor("Hvordan er tilstanden på boligen?", salgsoppgaveJob, 2));
        documents.addAll(searchVectorStoreFor("Hvilke TG3 feil finnes?", salgsoppgaveJob, 3));
        documents.addAll(searchVectorStoreFor("Hvilke TG2 feil finnes?", salgsoppgaveJob, 3));
        documents.addAll(searchVectorStoreFor("Hvilke feil med tilstandsgrad 2 eller høyere finnes?", salgsoppgaveJob, 3));
        documents.addAll(searchVectorStoreFor("Hva sier selger i egenerklæringen?", salgsoppgaveJob, 3));


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String content = documents.stream()
                .map(Document::getText)
                .distinct()
                .peek(d -> log.info("RemDoc: {}", d.substring(0,2) + d.length()))
                .collect(Collectors.joining("\n"));

        ArrayList<String> allRemarks = new ArrayList<>(findRemarks(content));

        // save most important remarks
        List<SalgsoppgaveBemerkning> salgsoppgaveBemerkninger = allRemarks.stream()
                .map(SalgsoppgaveBemerkning::new)
                .peek(bemerkning -> bemerkning.setSalgsoppgave(salgsoppgaveJob.getSalgsoppgave()))
                .toList();
        salgsoppgaveBemerkninger = salgsoppgaveBemerkningRepository.saveAll(salgsoppgaveBemerkninger);

        // connect remarks to salgsoppgave
        Salgsoppgave salgsoppgave = salgsoppgaveJob.getSalgsoppgave();
        salgsoppgave.setBemerkninger(salgsoppgaveBemerkninger);
        salgsoppgaveRepo.save(salgsoppgave);

        markSalgsoppgaveJobAsFinished(salgsoppgaveJob);
    }

    private List<String> findRemarks(String content) {
        BeanOutputConverter<String[]> beanOutputConverter = new BeanOutputConverter<>(String[].class);

        SystemPromptTemplate systemMessage = new SystemPromptTemplate("""
                Lag en liste på ca 20 punkter over alle feil og mangler som kjøperen bør vite om med følgende bolig. Altså alle feil med
                tilstandsgrad 2 eller høyere. Ikke start med noe sånn som "her er en liste...". Gå rett på sak og start
                på json outputen. Altså start med '[' symbol
                {format}
                """);
        systemMessage.add("format", beanOutputConverter.getFormat());

        UserMessage userMessage = new UserMessage(content);

        Prompt prompt = new Prompt(List.of(systemMessage.createMessage(), userMessage));
        String response = getChatModel().call(prompt).getResult().getOutput().getText();

        int startIndex = response.indexOf("[");
        int endIndex = response.indexOf("]");

        if (startIndex == -1 || endIndex == -1) {
            return new ArrayList<>();
        }

        response = response.substring(startIndex, endIndex+1);

        log.info("Response for converter: {}", response);

        try {
            String[] listOfBemerkninger = beanOutputConverter.convert(response);
            if (listOfBemerkninger != null) {
                return Arrays.asList(listOfBemerkninger);
            }
        }catch (Exception e) {
            log.debug(response);
            log.info("Hopper over chunk, da den feilet med å parse til json");
        }

        return new ArrayList<>();
    }
}
