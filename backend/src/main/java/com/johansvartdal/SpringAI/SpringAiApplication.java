package com.johansvartdal.SpringAI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Properties;

/**
 * Chat-modell, embedding-modell og vektorlager settes opp av Spring AI sin
 * autokonfigurasjon, styrt fra application.properties:
 *
 *   spring.ai.model.chat=anthropic        – Claude svarer på spørsmål
 *   spring.ai.model.embedding=openai      – OpenAI lager embeddings
 *   spring.ai.vectorstore.pgvector.*      – pgvector-lageret
 *
 * API-nøklene leses fra miljøvariablene ANTHROPIC_API_KEY og OPENAI_API_KEY, som
 * application.properties mapper inn i spring.ai-egenskapene. Se README.
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
public class SpringAiApplication {

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    public static void main(String[] args) {
        SpringApplication.run(SpringAiApplication.class, args);
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.domeneshop.no");
        mailSender.setPort(587);

        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");

        return mailSender;
    }
}
