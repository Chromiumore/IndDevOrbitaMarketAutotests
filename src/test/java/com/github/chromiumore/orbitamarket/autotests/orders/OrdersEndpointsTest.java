package com.github.chromiumore.orbitamarket.autotests.orders;

import com.github.chromiumore.orbitamarket.autotests.client.services.OrdersClient;
import com.github.chromiumore.orbitamarket.autotests.client.services.PaymentsClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrdersEndpointsTest {

    @Autowired
    private OrdersClient ordersClient;
    @Autowired
    private PaymentsClient paymentsClient;

    private static UUID TEST_USER_ID;
    private static Long createdOrderId;

    Response createTestAccount(UUID userId) {
        return paymentsClient.createAccount(TEST_USER_ID);
    }

    @BeforeAll
    void setUp() {

        TEST_USER_ID = UUID.randomUUID();

        createTestAccount(TEST_USER_ID).then().statusCode(200);
    }

    @Test
    @Order(1)
    void testCreateOrder() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_type", "ARCHIVE");
        requestBody.put("price", 120);
        Map<String, Object> payload = new HashMap<>();
        payload.put("aoi", "POLYGON((...))");
        payload.put("capture_date", "2024-06-15");
        payload.put("sensor_type", "MSI");
        requestBody.put("payload", payload);

        Response response = ordersClient.createOrder(TEST_USER_ID, requestBody);

        response.then().statusCode(201);

        JsonPath body = response.jsonPath();

        assertEquals("CREATED", body.getString("status"));
        assertEquals("ARCHIVE", body.getString("product_type"));
        assertEquals(120.0, body.getDouble("price"));
        assertNull(body.getString("failure_reason"));
        createdOrderId = body.getLong("order_id");
    }

    @Test
    @Order(2)
    void testGetAllOrders() {
        Response response = ordersClient.getUserOrders(TEST_USER_ID);

        response.then().statusCode(200);

        List<Map<String, Object>> responseBody = response.jsonPath().getList(".");
        Map<String, Object> createdOrder = responseBody.getFirst();

        assertEquals(createdOrderId, ((Integer) createdOrder.get("order_id")).longValue());
        assertEquals("ARCHIVE", createdOrder.get("product_type"));
        assertEquals(120.0, ((Float) createdOrder.get("price")).doubleValue());
        assertNull(createdOrder.get("failure_reason"));
    }

    @Test
    @Order(3)
    void testGetOrder() {
        Response response = ordersClient.getOrder(TEST_USER_ID, createdOrderId);

        response.then().statusCode(200);

        JsonPath responseBody = response.jsonPath();

        assertEquals(createdOrderId, ((Integer) responseBody.get("order_id")).longValue());
        assertEquals("ARCHIVE", responseBody.get("product_type"));
        assertEquals(120.0, ((Float) responseBody.get("price")).doubleValue());
        assertNull(responseBody.get("failure_reason"));
    }
}
