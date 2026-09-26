package pe.upn.sist1402a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador de prueba de salud del backend (Semana 01 y 06).
 * Verifica que el servidor Spring Boot está activo y respondiendo.
 */
@RestController
@RequestMapping("/api/saludo")
@CrossOrigin(origins = "*")
@Tag(name = "Health Check / Estado del Sistema", description = "Endpoints de verificación de estado y conectividad")
public class SaludController {

    @GetMapping
    @Operation(summary = "Verificar estado del backend", description = "Retorna el mensaje estándar 'Backend activo'")
    public ResponseEntity<String> saludoTexto() {
        return ResponseEntity.ok("Backend activo");
    }

    @GetMapping("/detallado")
    @Operation(summary = "Verificar metadatos del backend en formato JSON")
    public ResponseEntity<Map<String, Object>> saludoJson() {
        return ResponseEntity.ok(Map.of(
                "estado", "Backend activo",
                "framework", "Spring Boot 3.2.5",
                "javaVersion", "Java 17 LTS",
                "arquitectura", "N-Capas con Persistencia Dual (Spring Data JPA + EntityManager)",
                "odsAlineado", "ODS 09: Industria, Innovación e Infraestructura"
        ));
    }
}
