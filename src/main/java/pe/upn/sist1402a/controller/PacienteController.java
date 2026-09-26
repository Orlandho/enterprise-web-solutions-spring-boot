package pe.upn.sist1402a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.dto.PacienteDto;
import pe.upn.sist1402a.model.Paciente;
import pe.upn.sist1402a.service.IPacienteService;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
@Tag(name = "Caso 1: Salud / Pacientes", description = "Gestión de pacientes y diagnóstico dinámico de anemia en Virú")
public class PacienteController {

    private final IPacienteService pacienteService;

    public PacienteController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los pacientes registrados")
    public ResponseEntity<List<Paciente>> listarTodos() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar paciente por número de DNI")
    public ResponseEntity<Paciente> buscarPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(pacienteService.buscarPorDni(dni));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar pacientes por coincidencia de apellido")
    public ResponseEntity<List<Paciente>> buscarPorApellido(@RequestParam String apellido) {
        return ResponseEntity.ok(pacienteService.buscarPorApellido(apellido));
    }

    @GetMapping("/anemia")
    @Operation(summary = "Filtrar pacientes diagnosticados con anemia (hemoglobina < 11.0 g/dL)")
    public ResponseEntity<List<Paciente>> buscarConAnemia() {
        return ResponseEntity.ok(pacienteService.buscarConAnemia());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo paciente (valida Bean Validation y retorna 201 Created)")
    public ResponseEntity<Paciente> registrar(@Valid @RequestBody PacienteDto dto) {
        Paciente nuevo = pacienteService.registrar(dto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un paciente existente por ID")
    public ResponseEntity<Paciente> actualizar(@PathVariable Long id, @Valid @RequestBody PacienteDto dto) {
        Paciente actualizado = pacienteService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar paciente por ID (retorna 204 No Content)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pacienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
