package com.johansvartdal.SpringAI.service;


import com.johansvartdal.SpringAI.enums.Environment;
import com.johansvartdal.SpringAI.utils.EnvironmentUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Service
public class MailChimpService {

    @Value("${mailchimp.api.key}")
    public String MAILCHIMP_API_KEY;

    @Value("${mailchimp.server.prefix}")
    public String MAILCHIMP_SERVER_PREFIX;

    @Value("${mailchimp.list.id}")
    public String LIST_ID;

    private final RestTemplate restTemplate = new RestTemplate();

    public void addCustomerToMailChimp(String email, String firstName) {
        addCustomerToMailChimp(email, firstName, null);
    }

    public void addCustomerToMailChimp(String email, String firstName, String lastName) {
        if (EnvironmentUtils.getEnvironment() == Environment.DEVELOPMENT) {
            log.warn("Communication with Mailchimp stopped due to environment being development. Attempted to add user with email: {}", email);
            return;
        }
        String url = "https://" + MAILCHIMP_SERVER_PREFIX + ".api.mailchimp.com/3.0/lists/" + LIST_ID + "/members/";

        // Create customer data
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email_address", email);
        requestBody.put("status", "subscribed");  // "subscribed", "pending", or "unsubscribed"

        // Add merge fields like first name and last name
        Map<String, String> mergeFields = new HashMap<>();
        mergeFields.put("FNAME", firstName);
        if (lastName != null) {
            mergeFields.put("LNAME", lastName);
        }
        requestBody.put("merge_fields", mergeFields);

        // Set up headers with basic authentication
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(("anystring:" + MAILCHIMP_API_KEY).getBytes()));
        headers.add("Content-Type", "application/json");

        // Prepare the HTTP request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    // Method to add a tag to a user
    public void addTagToUser(String email, String tagName) {
        if (EnvironmentUtils.getEnvironment() != Environment.PRODUCTION) {
            log.warn("Communication with Mailchimp stopped due to environment being development. Attempted to add tag '{}' to user with email: {}", tagName, email);
            return;
        }
        String subscriberHash = getSubscriberHash(email);
        String url = "https://" + MAILCHIMP_SERVER_PREFIX + ".api.mailchimp.com/3.0/lists/" + LIST_ID + "/members/" + subscriberHash + "/tags";

        // Create the tag data
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, String>> tags = new ArrayList<>();
        Map<String, String> tag = new HashMap<>();
        tag.put("name", tagName);
        tag.put("status", "active");  // "active" to add the tag, "inactive" to remove it
        tags.add(tag);
        requestBody.put("tags", tags);

        // Set up headers with basic authentication
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(("anystring:" + MAILCHIMP_API_KEY).getBytes()));
        headers.add("Content-Type", "application/json");

        // Prepare the HTTP request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public void removeTagFromUser(String email, String tagName) {
        if (EnvironmentUtils.getEnvironment() == Environment.DEVELOPMENT) {
            log.warn("Communication with Mailchimp stopped due to environment being development. Attempted to remove tag '{}' to user with email: {}", tagName, email);
            return;
        }
        String subscriberHash = getSubscriberHash(email);
        String url = "https://" + MAILCHIMP_SERVER_PREFIX + ".api.mailchimp.com/3.0/lists/" + LIST_ID + "/members/" + subscriberHash + "/tags";

        // Create the tag data
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, String>> tags = new ArrayList<>();
        Map<String, String> tag = new HashMap<>();
        tag.put("name", tagName);
        tag.put("status", "inactive");  // "inactive" to remove the tag
        tags.add(tag);
        requestBody.put("tags", tags);

        // Set up headers with basic authentication
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + Base64.getEncoder().encodeToString(("anystring:" + MAILCHIMP_API_KEY).getBytes()));
        headers.add("Content-Type", "application/json");

        // Prepare the HTTP request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Send the POST request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    // Helper method to get the MD5 hash of the email (required by MailChimp API)
    private String getSubscriberHash(String email) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(email.toLowerCase().getBytes(StandardCharsets.UTF_8));

            // Convert bytes to hex format
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating subscriber hash", e);
        }
    }
}
