package com.persou.prontosus.integration.config;

import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;

public class MockServerManager {

    private static ClientAndServer mockServer;

    public static void start() {
        mockServer = ClientAndServer.startClientAndServer(0);
    }

    public static void stop() {
        mockServer.stop();
    }

    public static void clearStubs() {
        mockServer.clear(HttpRequest.request());
    }

    public static String getBaseUrl() {
        return "http://localhost:" + mockServer.getLocalPort();
    }

    public static MockServerClient getMockServerClient() {
        return mockServer;
    }

}