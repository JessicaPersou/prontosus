package com.persou.prontosus.integration.config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import com.persou.prontosus.integration.config.DatabaseTestConfig.DatabaseHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("logging.level.org.springframework.security", () -> "DEBUG");
    }

    @Autowired
    protected DatabaseHelper databaseHelper;

    protected String adminToken;
    protected String doctorToken;
    protected String nurseToken;

    @BeforeEach
    void setUpBase() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        try {
            databaseHelper.cleanAll();
            databaseHelper.insertTestData();
        } catch (Exception e) {
            createTestUsersViaAPI();
        }

        adminToken = getAuthToken("admin", "password");
        doctorToken = getAuthToken("doctor", "password");
        nurseToken = getAuthToken("nurse", "password");
    }

    private void createTestUsersViaAPI() {
        createUser("admin", "password", "Admin Test", "admin@test.com", "ADMIN123", "ADMIN");
        createUser("doctor", "password", "Dr. Test", "doctor@test.com", "CRM123456", "DOCTOR");
        createUser("nurse", "password", "Nurse Test", "nurse@test.com", "COREN123456", "NURSE");
    }

    private void createUser(String username, String password, String fullName, String email, String document,
                            String role) {
        try {
            given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "username": "%s",
                        "password": "%s",
                        "fullName": "%s",
                        "email": "%s",
                        "professionalDocument": "%s",
                        "role": "%s"
                    }
                    """, username, password, fullName, email, document, role))
                .when()
                .post("/users/register")
                .then()
                .statusCode(anyOf(is(201), is(409)));
        } catch (Exception e) {

        }
    }

    protected String getAuthToken(String username, String password) {
        try {
            return given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "username": "%s",
                        "password": "%s"
                    }
                    """, username, password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao obter token para usuário: " + username, e);
        }
    }

    protected String createTestPatient(String token, String cpf, String fullName) {
        try {
            return given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "cpf": "%s",
                        "fullName": "%s",
                        "birthDate": "1990-01-01",
                        "gender": "MALE"
                    }
                    """, cpf, fullName))
                .when()
                .post("/patients")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao criar paciente de teste", e);
        }
    }

    protected void cleanDatabase() {
        if (databaseHelper != null) {
            databaseHelper.cleanAll();
        }
    }

    protected void cleanSpecificTables(String... tables) {
        if (databaseHelper != null) {
            databaseHelper.cleanTables(tables);
        }
    }

    protected long countRecords(String tableName) {
        return databaseHelper != null ? databaseHelper.countRecords(tableName) : 0L;
    }
}