package com.persou.prontosus.integration.helpers;

import com.persou.prontosus.adapters.request.LoginRequest;
import com.persou.prontosus.adapters.request.UserRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthHelper {

    public static String createUserAndGetToken(String username, String password, String role) {
        UserRequest userRequest = UserRequest.builder()
            .username(username)
            .password(password)
            .fullName("Test User " + username)
            .email(username + "@test.com")
            .professionalDocument("DOC" + System.currentTimeMillis())
            .role(role)
            .specialty("Test Specialty")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(201);

        return loginAndGetToken(username, password);
    }

    public static String loginAndGetToken(String username, String password) {
        LoginRequest loginRequest = LoginRequest.builder()
            .username(username)
            .password(password)
            .build();

        Response response = given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .response();

        return response.jsonPath().getString("token");
    }

    public static String getAdminToken() {
        return createUserAndGetToken("admin_test", "password123", "ADMIN");
    }

    public static String getDoctorToken() {
        return createUserAndGetToken("doctor_test", "password123", "DOCTOR");
    }

    public static String getNurseToken() {
        return createUserAndGetToken("nurse_test", "password123", "NURSE");
    }
}