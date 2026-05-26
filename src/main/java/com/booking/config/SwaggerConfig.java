package com.booking.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                .title("Event Booking API")
                .version("1.0")
                .description("Production Grade Event Booking System"))
                .externalDocs(new ExternalDocumentation()
                .description("Project Documentation"));
    }
}
