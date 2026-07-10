package com.github.chromiumore.orbitamarket.autotests.payments;

import com.github.chromiumore.orbitamarket.autotests.client.services.PaymentsClient;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentAccountEndpointsTest {

    public static PaymentsClient paymentsClient;
    public static final Double TEST_AMOUNT_TOP_UP = 500.0;
    private static UUID TEST_USER_ID;

    @BeforeAll
    static void setUp() {
        paymentsClient = new PaymentsClient();

        TEST_USER_ID = UUID.randomUUID();
    }

    @BeforeEach
    void setUpResources() {

    }

    @Test
    @Order(1)
    void testCreateAccount() {
        Response response = paymentsClient.createAccount(TEST_USER_ID);

        response.then().statusCode(200);

        JsonPath body = response.jsonPath();

        assertEquals(TEST_USER_ID.toString(), body.getString("user_id"));
        assertEquals(0, body.getDouble("balance"));
    }

    @Test
    @Order(2)
    void testTopUp() {
        Response response = paymentsClient.topUp(TEST_USER_ID, TEST_AMOUNT_TOP_UP);

        response.then().statusCode(200);

        JsonPath responseBody = response.jsonPath();

        assertEquals(TEST_USER_ID.toString(), responseBody.getString("user_id"));
        assertEquals(TEST_AMOUNT_TOP_UP, responseBody.getDouble("balance"));
        assertEquals("geocredits", responseBody.getString("currency"));
    }

    @Test
    @Order(3)
    void testGetBalance() {
        Response response = paymentsClient.getBalance(TEST_USER_ID);

        response.then().statusCode(200);

        JsonPath responseBody = response.jsonPath();

        assertEquals(TEST_USER_ID.toString(), responseBody.getString("user_id"));
        assertEquals(TEST_AMOUNT_TOP_UP, responseBody.getDouble("balance"));
        assertEquals("geocredits", responseBody.getString("currency"));
    }
}
