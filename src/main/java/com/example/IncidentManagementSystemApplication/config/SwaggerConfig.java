package com.example.IncidentManagementSystemApplication.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Incident Management System API")
                .version("1.0.0")
                .description("API documentation for the Incident Management System. This documentation provides details about all available endpoints, request/response models, and authentication requirements.")
            );
    }
}

