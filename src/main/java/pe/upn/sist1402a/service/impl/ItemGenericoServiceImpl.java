package pe.upn.sist1402a.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.dto.ItemGenericoDto;
import pe.upn.sist1402a.exception.ResourceNotFoundException;
import pe.upn.sist1402a.model.ItemGenerico;
import pe.upn.sist1402a.repository.springdata.ItemGenericoRepository;
import pe.upn.sist1402a.service.IItemGenericoService;

import java.util.List;

@Service
@Transactional
public class ItemGenericoServiceImpl implements IItemGenericoService {

    private final ItemGenericoRepository itemRepository;

    public ItemGenericoServiceImpl(ItemGenericoRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemGenerico> listarTodos() {
        return itemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemGenerico buscarPorId(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemGenerico", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemGenerico buscarPorCodigoIdentificador(String codigoIdentificador) {
        return itemRepository.findByCodigoIdentificador(codigoIdentificador)
                .orElseThrow(() -> new ResourceNotFoundException("ItemGenerico", "codigoIdentificador", codigoIdentificador));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemGenerico> buscarPorDenominacion(String denominacion) {
        return itemRepository.findByDenominacionContainingIgnoreCase(denominacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemGenerico> buscarPorValorMinimo(Double valorMinimo) {
        return itemRepository.buscarPorValorMinimo(valorMinimo);
    }

    @Override
    public ItemGenerico registrar(ItemGenericoDto dto) {
        if (itemRepository.findByCodigoIdentificador(dto.getCodigoIdentificador()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un registro con el código: " + dto.getCodigoIdentificador());
        }
        ItemGenerico item = new ItemGenerico();
        item.setCodigoIdentificador(dto.getCodigoIdentificador());
        item.setDenominacion(dto.getDenominacion());
        item.setValorNumericoPrincipal(dto.getValorNumericoPrincipal());
        item.setCantidadEntera(dto.getCantidadEntera());
        return itemRepository.save(item);
    }

    @Override
    public ItemGenerico actualizar(Long id, ItemGenericoDto dto) {
        ItemGenerico item = buscarPorId(id);

        itemRepository.findByCodigoIdentificador(dto.getCodigoIdentificador()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("El código " + dto.getCodigoIdentificador() + " ya está en uso");
            }
        });

        item.setCodigoIdentificador(dto.getCodigoIdentificador());
        item.setDenominacion(dto.getDenominacion());
        item.setValorNumericoPrincipal(dto.getValorNumericoPrincipal());
        item.setCantidadEntera(dto.getCantidadEntera());
        return itemRepository.save(item);
    }

    @Override
    public void eliminar(Long id) {
        ItemGenerico item = buscarPorId(id);
        itemRepository.delete(item);
    }
}
