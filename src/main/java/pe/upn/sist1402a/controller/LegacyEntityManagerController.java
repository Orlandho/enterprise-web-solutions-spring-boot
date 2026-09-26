package pe.upn.sist1402a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.model.Producto;
import pe.upn.sist1402a.repository.entitymanager.ProductoEntityManagerRepository;

import java.util.List;

/**
 * Controlador de demostración explícita para la Semana 03.
 * Evidencia el uso manual de EntityManager inyectado con @PersistenceContext
 * sin depender de interfaces JpaRepository.
 */
@RestController
@RequestMapping("/api/legacy/productos")
@CrossOrigin(origins = "*")
@Tag(name = "Demostración Semana 03: EntityManager", description = "Persistencia manual mediante EntityManager puro (@PersistenceContext)")
public class LegacyEntityManagerController {

    private final ProductoEntityManagerRepository emRepository;

    public LegacyEntityManagerController(ProductoEntityManagerRepository emRepository) {
        this.emRepository = emRepository;
    }

    @GetMapping
    @Operation(summary = "Listar productos usando entityManager.createQuery()")
    public ResponseEntity<List<Producto>> listarConEntityManager() {
        return ResponseEntity.ok(emRepository.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto usando entityManager.find()")
    public ResponseEntity<Producto> buscarConEntityManager(@PathVariable Long id) {
        return emRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Guardar producto usando entityManager.persist()")
    public ResponseEntity<Producto> guardarConEntityManager(@RequestBody Producto producto) {
        Producto guardado = emRepository.guardar(producto);
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto usando entityManager.remove()")
    public ResponseEntity<Void> eliminarConEntityManager(@PathVariable Long id) {
        emRepository.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
