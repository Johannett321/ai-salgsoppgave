package com.johansvartdal.SpringAI.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FinnTools {

    @Value("${salgsoppgave.test}")
    private Boolean testMode;

    @Value("${salgsoppgave.datadir}")
    private String dataDir;

    public boolean isPdf(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            headers.set("Accept-Encoding", "gzip, deflate, br, zstd");
            headers.set("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8,nb;q=0.7");
            headers.set("Cache-Control", "no-cache");
            headers.set("DNT", "1");
            headers.set("Pragma", "no-cache");
            headers.set("Priority", "u=0, i");
            headers.set("Sec-CH-UA", "\"Not?A_Brand\";v=\"99\", \"Chromium\";v=\"130\"");
            headers.set("Sec-CH-UA-Mobile", "?0");
            headers.set("Sec-CH-UA-Platform", "\"macOS\"");
            headers.set("Sec-Fetch-Dest", "document");
            headers.set("Sec-Fetch-Mode", "navigate");
            headers.set("Sec-Fetch-Site", "none");
            headers.set("Sec-Fetch-User", "?1");
            headers.set("Upgrade-Insecure-Requests", "1");
            headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36");

            // Create an HttpEntity with the headers
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Send the request with headers
            ResponseEntity<Void> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Void.class
            );

            MediaType contentType = response.getHeaders().getContentType();
            log.debug("ContentType: {}", contentType);

            return MediaType.APPLICATION_PDF.equals(contentType);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String storePdf(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("No file found");
        }

        /*
        // Validation: Check file type (e.g., .pdf)
        if (!file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Invalid file type. Please upload a PDF.");
        }

         */

        // Save the file to your desired location
        File outputFile = new File(dataDir + File.separator + System.currentTimeMillis() + ".txt");
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(file.getBytes());
        fos.close();

        return outputFile.getAbsolutePath();
    }
}
