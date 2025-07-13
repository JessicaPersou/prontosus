package com.persou.prontosus.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.persou.prontosus.adapters.request.AppointmentRequest;
import com.persou.prontosus.integration.config.BaseIntegrationTest;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

class AppointmentControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldFailToCreateAppointmentWithInvalidPatient() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);

        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
            .patientId("nonexistent-patient-id")
            .healthcareProfessionalId("f8bd326c-e8e3-5ef4-974b-c6c393ged4f4")
            .scheduledDateTime(futureDate)
            .status("SCHEDULED")
            .type("CONSULTATION")
            .reason("Consulta com paciente inválido")
            .build();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + doctorToken)
            .body(appointmentRequest)
            .when()
            .post("/appointments")
            .then()
            .statusCode(404)
            .body("type", equalTo("ResourceNotFound"));
    }

    @Test
    void shouldFailToCreateAppointmentWithInvalidProfessional() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);

        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
            .patientId("p1a2b3c4-d5e6-7f8g-9h0i-j1k2l3m4n5o6")
            .healthcareProfessionalId("nonexistent-professional-id")
            .scheduledDateTime(futureDate)
            .status("SCHEDULED")
            .type("CONSULTATION")
            .reason("Consulta com profissional inválido")
            .build();

        given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + doctorToken)
            .body(appointmentRequest)
            .when()
            .post("/appointments")
            .then()
            .statusCode(404)
            .body("type", equalTo("ResourceNotFound"));
    }


    @Test
    void shouldGetAppointmentsByStatus() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now().plusDays(30);

        given()
            .header("Authorization", "Bearer " + doctorToken)
            .queryParam("start", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .queryParam("end", end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .when()
            .get("/appointments/status/SCHEDULED")
            .then()
            .statusCode(200);
    }

}
