package pe.upn.sist1402a;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EnterpriseBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Carga del contexto de Spring Boot")
    void contextLoads() {
    }

    @Test
    @DisplayName("Endpoint de Salud /api/saludo responde 200 OK con 'Backend activo'")
    void testSaludEndpoint() throws Exception {
        mockMvc.perform(get("/api/saludo"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Backend activo")));
    }

    @Test
    @DisplayName("Endpoint de Pacientes devuelve lista y diagnóstico dinámico")
    void testPacientesListar() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].estadoAnemia").exists());
    }

    @Test
    @DisplayName("Bean Validation responde 400 Bad Request estructurado ante datos inválidos")
    void testPacienteValidationBadRequest() throws Exception {
        String invalidJson = """
            {
                "dni": "123",
                "nombre": "",
                "apellido": "",
                "edad": -2,
                "nivelHemoglobina": 0.5
            }
        """;

        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.fieldErrors.dni").exists())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists());
    }

    @Test
    @DisplayName("Endpoint de Productos devuelve lista con estadoStock calculado")
    void testProductosListar() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].estadoStock").exists());
    }

    @Test
    @DisplayName("Búsqueda JPQL con NamedQuery en Productos responde 200 OK")
    void testProductosNamedQuery() throws Exception {
        mockMvc.perform(get("/api/productos/stock").param("minimo", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("Demostración de EntityManager puro (Semana 03) responde 200 OK")
    void testLegacyEntityManager() throws Exception {
        mockMvc.perform(get("/api/legacy/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }
}
