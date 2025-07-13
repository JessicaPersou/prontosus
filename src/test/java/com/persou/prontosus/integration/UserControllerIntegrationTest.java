package com.persou.prontosus.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.persou.prontosus.adapters.request.UserRequest;
import com.persou.prontosus.integration.config.BaseIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterNewDoctorSuccessfully() {
        UserRequest userRequest = UserRequest.builder()
            .username("dr.teste")
            .password("password123")
            .fullName("Dr. Teste Silva")
            .email("dr.teste@test.com")
            .professionalDocument("CRM999999")
            .role("DOCTOR")
            .specialty("Neurologia")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("username", equalTo("dr.teste"))
            .body("fullName", equalTo("Dr. Teste Silva"))
            .body("email", equalTo("dr.teste@test.com"))
            .body("professionalDocument", equalTo("CRM999999"))
            .body("role", equalTo("DOCTOR"))
            .body("specialty", equalTo("Neurologia"))
            .body("active", equalTo(true))
            .body("createdAt", notNullValue())
            .body("updatedAt", notNullValue());
    }

    @Test
    void shouldRegisterNewNurseSuccessfully() {
        UserRequest userRequest = UserRequest.builder()
            .username("enf.teste")
            .password("password123")
            .fullName("Enfermeira Teste")
            .email("enf.teste@test.com")
            .professionalDocument("COREN999999")
            .role("NURSE")
            .specialty("UTI")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(201)
            .body("username", equalTo("enf.teste"))
            .body("role", equalTo("NURSE"))
            .body("specialty", equalTo("UTI"));
    }

    @Test
    void shouldRegisterNewAdminSuccessfully() {
        UserRequest userRequest = UserRequest.builder()
            .username("admin.teste")
            .password("password123")
            .fullName("Admin Teste")
            .email("admin.teste@test.com")
            .professionalDocument("ADM999999")
            .role("ADMIN")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(201)
            .body("username", equalTo("admin.teste"))
            .body("role", equalTo("ADMIN"))
            .body("specialty", equalTo(null));
    }

    @Test
    void shouldFailToRegisterUserWithExistingUsername() {
        UserRequest userRequest = UserRequest.builder()
            .username("admin") // usuário já existe
            .password("password123")
            .fullName("Outro Admin")
            .email("outro.admin@test.com")
            .professionalDocument("ADM888888")
            .role("ADMIN")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(409)
            .body("type", equalTo("ResourceAlreadyExists"))
            .body("message", equalTo("Usuário já existe"));
    }

    @Test
    void shouldFailToRegisterUserWithExistingDocument() {
        UserRequest userRequest = UserRequest.builder()
            .username("novo.doutor")
            .password("password123")
            .fullName("Novo Doutor")
            .email("novo.doutor@test.com")
            .professionalDocument("CRM123456")
            .role("DOCTOR")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(409)
            .body("type", equalTo("ResourceAlreadyExists"))
            .body("message", equalTo("Documento já está cadastrado"));
    }

    @Test
    void shouldFailToRegisterUserWithInvalidRole() {
        UserRequest userRequest = UserRequest.builder()
            .username("usuario.invalido")
            .password("password123")
            .fullName("Usuário Inválido")
            .email("invalido@test.com")
            .professionalDocument("DOC777777")
            .role("INVALID_ROLE")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(400)
            .body("type", equalTo("BusinessError"))
            .body("message", equalTo("Role inválido: INVALID_ROLE. Valores aceitos: DOCTOR, NURSE, ADMIN"));
    }

    @Test
    void shouldFailToRegisterUserWithMissingRequiredFields() {
        UserRequest userRequest = UserRequest.builder()
            .username("incompleto")
            .password("123") // senha muito curta
            .email("email-invalido") // email inválido
            .role("DOCTOR")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"))
            .body("message", equalTo("Erro de validação nos dados enviados"));
    }

    @Test
    void shouldFailToRegisterUserWithShortPassword() {
        UserRequest userRequest = UserRequest.builder()
            .username("senha.curta")
            .password("123") // senha muito curta
            .fullName("Senha Curta")
            .email("senha.curta@test.com")
            .professionalDocument("DOC666666")
            .role("DOCTOR")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"));
    }

    @Test
    void shouldFailToRegisterUserWithInvalidEmail() {
        UserRequest userRequest = UserRequest.builder()
            .username("email.invalido")
            .password("password123")
            .fullName("Email Inválido")
            .email("email-sem-arroba") // email inválido
            .professionalDocument("DOC555555")
            .role("NURSE")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"));
    }

    @Test
    void shouldFailToRegisterUserWithLongUsername() {
        UserRequest userRequest = UserRequest.builder()
            .username("este_username_e_muito_longo_e_deveria_falhar_na_validacao")
            .password("password123")
            .fullName("Username Longo")
            .email("username.longo@test.com")
            .professionalDocument("DOC444444")
            .role("ADMIN")
            .build();

        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
            .when()
            .post("/users/register")
            .then()
            .statusCode(400)
            .body("type", equalTo("ValidationError"));
    }
}