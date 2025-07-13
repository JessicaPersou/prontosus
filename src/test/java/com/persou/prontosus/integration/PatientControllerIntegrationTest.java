package com.persou.prontosus.integration;

import com.persou.prontosus.adapters.request.PatientRequest;
import com.persou.prontosus.integration.helpers.AuthHelper;
import com.persou.prontosus.integration.helpers.TestDataHelper;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class PatientControllerIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Deve criar um paciente com sucesso")
    void shouldCreatePatientSuccessfully() {
        String token = AuthHelper.getAdminToken();
        PatientRequest request = TestDataHelper.createValidPatientRequest();

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/patients")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("cpf", equalTo(request.cpf()))
            .body("fullName", equalTo(request.fullName()))
            .body("email", equalTo(request.email()));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao criar paciente com CPF inválido")
    void shouldReturn400WhenCreatingPatientWithInvalidCpf() {
        String token = AuthHelper.getAdminToken();
        PatientRequest request = TestDataHelper.createValidPatientRequest()
            .withCpf("123");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/patients")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"))
            .body("details", hasSize(greaterThan(0)));
    }

    @Test
    @DisplayName("Deve buscar paciente por ID")
    void shouldFindPatientById() {
        String token = AuthHelper.getAdminToken();
        PatientRequest request = TestDataHelper.createValidPatientRequest();

        String patientId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/patients")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/patients/{id}", patientId)
            .then()
            .statusCode(200)
            .body("id", equalTo(patientId))
            .body("cpf", equalTo(request.cpf()))
            .body("fullName", equalTo(request.fullName()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar paciente inexistente")
    void shouldReturn404WhenPatientNotFound() {
        String token = AuthHelper.getAdminToken();

        given()
            .header("Authorization", "Bearer " + token)
            .when()
            .get("/patients/{id}", "nonexistent-id")
            .then()
            .statusCode(404)
            .body("type", equalTo("ResourceNotFound"));
    }

    @Test
    @DisplayName("Deve buscar pacientes por nome")
    void shouldFindPatientsByName() {
        String token = AuthHelper.getAdminToken();
        PatientRequest request = TestDataHelper.createValidPatientRequest()
            .withFullName("Maria Silva Teste");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/patients")
            .then()
            .statusCode(201);

        given()
            .header("Authorization", "Bearer " + token)
            .queryParam("name", "Maria")
            .when()
            .get("/patients/search")
            .then()
            .statusCode(200)
            .body("$", hasSize(greaterThan(0)))
            .body("[0].fullName", containsString("Maria"));
    }

    @Test
    @DisplayName("Deve retornar 401 para requisição sem token")
    void shouldReturn401WithoutToken() {
        PatientRequest request = TestDataHelper.createValidPatientRequest();

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/patients")
            .then()
            .statusCode(401);
    }

    @Test
    @DisplayName("Deve atualizar paciente com sucesso")
    void shouldUpdatePatientSuccessfully() {
        String token = AuthHelper.getAdminToken();
        PatientRequest createRequest = TestDataHelper.createValidPatientRequest();

        String patientId = given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(createRequest)
            .when()
            .post("/patients")
            .then()
            .statusCode(201)
            .extract()
            .path("id");

        PatientRequest updateRequest = createRequest
            .withFullName("Nome Atualizado")
            .withEmail("novoemail@test.com");

        given()
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(updateRequest)
            .when()
            .put("/patients/{id}", patientId)
            .then()
            .statusCode(200)
            .body("id", equalTo(patientId))
            .body("fullName", equalTo("Nome Atualizado"))
            .body("email", equalTo("novoemail@test.com"));
    }
}