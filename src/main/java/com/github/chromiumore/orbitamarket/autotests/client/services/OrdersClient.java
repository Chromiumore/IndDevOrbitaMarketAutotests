package com.github.chromiumore.orbitamarket.autotests.client.services;

import com.github.chromiumore.orbitamarket.autotests.client.services.api.ApiClient;
import com.github.chromiumore.orbitamarket.autotests.config.TestConfig;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrdersClient {

    private final ApiClient apiClient;
    private final static String ORDERS_URL = TestConfig.ORDERS_URL;

    public OrdersClient() {
        apiClient = new ApiClient();
    }

    public Response createOrder(UUID userId, Map<String, Object> body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.post(ORDERS_URL, body, headers);
    }

    public Response getUserOrders(UUID userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.get(ORDERS_URL, headers);
    }

    public Response getOrder(UUID userId, Long orderId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.get(ORDERS_URL + "/" + orderId, headers);
    }
}
