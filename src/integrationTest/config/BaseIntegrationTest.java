package com.persou.prontosus;

import com.persou.prontosus.config.TestSecurityConfig;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TestSecurityConfig.class})
public abstract class BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @ServiceConnection
    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("prontosus")
        .withUsername("root")
        .withPassword("root");

    @DynamicPropertySource
    static void setPostgreSQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    static void tearUp() {
        MockServerManager.start();
    }

    @AfterAll
    static void tearDown() {
        MockServerManager.stop();
    }

    @BeforeEach
    void prepare() {
        RestAssured.baseURI = "http://localhost:" + port;
        MockServerManager.clearStubs();
    }

}