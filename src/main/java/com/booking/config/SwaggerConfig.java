package com.booking.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Swagger/OpenAPI configuration.
 *
 * Provides:
 * - API documentation
 * - Interactive Swagger UI
 * - API testing interface
 */
@Configuration
public class SwaggerConfig {

    /*
     * OpenAPI configuration bean.
     */
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                /*
                 * API metadata.
                 */
                .info(new Info()

                        .title("Event Booking API")

                        .version("1.0")

                        .description(
                                "Production Grade Event Booking System"
                        )
                )

                /*
                 * External project documentation.
                 */
                .externalDocs(

                        new ExternalDocumentation()

                                .description(
                                        "Project Documentation"
                                )
                );
    }
}