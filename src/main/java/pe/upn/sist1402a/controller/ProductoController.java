package pe.upn.sist1402a.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.dto.ProductoDto;
import pe.upn.sist1402a.model.Producto;
import pe.upn.sist1402a.service.IProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
@Tag(name = "Caso 2: Almacén / Productos", description = "CRUD de inventario, stock crítico, JPQL y Named Queries")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos en inventario")
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por ID")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar producto por código SKU")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(productoService.buscarPorCodigo(codigo));
    }

    @GetMapping("/categoria")
    @Operation(summary = "Filtrar productos por categoría (consulta derivada)")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorCategoria(nombre));
    }

    @GetMapping("/precio")
    @Operation(summary = "Filtrar productos por rango de precio (consulta JPQL con @Query)")
    public ResponseEntity<List<Producto>> buscarPorRangoPrecio(
            @RequestParam Double minimo, @RequestParam Double maximo) {
        return ResponseEntity.ok(productoService.buscarPorRangoPrecio(minimo, maximo));
    }

    @GetMapping("/stock")
    @Operation(summary = "Buscar productos con stock mínimo (utiliza @NamedQuery)")
    public ResponseEntity<List<Producto>> buscarConStockMinimo(@RequestParam Integer minimo) {
        return ResponseEntity.ok(productoService.buscarConStockMinimo(minimo));
    }

    @GetMapping("/insuficientes")
    @Operation(summary = "Listar productos con stock insuficiente (stock < 5)")
    public ResponseEntity<List<Producto>> buscarStockInsuficiente() {
        return ResponseEntity.ok(productoService.buscarStockInsuficiente());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo producto (retorna 201 Created)")
    public ResponseEntity<Producto> registrar(@Valid @RequestBody ProductoDto dto) {
        Producto nuevo = productoService.registrar(dto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un producto existente")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDto dto) {
        Producto actualizado = productoService.actualizar(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto por ID (retorna 204 No Content)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
