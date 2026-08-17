package com.johansvartdal.SpringAI.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;


@Slf4j
public class PdfUtils {

    public static String extractTextFromPDF(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    public static String extractTextFromPDFUrl(String url) {
        StringBuilder pdfText = new StringBuilder();

        log.info("Downloading prospect...");
        try {
            // Open a connection to the URL and fetch the PDF content
            URL pdfUrl = new URL(url);
            try (InputStream pdfStream = pdfUrl.openStream(); PDDocument document = PDDocument.load(pdfStream)) {

                // Use PDFTextStripper to extract text from the PDF
                PDFTextStripper pdfStripper = new PDFTextStripper();
                pdfText.append(pdfStripper.getText(document));

            } catch (IOException e) {
                e.printStackTrace();
                return "Error reading PDF content.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error fetching PDF from URL.";
        }

        // Return the extracted text
        return pdfText.toString();
    }

    public static String extractTextFromPDF(MultipartFile file) throws IOException {
        String content = "";

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            content = pdfStripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging this in production
            // Optionally, you could throw a custom exception here
        }

        return content;
    }
}
