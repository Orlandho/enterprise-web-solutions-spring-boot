package pe.upn.sist1402a.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.upn.sist1402a.model.Paciente;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para Paciente (Semanas 04 a 06).
 * Incluye consultas derivadas y consultas JPQL parametrizadas seguras contra inyección SQL.
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDni(String dni);

    List<Paciente> findByApellidoContainingIgnoreCase(String apellido);

    /**
     * Consulta JPQL personalizada con parámetro enlazado con @Param (Regla de seguridad S04).
     */
    @Query("SELECT p FROM Paciente p WHERE p.nivelHemoglobina < :limite ORDER BY p.nivelHemoglobina ASC")
    List<Paciente> buscarConHemoglobinaMenorA(@Param("limite") Double limite);

    @Query("SELECT p FROM Paciente p WHERE p.edad BETWEEN :minEdad AND :maxEdad")
    List<Paciente> buscarPorRangoEdad(@Param("minEdad") Integer minEdad, @Param("maxEdad") Integer maxEdad);
}
