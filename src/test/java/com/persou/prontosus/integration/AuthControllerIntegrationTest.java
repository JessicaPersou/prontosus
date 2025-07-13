package com.persou.prontosus.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.persou.prontosus.adapters.request.LoginRequest;
import com.persou.prontosus.integration.config.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("admin")
            .password("password")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("type", equalTo("Bearer"))
            .body("expiresIn", equalTo(86400))
            .body("user.username", equalTo("admin"))
            .body("user.fullName", notNullValue())
            .body("user.email", notNullValue())
            .body("user.role", equalTo("ADMIN"));
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("admin")
            .password("wrongpassword")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(404)
            .body("type", equalTo("ResourceNotFound"))
            .body("message", equalTo("Usuário não autorizado"));
    }

    @Test
    void shouldFailLoginWithNonExistentUser() {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("nonexistent")
            .password("password")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(404)
            .body("type", equalTo("ResourceNotFound"))
            .body("message", equalTo("Usuário não autorizado"));
    }

    @Test
    void shouldFailLoginWithMissingUsername() {
        LoginRequest loginRequest = LoginRequest.builder()
            .password("password")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"))
            .body("message", equalTo("Erro de validação nos dados enviados"));
    }

    @Test
    void shouldFailLoginWithMissingPassword() {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("admin")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"))
            .body("message", equalTo("Erro de validação nos dados enviados"));
    }

}
