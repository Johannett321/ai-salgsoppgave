package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.enums.Environment;
import com.johansvartdal.SpringAI.model.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.johansvartdal.SpringAI.utils.EnvironmentUtils.getEnvironment;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(Email email) throws MessagingException {
        if (getEnvironment() != Environment.PRODUCTION) {
            System.err.println("INFO: Email not sent in development mode!");
            System.out.println(email.toString());
            return;
        }
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("kontakt@aisalgsoppgave.no");// endres til no reply AISalgsoppgave
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setText(email.getBody(), email.getIsHTML());

        if (email.getAttachmentPath() != null && email.getAttachmentName() != null) {
            FileSystemResource file = new FileSystemResource(new File(email.getAttachmentPath()));
            helper.addAttachment(email.getAttachmentName(), file);
        }
        emailSender.send(message);

        System.out.println("Sending email:");
        System.out.println(email.toString());
    }
}
