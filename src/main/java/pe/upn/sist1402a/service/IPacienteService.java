package pe.upn.sist1402a.service;

import pe.upn.sist1402a.dto.PacienteDto;
import pe.upn.sist1402a.model.Paciente;

import java.util.List;

public interface IPacienteService {
    List<Paciente> listarTodos();
    Paciente buscarPorId(Long id);
    Paciente buscarPorDni(String dni);
    List<Paciente> buscarPorApellido(String apellido);
    List<Paciente> buscarConAnemia();
    Paciente registrar(PacienteDto dto);
    Paciente actualizar(Long id, PacienteDto dto);
    void eliminar(Long id);
}
