package pe.upn.sist1402a.service;

import org.springframework.stereotype.Component;

/**
 * Demostración del Patrón de Diseño GoF Factory (Semana 02).
 * Proporciona un punto centralizado desacoplado para resolver y suministrar servicios del dominio.
 */
@Component
public class ServiceFactory {

    private final IPacienteService pacienteService;
    private final IProductoService productoService;
    private final IItemGenericoService itemGenericoService;

    public ServiceFactory(IPacienteService pacienteService,
                          IProductoService productoService,
                          IItemGenericoService itemGenericoService) {
        this.pacienteService = pacienteService;
        this.productoService = productoService;
        this.itemGenericoService = itemGenericoService;
    }

    public Object getService(String dominio) {
        if (dominio == null) {
            throw new IllegalArgumentException("El tipo de dominio no puede ser nulo");
        }
        return switch (dominio.toLowerCase()) {
            case "paciente", "salud" -> pacienteService;
            case "producto", "almacen" -> productoService;
            case "generico", "item", "activo" -> itemGenericoService;
            default -> throw new IllegalArgumentException("Dominio desconocido: " + dominio);
        };
    }

    public IPacienteService getPacienteService() {
        return pacienteService;
    }

    public IProductoService getProductoService() {
        return productoService;
    }

    public IItemGenericoService getItemGenericoService() {
        return itemGenericoService;
    }
}
