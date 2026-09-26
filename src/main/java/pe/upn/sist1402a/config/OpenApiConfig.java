package pe.upn.sist1402a.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3 / Swagger UI neutral y académica para la evaluación T1.
 * Sin datos personales ni metadatos comerciales para que sea 100% segura para la entrega académica.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API RESTful - Backend Spring Boot")
                        .version("1.0.0")
                        .description("Documentación interactiva de servicios web y endpoints RESTful desarrollada con Spring Boot y Java 17 LTS."));
    }
}
