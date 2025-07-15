package com.persou.prontosus.integration;

import com.persou.prontosus.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AuthenticationIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldAuthenticateValidUser() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"admin\", \"password\": \"root\"}")
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("user.username", equalTo("admin"));
    }

    @Test
    void shouldRejectInvalidCredentials() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"username\": \"invalid\", \"password\": \"wrong\"}")
            .when()
            .post("/auth/login")
            .then()
            .statusCode(404);
    }
}