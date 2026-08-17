package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.enums.JobStatus;
import com.johansvartdal.SpringAI.exception.NotFoundException;
import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import com.johansvartdal.SpringAI.model.SalgsoppgavePdfUrl;
import com.johansvartdal.SpringAI.repository.SalgsoppgaveJobRepo;
import com.johansvartdal.SpringAI.utils.FinnTools;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class FinnScraper {

    private final SalgsoppgaveJobRepo salgsoppgaveJobRepo;
    private final ChatModel chatModel;
    private final FinnTools finnTools;

    public FinnScraper(SalgsoppgaveJobRepo salgsoppgaveJobRepo, ChatModel chatModel, FinnTools finnTools) {
        this.salgsoppgaveJobRepo = salgsoppgaveJobRepo;
        this.chatModel = chatModel;
        this.finnTools = finnTools;
    }

    public SalgsoppgaveJob getKomplettSalgsoppgaveUrl(SalgsoppgaveJob salgsoppgaveJob) throws NotFoundException {
        salgsoppgaveJob.setJobStatus(JobStatus.VISITING_FINN);
        salgsoppgaveJobRepo.save(salgsoppgaveJob);

        String finalLink;
        String komplettSalgsoppgaveUrl = getKomplettSalgsoppgaveUrlFromFinnPage(salgsoppgaveJob);
        if (komplettSalgsoppgaveUrl == null) {
            // fail
            log.warn("Failed to find komplett salgsoppgaveurl from {}", salgsoppgaveJob.getFinnUrl());
            throw new NotFoundException();
        }

        salgsoppgaveJob.setFinnKomplettSalgsoppgaveUrl(komplettSalgsoppgaveUrl);

        if (finnTools.isPdf(komplettSalgsoppgaveUrl)) {
            finalLink = komplettSalgsoppgaveUrl;
        }else {
            // get pdf
            log.info("Visiting {} and retrieving all clickable url's", komplettSalgsoppgaveUrl);
            salgsoppgaveJob.setJobStatus(JobStatus.VISITING_MEGLER);
            salgsoppgaveJobRepo.save(salgsoppgaveJob);

            Map<String, String> allClickableLinksOnPage = findAllClickableLinksOnPage(komplettSalgsoppgaveUrl);

            log.info("Asking ai to find salgsoppgave from a list of clickable urls...");

            finalLink = llmFindSalgsoppgaveUrlFromMap(allClickableLinksOnPage);
        }

        if (finalLink == null || !finnTools.isPdf(finalLink)) {
            // fail
            log.warn("Failed to download salgsoppgave PDF from link '{}'. IsPDF: {}", finalLink, finnTools.isPdf(finalLink));
            throw new NotFoundException();
        }

        log.info("SalgsoppgavePDFUrl retrieved: {}", finalLink);

        salgsoppgaveJob.setSalgsoppgavePDFUrl(finalLink);
        salgsoppgaveJobRepo.save(salgsoppgaveJob);

        // analyze pdf
        return salgsoppgaveJob;
    }

    private String getKomplettSalgsoppgaveUrlFromFinnPage(SalgsoppgaveJob job) {
        try {
            // Fetch the HTML from the URL
            Document doc = Jsoup.connect(job.getFinnUrl()).get();
            job.setFinnContent(doc.html());
            salgsoppgaveJobRepo.save(job);

            // Find the <div> with class "pb-16 mt-40 border-b"
            Elements divs = doc.select("div.pb-16.mt-40.border-b");

            // Iterate through the matching <div> elements
            for (Element div : divs) {
                // Find the <a> element inside the div that contains the "Se komplett salgsoppgave" text
                Element anchor = div.selectFirst("a:contains(Se komplett salgsoppgave)");

                // Check if the <a> element exists
                if (anchor != null) {
                    // Return the href attribute of the <a> element
                    return anchor.attr("href");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return null if nothing is found
        return null;
    }

    private Map<String, String> findAllClickableLinksOnPage(String url) {
        Map<String, String> linkMap = new HashMap<>();

        try {
            // Fetch the HTML from the URL
            Document doc = Jsoup.connect(url).get();

            log.info(doc.body().wholeText());

            // Select all <a> elements with href attributes (links)
            Elements anchorTags = doc.select("a[href]");

            // Loop through each <a> element
            for (Element anchor : anchorTags) {
                String link = anchor.attr("href");
                String text;
                if (anchor.hasText()) {
                    text = anchor.text();
                }else {
                    text = getAllTextFromChildren(anchor);
                }

                if (!link.isEmpty() && !text.isEmpty()) {
                    linkMap.put(text, link); // Add text and link to map
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the map containing clickable texts and corresponding links
        return linkMap;
    }

    // Recursive method to extract text from all child elements
    private String getAllTextFromChildren(Element element) {
        StringBuilder text = new StringBuilder();

        for (Element child : element.children()) {
            // Recursively get the text from each child element
            text.append(child.ownText()).append(" ");
            text.append(getAllTextFromChildren(child)); // Recurse into child elements
        }

        return text.toString().trim(); // Return the full text
    }

    private String llmFindSalgsoppgaveUrlFromMap(Map<String, String> bigLinksWithTitle) {
        // Convert the map entries to a list for easy batch processing
        List<Map.Entry<String, String>> entries = new ArrayList<>(bigLinksWithTitle.entrySet());

        // Define the batch size
        int batchSize = 125;

        // Process the entries in batches
        for (int i = 0; i < entries.size(); i += batchSize) {
            log.info("Working on batch: {}", i);
            // Determine the end index of the current batch
            int end = Math.min(i + batchSize, entries.size());
            List<Map.Entry<String, String>> batch = entries.subList(i, end);

            // Build the context string for the current batch
            StringBuilder context = new StringBuilder();
            for (Map.Entry<String, String> entry : batch) {
                context.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            // Prepare the LLM prompt
            BeanOutputConverter<SalgsoppgavePdfUrl> beanOutputConverter = new BeanOutputConverter<>(SalgsoppgavePdfUrl.class);
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate("""
            You are an API that is used to find salgsoppgaver within lists of URLs and titles.
            The user will send you a list of some URLs and corresponding titles. Please reply with a JSON object of
            what you think is the URL to the salgsoppgave PDF file.

            For example:
            {example}

            Do not reply with any explanation or anything else than the JSON object with the URL to the salgsoppgave PDF file.

            If you cannot find the PDF, just answer "unknown"

            {format}
            """);
            systemPromptTemplate.add("format", beanOutputConverter.getFormat());
            systemPromptTemplate.add("example", """
            {
                "salgsoppgavePdfUrl": "https://api.eie.no/salgsoppgave.pdf"
            }
            """);
            UserMessage userMessage = new UserMessage(context.toString());

            // Create and log the prompt
            Prompt prompt = new Prompt(List.of(systemPromptTemplate.createMessage(), userMessage));
            log.info("Running prompt \n {}", prompt.getInstructions().toString());

            // Call the LLM and get the response
            String response = chatModel.call(prompt).getResult().getOutput().getText();
            log.info("\n\nResponse: \n");
            log.info(response);

            // Attempt to parse the LLM's response
            try {
                SalgsoppgavePdfUrl result = beanOutputConverter.convert(response);
                String pdfUrl = result.getSalgsoppgavePdfUrl();
                // Check if the LLM found the PDF URL
                if (pdfUrl != null && !pdfUrl.equalsIgnoreCase("unknown")) {
                    return pdfUrl;
                }
            } catch (Exception e) {
                log.error("Error parsing response", e);
                // Continue to the next batch if an error occurs
            }
        }

        // Return null if no PDF URL is found
        return null;
    }
}
