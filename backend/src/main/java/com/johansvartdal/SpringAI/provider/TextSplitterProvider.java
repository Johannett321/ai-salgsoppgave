package com.johansvartdal.SpringAI.provider;

import com.johansvartdal.SpringAI.enums.JobStatus;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveJobRepo;
import com.johansvartdal.SpringAI.service.DataProvidersManagerService;
import com.johansvartdal.SpringAI.service.SalgsoppgaveJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TextSplitterProvider extends DataProvider {

    private final SalgsoppgaveJobRepo salgsoppgaveJobRepo;

    public TextSplitterProvider(SalgsoppgaveJobService salgsoppgaveJobService, ChatModel chatModel, SalgsoppgaveJobRepo salgsoppgaveJobRepo, PgVectorStore vectorStore, DataProvidersManagerService dataProvidersManagerService) {
        super(salgsoppgaveJobService, chatModel, vectorStore, dataProvidersManagerService);
        this.salgsoppgaveJobRepo = salgsoppgaveJobRepo;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void onSalgsoppgaveCreated(SalgsoppgaveJob salgsoppgaveJob) {
        salgsoppgaveJob.setJobStatus(JobStatus.CREATING_EMBEDDINGS);
        salgsoppgaveJob = salgsoppgaveJobRepo.save(salgsoppgaveJob);

        String pdfContent = salgsoppgaveJob.getPdfContent();

        Document document = new Document(pdfContent, Map.of("salgsoppgaveJob", salgsoppgaveJob.getId()));

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
        List<Document> split = tokenTextSplitter.split(document);

        // create embeddings
        log.debug("Creating embeddings for paragraphs...");
        getVectorStore().add(split);
        log.debug("Created embeddings for paragraphs.");

        salgsoppgaveJob.setJobStatus(JobStatus.LLM_IN_PROGRESS);
        salgsoppgaveJobRepo.save(salgsoppgaveJob);

        markSalgsoppgaveJobAsFinished(salgsoppgaveJob);
    }
}
