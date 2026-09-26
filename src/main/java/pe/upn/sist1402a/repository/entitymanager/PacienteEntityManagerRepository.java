package pe.upn.sist1402a.repository.entitymanager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.model.Paciente;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio manual con EntityManager para Pacientes (Semana 03).
 */
@Repository
@Transactional
public class PacienteEntityManagerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Paciente guardar(Paciente paciente) {
        if (paciente.getId() == null) {
            entityManager.persist(paciente);
            return paciente;
        } else {
            return entityManager.merge(paciente);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Long id) {
        Paciente paciente = entityManager.find(Paciente.class, id);
        return Optional.ofNullable(paciente);
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return entityManager.createQuery("SELECT p FROM Paciente p ORDER BY p.id ASC", Paciente.class)
                .getResultList();
    }

    public void eliminar(Long id) {
        Paciente paciente = entityManager.find(Paciente.class, id);
        if (paciente != null) {
            entityManager.remove(paciente);
        }
    }
}
