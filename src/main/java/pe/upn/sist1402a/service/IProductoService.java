package pe.upn.sist1402a.service;

import pe.upn.sist1402a.dto.ProductoDto;
import pe.upn.sist1402a.model.Producto;

import java.util.List;

public interface IProductoService {
    List<Producto> listarTodos();
    Producto buscarPorId(Long id);
    Producto buscarPorCodigo(String codigo);
    List<Producto> buscarPorCategoria(String categoria);
    List<Producto> buscarPorRangoPrecio(Double minimo, Double maximo);
    List<Producto> buscarConStockMinimo(Integer stockMinimo);
    List<Producto> buscarStockInsuficiente();
    Producto registrar(ProductoDto dto);
    Producto actualizar(Long id, ProductoDto dto);
    void eliminar(Long id);
}
