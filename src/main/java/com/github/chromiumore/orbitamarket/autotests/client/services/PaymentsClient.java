package com.github.chromiumore.orbitamarket.autotests.client.services;

import com.github.chromiumore.orbitamarket.autotests.client.services.api.ApiClient;
import com.github.chromiumore.orbitamarket.autotests.config.TestConfig;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentsClient {

    private final ApiClient apiClient;
    private final static String PAYMENTS_URL = TestConfig.PAYMENTS_URL;

    public PaymentsClient() {
        apiClient = new ApiClient();
    }

    public Response createAccount(UUID userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());
        return apiClient.post(PAYMENTS_URL + "/accounts", new HashMap<>(), headers);
    }

    public Response topUp(UUID userId, Double amount) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);
        return apiClient.post(PAYMENTS_URL + "/accounts/top-up", requestBody, headers);
    }

    public Response getBalance(UUID userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.get(PAYMENTS_URL + "/accounts/balance", headers);
    }
}
