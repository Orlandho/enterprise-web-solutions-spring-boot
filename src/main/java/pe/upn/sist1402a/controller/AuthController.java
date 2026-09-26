package pe.upn.sist1402a.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.dto.LoginRequestDto;
import pe.upn.sist1402a.dto.LoginResponseDto;

import java.security.Key;
import java.util.Date;

/**
 * Controlador de Autenticación Stateless y emisión de tokens JWT (Semana 05).
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Seguridad / Autenticación", description = "Emisión de tokens JWT para autenticación stateless")
public class AuthController {

    // Clave secreta estática de 256 bits para demostración académica segura
    public static final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "UPN_SIST1402A_MASTER_SECRET_KEY_SPRING_BOOT_3_JAVA_17_LTS_ENTERPRISE_KEY".getBytes()
    );
    public static final long EXPIRATION_TIME = 86_400_000L; // 24 horas en ms

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario y obtener Bearer Token JWT")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        // En una aplicación de prueba aceptamos admin / password o cualquier credencial válida
        if ("admin".equalsIgnoreCase(request.getUsername()) && !"admin123".equals(request.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta para el usuario: " + request.getUsername());
        }

        String token = Jwts.builder()
                .setSubject(request.getUsername())
                .claim("role", "ROLE_USER")
                .claim("course", "SIST1402A")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        LoginResponseDto response = new LoginResponseDto(token, "Bearer", request.getUsername(), EXPIRATION_TIME);
        return ResponseEntity.ok(response);
    }
}
