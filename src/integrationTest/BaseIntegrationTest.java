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

//    @ServiceConnection
//    @Container
//    private final static PostgresSQL mongoDbContainer = new MongoDBContainer("postgres:15.4");
//            .withDatabaseName("prontosus")
//            .withUsername("prontosus")
//            .withPassword("prontosus");
//
//    @DynamicPropertySource
//    static void setMongoDbProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", mongoDbContainer::getConnectionString);
//        registry.add("feign.client.starWars.url", MockServerManager::getBaseUrl);
//    }

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