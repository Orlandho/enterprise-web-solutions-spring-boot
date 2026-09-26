package pe.upn.sist1402a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.dto.ItemGenericoDto;
import pe.upn.sist1402a.model.ItemGenerico;
import pe.upn.sist1402a.service.IItemGenericoService;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
@Tag(name = "Caso 3: Comodín / Activos ODS 9", description = "Plantilla comodín multipropósito para examen T1")
public class ItemGenericoController {

    private final IItemGenericoService itemService;

    public ItemGenericoController(IItemGenericoService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los ítems registrados")
    public ResponseEntity<List<ItemGenerico>> listarTodos() {
        return ResponseEntity.ok(itemService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ítem por ID")
    public ResponseEntity<ItemGenerico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar ítem por código identificador")
    public ResponseEntity<ItemGenerico> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(itemService.buscarPorCodigoIdentificador(codigo));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar ítems por coincidencia de denominación")
    public ResponseEntity<List<ItemGenerico>> buscarPorDenominacion(@RequestParam String valor) {
        return ResponseEntity.ok(itemService.buscarPorDenominacion(valor));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo ítem (retorna 201 Created)")
    public ResponseEntity<ItemGenerico> registrar(@Valid @RequestBody ItemGenericoDto dto) {
        ItemGenerico nuevo = itemService.registrar(dto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ítem existente")
    public ResponseEntity<ItemGenerico> actualizar(@PathVariable Long id, @Valid @RequestBody ItemGenericoDto dto) {
        ItemGenerico actualizado = itemService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ítem por ID (retorna 204 No Content)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        itemService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
