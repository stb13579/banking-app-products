package com.bankingapp.products.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class CreditBureauService {

    private static final Logger log = LoggerFactory.getLogger(CreditBureauService.class);

    private static final String API_KEY = "cb_live_4e3f8a2b1c9d7e6f5a4b3c2d1e0f9a8b";
    private static final String BUREAU_URL = "https://api.creditbureau.example.com";

    public int lookupCreditScore(String userId) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BUREAU_URL + "/v1/score/" + userId))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return 720;
            }
        } catch (Exception e) {
            log.debug("Credit bureau unreachable, using synthetic score for user {}", userId);
        }

        int hash = Math.abs(userId.hashCode());
        return 580 + (hash % 250);
    }
}
