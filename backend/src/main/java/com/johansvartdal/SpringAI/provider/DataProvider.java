package com.johansvartdal.SpringAI.provider;

import com.johansvartdal.SpringAI.listener.SalgsoppgaveJobCreatedListener;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.service.DataProvidersManagerService;
import com.johansvartdal.SpringAI.service.SalgsoppgaveJobService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Component
public abstract class DataProvider implements SalgsoppgaveJobCreatedListener {

    private final ChatModel chatModel;

    private final ArrayList<SalgsoppgaveJob> finished = new ArrayList<>();
    private final PgVectorStore vectorStore;

    public DataProvider(SalgsoppgaveJobService salgsoppgaveJobService, ChatModel chatModel, PgVectorStore vectorStore, DataProvidersManagerService dataProvidersManagerService) {
        dataProvidersManagerService.subscribeToNewSalgsoppgaveJob(this);
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    Double llmExtractToDouble(String instruction, String context) {
        return llmExtractToDouble(instruction, context, 5);
    }

    Double llmExtractToDouble(String instruction, String context, int attempts) {
        if (attempts == 0) {
            return null;
        }

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("""
                Bruk følgende context for å besvare spørsmålet fra brukeren:
                {context}
                """);
        systemPromptTemplate.add("context", context);

        UserMessage userMessage = new UserMessage(instruction);

        Prompt prompt = new Prompt(List.of(systemPromptTemplate.createMessage(), userMessage));
        String response = chatModel.call(prompt).getResult().getOutput().getText();
        String formattedResponse = response.replace(" ", "");

        System.out.println("----------------------------------------------------");
        System.out.println(instruction + "\n" + context);

        try {
            return Double.parseDouble(formattedResponse);
        }catch (NumberFormatException e) {
            return llmExtractToDouble(instruction, context, attempts -1);
        }
    }

    String llmExtractToString(String instruction, String context, int attempts) {
        if (attempts == 0) {
            return null;
        }

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("""
                Bruk følgende context for å besvare spørsmålet fra brukeren:
                {context}
                
                Dersom svaret brukeren spør om ikke finnes i context, svarer du 'ukjent'
                """);
        systemPromptTemplate.add("context", context);

        UserMessage userMessage = new UserMessage(instruction);

        Prompt prompt = new Prompt(List.of(systemPromptTemplate.createMessage(), userMessage));
        String response = chatModel.call(prompt).getResult().getOutput().getText();

        System.out.println("----------------------------------------------------");
        System.out.println(instruction + "\n" + context);

        if (response.toLowerCase().contains("ukjent")) {
            return llmExtractToString(instruction, context, attempts -1);
        }

        return response;
    }

    String getTextUsingSearchTerm(SalgsoppgaveJob salgsoppgaveJob, int margin, String term) {
        int[] marginBegins = getMarginBegins(salgsoppgaveJob, margin, term);
        StringBuilder stringBuilder = new StringBuilder();
        for (int marginBegin : marginBegins) {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append("\n");
            }

            stringBuilder.append(salgsoppgaveJob.getPdfContent().substring(marginBegin, marginBegin + (margin * 2)));
        }

        return stringBuilder.toString();
    }

    void markSalgsoppgaveJobAsFinished(SalgsoppgaveJob salgsoppgaveJob) {
        log.info("{} has finished it's work on '{}'", this.getClass().getName(), salgsoppgaveJob.getId());
        finished.add(salgsoppgaveJob);
    }

    private int[] getMarginBegins(SalgsoppgaveJob salgsoppgaveJob, int margin, String searchTerm) {
        List<Integer> indices = new ArrayList<>();
        int totalprisIndex = salgsoppgaveJob.getPdfContent().toLowerCase().indexOf(searchTerm.toLowerCase());

        while (totalprisIndex != -1) {
            // Convert the index to a "begin" index based on the margin
            int beginIndex = Math.max(0, totalprisIndex - margin);

            indices.add(beginIndex);

            // Find the next occurrence of the search term
            totalprisIndex = salgsoppgaveJob.getPdfContent().toLowerCase().indexOf(searchTerm.toLowerCase(), totalprisIndex + 1);
        }

        int[] result = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = indices.get(i);
        }
        return result;
    }

    protected List<Document> searchVectorStoreFor(String query, SalgsoppgaveJob salgsoppgaveJob, int numResults) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();

        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(numResults)
                .similarityThreshold(0.4)
                .filterExpression(
                        b.eq("salgsoppgaveJob", salgsoppgaveJob.getId()).build())
                .build());
    }
}
