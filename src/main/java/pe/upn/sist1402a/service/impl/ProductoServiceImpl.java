package pe.upn.sist1402a.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.dto.ProductoDto;
import pe.upn.sist1402a.exception.ResourceNotFoundException;
import pe.upn.sist1402a.model.Producto;
import pe.upn.sist1402a.repository.springdata.ProductoRepository;
import pe.upn.sist1402a.service.IProductoService;

import java.util.List;

@Service
@Transactional
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "codigo", codigo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaIgnoreCase(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorRangoPrecio(Double minimo, Double maximo) {
        return productoRepository.buscarPorRangoPrecio(minimo, maximo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarConStockMinimo(Integer stockMinimo) {
        return productoRepository.buscarConStockMinimo(stockMinimo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarStockInsuficiente() {
        return productoRepository.findByStockLessThan(5);
    }

    @Override
    public Producto registrar(ProductoDto dto) {
        if (productoRepository.findByCodigo(dto.getCodigo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con el código: " + dto.getCodigo());
        }
        Producto p = new Producto();
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setCategoria(dto.getCategoria());
        return productoRepository.save(p);
    }

    @Override
    public Producto actualizar(Long id, ProductoDto dto) {
        Producto p = buscarPorId(id);

        productoRepository.findByCodigo(dto.getCodigo()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("El código " + dto.getCodigo() + " ya está asignado a otro producto");
            }
        });

        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setCategoria(dto.getCategoria());
        return productoRepository.save(p);
    }

    @Override
    public void eliminar(Long id) {
        Producto p = buscarPorId(id);
        productoRepository.delete(p);
    }
}
