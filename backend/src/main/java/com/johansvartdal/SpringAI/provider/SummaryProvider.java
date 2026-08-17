package com.johansvartdal.SpringAI.provider;

import com.johansvartdal.SpringAI.model.Salgsoppgave;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveRepo;
import com.johansvartdal.SpringAI.service.DataProvidersManagerService;
import com.johansvartdal.SpringAI.service.SalgsoppgaveJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SummaryProvider extends DataProvider{

    private final SalgsoppgaveRepo salgsoppgaveRepo;

    public SummaryProvider(SalgsoppgaveJobService salgsoppgaveJobService, ChatModel chatModel, SalgsoppgaveRepo salgsoppgaveRepo, PgVectorStore vectorStore, DataProvidersManagerService dataProvidersManagerService) {
        super(salgsoppgaveJobService, chatModel, vectorStore, dataProvidersManagerService);
        this.salgsoppgaveRepo = salgsoppgaveRepo;
    }

    @Override
    public int getOrder() {
        return 90;
    }

    @Override
    public void onSalgsoppgaveCreated(SalgsoppgaveJob salgsoppgaveJob) {
        /*
        if (EnvironmentUtils.getEnvironment() == Environment.DEVELOPMENT) {
            markSalgsoppgaveJobAsFinished(salgsoppgaveJob);
            return;
        }
         */

        log.debug("Creating summary...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Document> documents = searchVectorStoreFor("Hvor mange soverom er det? Er det loft? er det Stue? Hvordan er kjelleren?", salgsoppgaveJob, 2);
        documents.addAll(searchVectorStoreFor("Hvordan er beliggenheten til boligen? Finnes det daligvarebutikker, barnehager eller annet i nærheten?", salgsoppgaveJob, 2));
        documents.addAll(searchVectorStoreFor("Hvordan er det med oppvarming? Er det peis? Panelovn?", salgsoppgaveJob, 1));
        documents.addAll(searchVectorStoreFor("Hvordan er det med vann, avløp, kommunale tjenester?", salgsoppgaveJob, 1));
        documents.addAll(searchVectorStoreFor("Hvordan er det med parkering? Finnes det parkeringsplass?", salgsoppgaveJob, 1));
        documents.addAll(searchVectorStoreFor("Hvordan er kjøkkenet? Er kjøkkenet oppgradert?", salgsoppgaveJob, 1));
        documents.addAll(searchVectorStoreFor("Hvordan er badet? Er badet oppgradert?", salgsoppgaveJob, 1));

        String content = documents.stream()
                .map(Document::getText)
                .distinct()
                .peek(d -> log.info("SumDoc: {}", d.substring(0,2) + d.length()))
                .collect(Collectors.joining("\n"));

        String oppsummering = createOppsummering(content);


        // connect remarks to salgsoppgave
        Salgsoppgave salgsoppgave = salgsoppgaveJob.getSalgsoppgave();
        salgsoppgave.setOppsummering(oppsummering);
        salgsoppgaveRepo.save(salgsoppgave);

        markSalgsoppgaveJobAsFinished(salgsoppgaveJob);
    }

    private String createOppsummering(String content) {
        SystemPromptTemplate systemMessage = new SystemPromptTemplate("""
                Lag en oppsummering av feil og mangler som kjøperen bør vite om med følgende bolig. Ikke start med å
                si at det er en oppsummering over feil og mangler. Bare begynn rett på sak.
                """);

        systemMessage = new SystemPromptTemplate("""
                Lag en kort oppsummering av salgsoppgaven brukeren sender deg. Nevn ting som rommene i boligen,
                beliggenhet, standard, oppvarming, kjøkken og bad, vann og avløp, kommunale tjenester og bredbånd.
                Ikke start med å si at det er en oppsummering. Bare begynn rett på sak. For eksempel "Boligen er en...".
                """);

        UserMessage userMessage = new UserMessage(content);

        Prompt prompt = new Prompt(List.of(systemMessage.createMessage(), userMessage));
        String response = getChatModel().call(prompt).getResult().getOutput().getText();

        return response;
    }
}
