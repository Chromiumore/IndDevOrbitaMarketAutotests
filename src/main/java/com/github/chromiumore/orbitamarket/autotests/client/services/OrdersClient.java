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
public class OrdersClient {

    @Autowired
    private ApiClient apiClient;

    @Value("${autotests.config.orders-url}")
    private String url;

    public Response createOrder(UUID userId, Map<String, Object> body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.post(url, body, headers);
    }

    public Response getUserOrders(UUID userId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.get(url, headers);
    }

    public Response getOrder(UUID userId, Long orderId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-User-Id", userId.toString());

        return apiClient.get(url + "/" + orderId, headers);
    }
}
