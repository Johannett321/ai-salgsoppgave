package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.DTO.RequestMarketingFileDTO;
import com.johansvartdal.SpringAI.enums.DiscordMessageChannel;
import com.johansvartdal.SpringAI.exception.NotFoundException;
import com.johansvartdal.SpringAI.model.Email;
import com.johansvartdal.SpringAI.model.PreRegisteredUser;
import com.johansvartdal.SpringAI.model.User;
import com.johansvartdal.SpringAI.repository.PreRegisteredUserRepository;
import com.johansvartdal.SpringAI.repository.UserRepository;
import com.johansvartdal.SpringAI.utils.EnvironmentUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class MarketingService {
    private final MailChimpService mailChimpService;
    private final PreRegisteredUserRepository preRegisteredUserRepository;
    private final DiscordMessageService discordMessageService;
    private final ResourceLoader resourceLoader;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public MarketingService(MailChimpService mailChimpService, PreRegisteredUserRepository preRegisteredUserRepository, DiscordMessageService discordMessageService, ResourceLoader resourceLoader, EmailService emailService, UserRepository userRepository) {
        this.mailChimpService = mailChimpService;
        this.preRegisteredUserRepository = preRegisteredUserRepository;
        this.discordMessageService = discordMessageService;
        this.resourceLoader = resourceLoader;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    public void preRegisterAndEmailFile(RequestMarketingFileDTO requestMarketingFileDTO) throws MessagingException {
        preRegisterUser(requestMarketingFileDTO);

        String downloadLink = generateDownloadLink(requestMarketingFileDTO);

        mailResource(requestMarketingFileDTO, downloadLink);
    }

    private String generateDownloadLink(RequestMarketingFileDTO requestMarketingFileDTO) {
        return EnvironmentUtils.getBackendUrl() + "api/v1/marketing/download-file/" + getActualFileName(requestMarketingFileDTO.getFileName());
    }

    private void preRegisterUser(RequestMarketingFileDTO requestMarketingFileDTO) {
        // make sure user does not already exist
        Optional<User> optionalUser = userRepository.findUserByEmail(requestMarketingFileDTO.getEmail());
        Optional<PreRegisteredUser> optionalPreRegisteredUser = preRegisteredUserRepository.findByEmail(requestMarketingFileDTO.getEmail());
        if (optionalUser.isPresent() || optionalPreRegisteredUser.isPresent()) {
            return;
        }

        PreRegisteredUser preRegisteredUser = new PreRegisteredUser();
        preRegisteredUser.setEmail(requestMarketingFileDTO.getEmail().toLowerCase());
        preRegisteredUser.setFirstName(requestMarketingFileDTO.getFirstName());

        preRegisteredUserRepository.save(preRegisteredUser);

        discordMessageService.sendMessage(DiscordMessageChannel.NOTIFICATIONS, preRegisteredUser.getEmail() + " just pre-registered to download: " + requestMarketingFileDTO.getFileName());

        mailChimpService.addCustomerToMailChimp(requestMarketingFileDTO.getEmail(), requestMarketingFileDTO.getFirstName());
        mailChimpService.addTagToUser(requestMarketingFileDTO.getEmail(), "pre-registered");
    }

    private void mailResource(RequestMarketingFileDTO requestMarketingFileDTO, String downloadLink) throws MessagingException {
        Email email = new Email();
        email.setTo(requestMarketingFileDTO.getEmail());
        email.setSubject("Nedlastingen din er klar: " + getFileTitle(requestMarketingFileDTO.getFileName()));
        email.setBody("<html style=\"padding: 0\">\n" +
                "<head>\n" +
                "    <style>\n" +
                "        html, body {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div style=\"width: 100%; height: 100%; background-color: #ecf1ef; padding-top: 30px; padding-bottom: 30px;\">\n" +
                "    <div style=\"width: 100%; text-align: center; margin-bottom: 30px\">\n" +
                "        <img src=\"https://aisalgsoppgave.no/images/logo_dark.png\" style=\"height: 70px;\"/>\n" +
                "    </div>\n" +
                "    <div style=\"max-width: 800px; background-color: white; border-radius: 10px; box-shadow: rgba(156,156,156,0.47) 10px 10px 20px; margin-left: auto; padding: 30px; padding-top: 60px; padding-bottom: 60px; margin-right: auto; text-align: center; font-family: 'Helvetica Neue',serif; font-size: 1.1em\">\n" +
                "        <h1 style=\"padding-top: 0; margin-top: 0\">Nedlastningen din er klar</h1>\n" +
                "        <p>Hei " + requestMarketingFileDTO.getFirstName() + ",</p>\n" +
                "        <p>Nedlastningen din er klar. Last ned "+ requestMarketingFileDTO.getFileName() + " her:</p>\n" +
                "        <a href=\"" + downloadLink + "\">\n" +
                "            <button style=\"background-color: #305345; color: white; border-radius: 10px; border:0; padding: 15px; cursor: pointer; font-size: 1em\">\n" +
                "                Last ned " + requestMarketingFileDTO.getFileName() +"\n" +
                "            </button>\n" +
                "        </a>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>\n");
        email.setIsHTML(true);
        emailService.sendEmail(email);
    }

    public ResponseEntity<Resource> actuallyDownloadFile(String filename) {
        try {
            // Load the file as a Resource
            Resource resource = resourceLoader.getResource("classpath:marketing/" + filename);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Set the content type based on the file type
            String contentType = "application/octet-stream"; // Default to binary stream
            String fileExtension = getFileExtension(filename);
            if (fileExtension.equals("txt")) {
                contentType = "text/plain";
            } else if (fileExtension.equals("png")) {
                contentType = "image/png";
            } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
                contentType = "image/jpeg";
            } else if (fileExtension.equals("pdf")) {
                contentType = "application/pdf"; // Content type for PDF files
            }
            // Add more content types as necessary

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getFileExtension(String filename) {
        if (filename.lastIndexOf(".") != -1 && filename.lastIndexOf(".") != 0) {
            return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(); // Convert to lower case for consistency
        }
        return "";
    }

    private String getActualFileName(String id) {
        return switch (id) {
            case "budsjett" ->"Budsjett.xlsx";
            case "førstegangskjøp-håndbok" -> "førstegangskjøp-håndbok.pdf";
            case "10-spørsmål" -> "10-spørsmål.pdf";
            default -> throw new NotFoundException("Fant ikke " + id);
        };
    }

    /**
     * Makes sure the file name is OK
     */
    private String getFileTitle(String id) {
        return switch (id) {
            case "budsjett" ->"Budsjett";
            case "førstegangskjøp-håndbok.pdf" -> "Førstegangskjøp håndbok";
            case "10-spørsmål.pdf" -> "10 spørsmål for å få bedre oversikt over en bolig";
            default -> throw new NotFoundException("Fant ikke " + id);
        };
    }
}
