package com.johansvartdal.SpringAI.service;

import com.johansvartdal.SpringAI.enums.DiscordMessageChannel;
import com.johansvartdal.SpringAI.enums.Environment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.johansvartdal.SpringAI.utils.EnvironmentUtils.getEnvironment;

@Slf4j
@Service
public class DiscordMessageService {

    @Value("${discord.webhook.error}")
    private String discordErrorWebhook;

    @Value("${discord.webhook.notifications}")
    private String discordNotificationsWebhook;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(DiscordMessageChannel messageChannel, String message) {
        if (getEnvironment() != Environment.PRODUCTION) {
            if (messageChannel != DiscordMessageChannel.ERRORS) {
                log.debug("Discord message not sent due to Discord being disable outside production. Message content: \n{}", message);
            }

            return;
        }

        // Create the payload for the Discord webhook
        Map<String, String> payload = new HashMap<>();

        // Ensure the message is within the 2000 character limit
        if (message.length() > 2000) {
            message = message.substring(0, 1997) + "...";
        }

        payload.put("content", message);

        String channelUrl = switch (messageChannel) {
            case NOTIFICATIONS -> discordNotificationsWebhook;
            case ERRORS -> discordErrorWebhook;
        };

        // Send the POST request to the Discord webhook URL
        restTemplate.postForObject(channelUrl, payload, String.class);
    }
}
