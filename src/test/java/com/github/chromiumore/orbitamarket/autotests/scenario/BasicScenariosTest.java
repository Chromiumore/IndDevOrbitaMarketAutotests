package com.github.chromiumore.orbitamarket.autotests.scenario;

import com.github.chromiumore.orbitamarket.autotests.client.kafka.PaymentEventProducer;
import com.github.chromiumore.orbitamarket.autotests.client.services.OrdersClient;
import com.github.chromiumore.orbitamarket.autotests.client.services.PaymentsClient;
import com.github.chromiumore.orbitamarket.autotests.dto.event.PaymentRequestedEvent;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BasicScenariosTest {

    @Autowired
    private OrdersClient ordersClient;
    @Autowired
    private PaymentsClient paymentsClient;
    @Autowired
    private PaymentEventProducer eventProducer;

    private UUID createAccount() {
        UUID userId = UUID.randomUUID();
        paymentsClient.createAccount(userId).then().statusCode(200);
        return userId;
    }

    @Test
    void testHappyPath() {
        Double topUpAmount = 1000.0;
        Double price = 120.0;

        UUID userId = createAccount();

        Response response = paymentsClient.topUp(userId, topUpAmount);
        response.then().statusCode(200);

        JsonPath responseBody = response.jsonPath();
        assertEquals(topUpAmount, responseBody.getDouble("balance"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_type", "ARCHIVE");
        requestBody.put("price", price);
        Map<String, Object> payload = new HashMap<>();
        payload.put("aoi", "POLYGON((...))");
        payload.put("capture_date", "2024-06-15");
        payload.put("sensor_type", "MSI");
        requestBody.put("payload", payload);

        ordersClient.createOrder(userId, requestBody);

        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> paymentsClient.getBalance(userId).jsonPath().getDouble("balance") == topUpAmount - price);
    }

    @Test
    void testNotEnoughBalance() {
        Double topUpAmount = 50.0;
        Double price = 120.0;

        UUID userId = createAccount();

        Response response = paymentsClient.topUp(userId, topUpAmount);
        response.then().statusCode(200);

        JsonPath responseBody = response.jsonPath();
        assertEquals(topUpAmount, responseBody.getDouble("balance"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_type", "ARCHIVE");
        requestBody.put("price", price);
        Map<String, Object> payload = new HashMap<>();
        payload.put("aoi", "POLYGON((...))");
        payload.put("capture_date", "2024-06-15");
        payload.put("sensor_type", "MSI");
        requestBody.put("payload", payload);

        Long orderId = ordersClient.createOrder(userId, requestBody).jsonPath().getLong("order_id");

        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> paymentsClient.getBalance(userId).jsonPath().getDouble("balance") == topUpAmount);

        await().atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofSeconds(1))
                .until(() -> ordersClient.getOrder(userId, orderId).jsonPath().getString("status").equals("PAYMENT_FAILED"));
    }

    @Test
    void testDuplicateOrderIdPaymentEvent() throws InterruptedException {
        Double topUpAmount = 500.0;
        Double price = 100.0;

        UUID userId = createAccount();

        Response response = paymentsClient.topUp(userId, topUpAmount);
        response.then().statusCode(200);

        JsonPath responseBody = response.jsonPath();
        assertEquals(topUpAmount, responseBody.getDouble("balance"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_type", "ARCHIVE");
        requestBody.put("price", price);
        Map<String, Object> payload = new HashMap<>();
        payload.put("aoi", "POLYGON((...))");
        payload.put("capture_date", "2024-06-15");
        payload.put("sensor_type", "MSI");
        requestBody.put("payload", payload);

        Long orderId = ordersClient.createOrder(userId, requestBody).jsonPath().getLong("order_id");

        PaymentRequestedEvent event = new PaymentRequestedEvent(
                UUID.randomUUID(),
                orderId,
                userId,
                price,
                Instant.now()
        );
        eventProducer.sendToKafka(event);

        TimeUnit.SECONDS.sleep(7);
        assertEquals(topUpAmount - price, paymentsClient.getBalance(userId).jsonPath().getDouble("balance"));
    }

    @Test
    void testTwoOrders() throws InterruptedException {
        Double topUpAmount = 1000.0;
        double price = 400.0;

        UUID userId = createAccount();

        Response response = paymentsClient.topUp(userId, topUpAmount);
        response.then().statusCode(200);

        JsonPath responseBody = response.jsonPath();
        assertEquals(topUpAmount, responseBody.getDouble("balance"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("product_type", "ARCHIVE");
        requestBody.put("price", price);
        Map<String, Object> payload = new HashMap<>();
        payload.put("aoi", "POLYGON((...))");
        payload.put("capture_date", "2024-06-15");
        payload.put("sensor_type", "MSI");
        requestBody.put("payload", payload);

        ordersClient.createOrder(userId, requestBody);
        ordersClient.createOrder(userId, requestBody);

        TimeUnit.SECONDS.sleep(7);

        assertEquals(topUpAmount - price * 2, paymentsClient.getBalance(userId).jsonPath().getDouble("balance"));
    }

    @Test
    void testDuplicateCreateAccount() {
        UUID userId = UUID.randomUUID();
        Response response = paymentsClient.createAccount(userId);
        Long accountId = response.jsonPath().getLong("id");

        response = paymentsClient.topUp(userId, 500.0);

        response = paymentsClient.createAccount(userId);
        JsonPath responseBody = response.jsonPath();

        assertEquals(userId.toString(), responseBody.getString("user_id"));
        assertEquals(accountId, responseBody.getLong("id"));
        assertEquals(500.0, responseBody.getDouble("balance"));
    }
}
