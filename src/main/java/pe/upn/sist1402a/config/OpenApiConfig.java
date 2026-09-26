package pe.upn.sist1402a.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Enterprise Web Solutions & Distributed Backend Architecture API")
                        .version("1.0.0")
                        .description("Modular RESTful Spring Boot 3 Engine developed in Java 17 LTS. " +
                                "Implements decoupled multi-tier architecture, dual-strategy persistence " +
                                "(Spring Data JPA & EntityManager), declarative Bean Validation, " +
                                "centralized exception handling, and stateless security.")
                        .contact(new Contact()
                                .name("Engineering Lead")
                                .url("https://github.com/Orlandho"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
