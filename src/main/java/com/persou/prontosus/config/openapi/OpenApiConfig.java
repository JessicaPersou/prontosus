package com.persou.prontosus.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.setExtensions(new HashMap<>());
        return new OpenAPI()
            .info(new Info()
                .title("ProntoSUS API")
                .version("1.0.0")
                .description("Gestão de Prontuários Eletrônicos"));
    }
}