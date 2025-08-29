package com.example.hiring.service;

import com.example.hiring.model.dto.GenerateWebhookRequest;
import com.example.hiring.model.dto.GenerateWebhookResponse;
import com.example.hiring.model.dto.SubmitFinalQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class HiringClient {

    private static final Logger log = LoggerFactory.getLogger(HiringClient.class);

    private final RestTemplate restTemplate;

    @Value("${app.hiring.base-url}")
    private String baseUrl;

    @Value("${app.hiring.generate-webhook-path}")
    private String generateWebhookPath;

    @Value("${app.hiring.test-webhook-path}")
    private String testWebhookPath;

    public HiringClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GenerateWebhookResponse generateWebhook(GenerateWebhookRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(generateWebhookPath)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GenerateWebhookRequest> entity = new HttpEntity<>(request, headers);

        log.info("Calling generateWebhook at {}", url);
        ResponseEntity<GenerateWebhookResponse> resp =
                restTemplate.exchange(url, HttpMethod.POST, entity, GenerateWebhookResponse.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException("Failed to generate webhook: " + resp.getStatusCode());
        }
        log.info("Received webhook and accessToken.");
        return resp.getBody();
    }

    public ResponseEntity<String> submitFinalQuery(String webhookUrlOrNull, String accessToken, String finalQuery) {
        String targetUrl = (webhookUrlOrNull != null && !webhookUrlOrNull.isBlank())
                ? webhookUrlOrNull
                : UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path(testWebhookPath)
                    .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // IMPORTANT: Use the token as-is (do not prepend "Bearer " unless instructed)
        headers.set("Authorization", accessToken);

        SubmitFinalQueryRequest payload = new SubmitFinalQueryRequest(finalQuery);
        HttpEntity<SubmitFinalQueryRequest> entity = new HttpEntity<>(payload, headers);

        log.info("Submitting final query to {}", targetUrl);
        return restTemplate.exchange(targetUrl, HttpMethod.POST, entity, String.class);
    }
}
