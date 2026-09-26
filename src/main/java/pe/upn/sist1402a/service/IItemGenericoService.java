package pe.upn.sist1402a.service;

import pe.upn.sist1402a.dto.ItemGenericoDto;
import pe.upn.sist1402a.model.ItemGenerico;

import java.util.List;

public interface IItemGenericoService {
    List<ItemGenerico> listarTodos();
    ItemGenerico buscarPorId(Long id);
    ItemGenerico buscarPorCodigoIdentificador(String codigoIdentificador);
    List<ItemGenerico> buscarPorDenominacion(String denominacion);
    List<ItemGenerico> buscarPorValorMinimo(Double valorMinimo);
    ItemGenerico registrar(ItemGenericoDto dto);
    ItemGenerico actualizar(Long id, ItemGenericoDto dto);
    void eliminar(Long id);
}
