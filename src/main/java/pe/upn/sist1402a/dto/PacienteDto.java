package pe.upn.sist1402a.dto;

import jakarta.validation.constraints.*;

public class PacienteDto {

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe contener exactamente 8 dígitos")
    @Pattern(regexp = "\\d+", message = "El DNI debe contener solo números")
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 60, message = "El nombre debe tener entre 2 y 60 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 60, message = "El apellido debe tener entre 2 y 60 caracteres")
    private String apellido;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 0, message = "La edad no puede ser menor a 0")
    @Max(value = 120, message = "La edad no puede ser mayor a 120")
    private Integer edad;

    @NotNull(message = "El nivel de hemoglobina es obligatorio")
    @DecimalMin(value = "1.0", message = "El nivel de hemoglobina debe ser al menos 1.0")
    @DecimalMax(value = "25.0", message = "El nivel de hemoglobina no puede exceder 25.0")
    private Double nivelHemoglobina;

    public PacienteDto() {
    }

    public PacienteDto(String dni, String nombre, String apellido, Integer edad, Double nivelHemoglobina) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.nivelHemoglobina = nivelHemoglobina;
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
}
