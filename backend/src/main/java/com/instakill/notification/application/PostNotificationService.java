package com.instakill.notification.application;

import com.instakill.post.domain.Post;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostNotificationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NTFY_TOPIC_URL = "https://ntfy.sh/instakill_demo_alerts";

    @Async
    public void notifyPostCreated(Post post) {
        try {
            // Using record accessors .id() and .createdAt() instead of getters
            String message = String.format("New post created! ID: %s | Time: %s",
                    post.id(), post.createdAt());

            restTemplate.postForEntity(NTFY_TOPIC_URL, message, String.class);
            System.out.println("Async notification sent successfully to ntfy.sh.");
        } catch (Exception e) {
            System.err.println("Failed to send async notification: " + e.getMessage());
        }
    }
}