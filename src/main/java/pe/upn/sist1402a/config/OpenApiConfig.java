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
                        .title("Sistema de Gestión de Pacientes y Detección de Anemia - API REST")
                        .version("1.0.0")
                        .description("Solución backend modular desarrollada con Spring Boot 3 y Java 17 LTS " +
                                "para la Evaluación T1 de Soluciones Web y Aplicaciones Distribuidas (SIST1402A) - UPN. " +
                                "Implementa arquitectura N-Capas, persistencia dual (Spring Data JPA y EntityManager), " +
                                "Bean Validation declarativo, manejo global estructurado de excepciones y seguridad stateless."));
    }
}
