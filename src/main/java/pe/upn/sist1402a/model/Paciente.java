package pe.upn.sist1402a.model;

import jakarta.persistence.*;

/**
 * Entidad JPA para el Caso 1: Salud / Posta Médica Virú (Prevención de Anemia).
 * Incluye atributo calculado @Transient para diagnóstico dinámico según hemoglobina.
 */
@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dni", nullable = false, length = 8, unique = true)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 60)
    private String apellido;

    @Column(name = "edad", nullable = false)
    private Integer edad;

    @Column(name = "nivel_hemoglobina", nullable = false)
    private Double nivelHemoglobina;

    /**
     * Atributo dinámico calculado en memoria.
     * @Transient instruye a Hibernate/JPA para que NO persista este valor en la base de datos,
     * pero Jackson lo serializa automáticamente en la respuesta JSON.
     */
    @Transient
    private String estadoAnemia;

    // Constructor vacío por defecto obligatorio para serialización/deserialización Jackson y JPA
    public Paciente() {
    }

    public Paciente(Long id, String dni, String nombre, String apellido, Integer edad, Double nivelHemoglobina) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.nivelHemoglobina = nivelHemoglobina;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Double getNivelHemoglobina() {
        return nivelHemoglobina;
    }

    public void setNivelHemoglobina(Double nivelHemoglobina) {
        this.nivelHemoglobina = nivelHemoglobina;
    }

    public String getEstadoAnemia() {
        if (this.nivelHemoglobina == null) {
            return "SIN_DATOS";
        }
        return this.nivelHemoglobina < 11.0 ? "ANEMIA" : "NORMAL";
    }

    public void setEstadoAnemia(String estadoAnemia) {
        this.estadoAnemia = estadoAnemia;
    }
}
