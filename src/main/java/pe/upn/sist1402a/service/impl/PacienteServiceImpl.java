package pe.upn.sist1402a.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.dto.PacienteDto;
import pe.upn.sist1402a.exception.ResourceNotFoundException;
import pe.upn.sist1402a.model.Paciente;
import pe.upn.sist1402a.repository.springdata.PacienteRepository;
import pe.upn.sist1402a.service.IPacienteService;

import java.util.List;

/**
 * Implementación de la capa de servicio para Pacientes.
 * Aplica el patrón GoF Singleton mediante la gestión de beans de Spring (@Service).
 * Garantiza transaccionalidad con @Transactional(rollbackFor = Exception.class).
 */
@Service
@Transactional
public class PacienteServiceImpl implements IPacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente buscarPorDni(String dni) {
        return pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "dni", dni));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> buscarPorApellido(String apellido) {
        return pacienteRepository.findByApellidoContainingIgnoreCase(apellido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> buscarConAnemia() {
        // En niños/población de Virú, < 11.0 g/dL se diagnostica anemia
        return pacienteRepository.buscarConHemoglobinaMenorA(11.0);
    }

    @Override
    public Paciente registrar(PacienteDto dto) {
        if (pacienteRepository.findByDni(dto.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con el DNI: " + dto.getDni());
        }
        Paciente paciente = new Paciente();
        paciente.setDni(dto.getDni());
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setEdad(dto.getEdad());
        paciente.setNivelHemoglobina(dto.getNivelHemoglobina());
        return pacienteRepository.save(paciente);
    }

    @Override
    public Paciente actualizar(Long id, PacienteDto dto) {
        Paciente paciente = buscarPorId(id);

        pacienteRepository.findByDni(dto.getDni()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("El DNI " + dto.getDni() + " ya está asignado a otro paciente");
            }
        });

        paciente.setDni(dto.getDni());
        paciente.setNombre(dto.getNombre());
        paciente.setApellido(dto.getApellido());
        paciente.setEdad(dto.getEdad());
        paciente.setNivelHemoglobina(dto.getNivelHemoglobina());
        return pacienteRepository.save(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = buscarPorId(id);
        pacienteRepository.delete(paciente);
    }
}
