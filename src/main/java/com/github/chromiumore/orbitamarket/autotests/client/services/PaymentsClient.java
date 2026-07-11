package com.github.chromiumore.orbitamarket.autotests.client.services;

import com.github.chromiumore.orbitamarket.autotests.client.services.api.ApiClient;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class PaymentsClient {

    @Autowired
    private ApiClient apiClient;

    @Value("${autotests.config.payments-url}")
    private String url;

    public Response createAccount(UUID userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());
        return apiClient.post(url + "/accounts", new HashMap<>(), headers);
    }

    public Response topUp(UUID userId, Double amount) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);
        return apiClient.post(url + "/accounts/top-up", requestBody, headers);
    }

    public Response getBalance(UUID userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.get(url + "/accounts/balance", headers);
    }
}
