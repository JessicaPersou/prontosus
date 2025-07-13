package com.persou.prontosus.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import com.persou.prontosus.integration.config.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

class PatientControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldCreatePatientSuccessfully() {
        String patientJson = """
            {
                "cpf": "12345678901",
                "fullName": "João Silva",
                "birthDate": "1990-01-01",
                "gender": "MALE",
                "phoneNumber": "11987654321",
                "email": "joao@email.com",
                "address": {
                    "zipCode": "01310100",
                    "street": "Avenida Paulista",
                    "number": "1000",
                    "city": "São Paulo",
                    "state": "SP"
                },
                "emergencyContactName": "Maria Silva",
                "emergencyContactPhone": "11976543210"
            }
            """;

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(patientJson)
            .when()
            .post("/patients")
            .then()
            .statusCode(201)
            .body("cpf", equalTo("12345678901"))
            .body("fullName", equalTo("João Silva"))
            .body("gender", equalTo("MALE"))
            .body("email", equalTo("joao@email.com"))
            .body("id", notNullValue())
            .body("createdAt", notNullValue());
    }

    @Test
    void shouldReturnBadRequestWhenCpfIsInvalid() {
        String invalidPatientJson = """
            {
                "cpf": "123",
                "fullName": "João Silva",
                "birthDate": "1990-01-01",
                "gender": "MALE"
            }
            """;

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(invalidPatientJson)
            .when()
            .post("/patients")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"));
    }

    @Test
    void shouldReturnConflictWhenCpfAlreadyExists() {
        String patientJson = """
            {
                "cpf": "98765432100",
                "fullName": "Maria Santos",
                "birthDate": "1985-05-15",
                "gender": "FEMALE"
            }
            """;

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(patientJson)
            .when()
            .post("/patients")
            .then()
            .statusCode(201);

        String duplicatePatientJson = """
            {
                "cpf": "98765432100",
                "fullName": "Outro Nome",
                "birthDate": "1990-01-01",
                "gender": "MALE"
            }
            """;

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(duplicatePatientJson)
            .when()
            .post("/patients")
            .then()
            .statusCode(409)
            .body("type", equalTo("ResourceAlreadyExists"));
    }

    @Test
    void shouldFindAllPatients() {
        createTestPatient(adminToken, "11111111111", "Patient One");
        createTestPatient(adminToken, "22222222222", "Patient Two");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .when()
            .get("/patients")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    void shouldFindPatientById() {
        String patientId = createTestPatient(adminToken, "33333333333", "Test Patient");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .when()
            .get("/patients/{id}", patientId)
            .then()
            .statusCode(200)
            .body("id", equalTo(patientId))
            .body("cpf", equalTo("33333333333"))
            .body("fullName", equalTo("Test Patient"));
    }

    @Test
    void shouldReturnNotFoundWhenPatientDoesNotExist() {
        given()
            .header("Authorization", "Bearer " + adminToken)
            .when()
            .get("/patients/{id}", "nonexistent-id")
            .then()
            .statusCode(404)
            .body("type", equalTo("ResourceNotFound"));
    }

    @Test
    void shouldFindPatientByCpf() {
        createTestPatient(adminToken, "44444444444", "CPF Test Patient");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .when()
            .get("/patients/cpf/{cpf}", "44444444444")
            .then()
            .statusCode(200)
            .body("cpf", equalTo("44444444444"))
            .body("fullName", equalTo("CPF Test Patient"));
    }

    @Test
    void shouldReturnNotFoundWhenCpfDoesNotExist() {
        given()
            .header("Authorization", "Bearer " + adminToken)
            .when()
            .get("/patients/cpf/{cpf}", "99999999999")
            .then()
            .statusCode(404)
            .body("type", equalTo("ResourceNotFound"));
    }

    @Test
    void shouldSearchPatientsByName() {
        createTestPatient(adminToken, "55555555555", "João da Silva");
        createTestPatient(adminToken, "66666666666", "João dos Santos");

        given()
            .header("Authorization", "Bearer " + adminToken)
            .queryParam("name", "João")
            .when()
            .get("/patients/search")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(2))
            .body("fullName", everyItem(containsStringIgnoringCase("João")));
    }

    @Test
    void shouldUpdatePatientSuccessfully() {
        String patientId = createTestPatient(adminToken, "77777777777", "Original Name");

        String updateJson = """
            {
                "cpf": "77777777777",
                "fullName": "Updated Name",
                "birthDate": "1990-01-01",
                "gender": "MALE",
                "phoneNumber": "11999999999",
                "email": "updated@email.com"
            }
            """;

        given()
            .header("Authorization", "Bearer " + adminToken)
            .contentType(ContentType.JSON)
            .body(updateJson)
            .when()
            .put("/patients/{id}", patientId)
            .then()
            .statusCode(200)
            .body("id", equalTo(patientId))
            .body("fullName", equalTo("Updated Name"))
            .body("phoneNumber", equalTo("11999999999"))
            .body("email", equalTo("updated@email.com"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoToken() {
        String patientJson = """
            {
                "cpf": "88888888888",
                "fullName": "Unauthorized Test",
                "birthDate": "1990-01-01",
                "gender": "MALE"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(patientJson)
            .when()
            .post("/patients")
            .then()
            .statusCode(403);
    }

    @Test
    void shouldReturnUnauthorizedWhenInvalidToken() {
        String patientJson = """
            {
                "cpf": "99999999999",
                "fullName": "Invalid Token Test",
                "birthDate": "1990-01-01",
                "gender": "MALE"
            }
            """;

        given()
            .header("Authorization", "Bearer invalid-token")
            .contentType(ContentType.JSON)
            .body(patientJson)
            .when()
            .post("/patients")
            .then()
            .statusCode(403);
    }

    @Test
    void shouldAllowDoctorAndNurseToAccessPatients() {
        given()
            .header("Authorization", "Bearer " + doctorToken)
            .when()
            .get("/patients")
            .then()
            .statusCode(200);


        given()
            .header("Authorization", "Bearer " + nurseToken)
            .when()
            .get("/patients")
            .then()
            .statusCode(200);
    }
}