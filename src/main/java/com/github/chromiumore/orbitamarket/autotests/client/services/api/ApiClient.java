package com.github.chromiumore.orbitamarket.autotests.client.services.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiClient {
    public Response get(
            String endpoint,
            Map<String, String> headers
    ) {
        return given()
                .log().all()
                .headers(headers)
                .then()
                .log().all()
                .when()
                .get(endpoint);
    }

    public Response post(
            String endpoint,
            Map<String, Object> body,
            Map<String, String> headers
    ) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .headers(headers)
                .then()
                .log().all()
                .when()
                .post(endpoint);
    }

    public Response put(
            String endpoint,
            Map<String, Object> body,
            Map<String, String> headers
    ) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(body)
                .headers(headers)
                .then()
                .log().all()
                .when()
                .put(endpoint);
    }

    public Response delete(
            String endpoint,
            Map<String, String> headers
    ) {
        return given()
                .log().all()
                .headers(headers)
                .then()
                .log().all()
                .when()
                .delete(endpoint);
    }
}
