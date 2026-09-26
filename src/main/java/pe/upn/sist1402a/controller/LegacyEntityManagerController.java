package pe.upn.sist1402a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.model.Paciente;
import pe.upn.sist1402a.model.Producto;
import pe.upn.sist1402a.repository.entitymanager.PacienteEntityManagerRepository;
import pe.upn.sist1402a.repository.entitymanager.ProductoEntityManagerRepository;

import java.util.List;

/**
 * Controlador de demostración explícita para la Semana 03.
 * Evidencia el uso manual de EntityManager inyectado con @PersistenceContext
 * sin depender de interfaces JpaRepository.
 */
@RestController
@RequestMapping("/api/legacy")
@CrossOrigin(origins = "*")
@Tag(name = "Demostración Semana 03: EntityManager", description = "Persistencia manual mediante EntityManager puro (@PersistenceContext)")
public class LegacyEntityManagerController {

    private final ProductoEntityManagerRepository productoEmRepository;
    private final PacienteEntityManagerRepository pacienteEmRepository;

    public LegacyEntityManagerController(ProductoEntityManagerRepository productoEmRepository,
                                         PacienteEntityManagerRepository pacienteEmRepository) {
        this.productoEmRepository = productoEmRepository;
        this.pacienteEmRepository = pacienteEmRepository;
    }

    // --- Endpoints de Pacientes con EntityManager (Caso Principal del Curso) ---

    @GetMapping("/pacientes")
    @Operation(summary = "Listar pacientes usando entityManager.createQuery()")
    public ResponseEntity<List<Paciente>> listarPacientesConEntityManager() {
        return ResponseEntity.ok(pacienteEmRepository.listarTodos());
    }

    @GetMapping("/pacientes/{id}")
    @Operation(summary = "Buscar paciente usando entityManager.find()")
    public ResponseEntity<Paciente> buscarPacienteConEntityManager(@PathVariable Long id) {
        return pacienteEmRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/pacientes")
    @Operation(summary = "Guardar paciente usando entityManager.persist()")
    public ResponseEntity<Paciente> guardarPacienteConEntityManager(@RequestBody Paciente paciente) {
        Paciente guardado = pacienteEmRepository.guardar(paciente);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @DeleteMapping("/pacientes/{id}")
    @Operation(summary = "Eliminar paciente usando entityManager.remove()")
    public ResponseEntity<Void> eliminarPacienteConEntityManager(@PathVariable Long id) {
        pacienteEmRepository.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Productos con EntityManager (Caso Secundario de Almacén) ---

    @GetMapping("/productos")
    @Operation(summary = "Listar productos usando entityManager.createQuery()")
    public ResponseEntity<List<Producto>> listarProductosConEntityManager() {
        return ResponseEntity.ok(productoEmRepository.listarTodos());
    }

    @GetMapping("/productos/{id}")
    @Operation(summary = "Buscar producto usando entityManager.find()")
    public ResponseEntity<Producto> buscarProductoConEntityManager(@PathVariable Long id) {
        return productoEmRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/productos")
    @Operation(summary = "Guardar producto usando entityManager.persist()")
    public ResponseEntity<Producto> guardarProductoConEntityManager(@RequestBody Producto producto) {
        Producto guardado = productoEmRepository.guardar(producto);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @DeleteMapping("/productos/{id}")
    @Operation(summary = "Eliminar producto usando entityManager.remove()")
    public ResponseEntity<Void> eliminarProductoConEntityManager(@PathVariable Long id) {
        productoEmRepository.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
