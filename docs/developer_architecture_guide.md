# Guía de Extensión Rápida y Arquitectura de Módulos (Developer Guide)

> **Documento Técnico de Referencia Interna:** 
> Esta guía detalla el flujo de trabajo en cascada, convenciones de diseño y patrones de persistencia dual implementados en el motor backend **Enterprise Web Solutions & Distributed Backend Engine**. Permite a cualquier desarrollador incorporar nuevos módulos o entidades de negocio en menos de 5 a 10 minutos cumpliendo estrictamente los estándares de arquitectura N-Capas.

---

## 1. Estrategia de Incorporación Rápida de Nuevos Módulos

El proyecto incluye una plantilla base completamente desacoplada (`ItemGenerico`) diseñada para clonar o adaptar cualquier modelo de dominio en cuestión de minutos mediante la herramienta de refactorización de símbolos del IDE:

### Procedimiento de Adaptación con Refactorización de Símbolos (`F2`):
1. **Modelo de Dominio:** Abrir `src/main/java/pe/upn/sist1402a/model/ItemGenerico.java`.
 - Seleccionar la clase `ItemGenerico`, presionar `F2` (Rename Symbol) y renombrarla a la nueva entidad (ej. `Vehiculo`, `Cita`, `Activo`). El IDE actualizará automáticamente el nombre de la clase, el nombre de archivo y todas las importaciones del proyecto.
 - Renombrar los atributos según las necesidades del dominio:
 - `codigoIdentificador` -> `codigo` o `placa`
 - `denominacion` -> `descripcion` o `nombre`
 - `valorNumericoPrincipal` -> `monto`, `precio` o `kilometraje`
 - `cantidadEntera` -> `capacidad` o `stock`
 - `clasificacionCalculada` -> regla de negocio dinámica `@Transient`.
2. **Objeto de Transferencia de Datos:** Abrir `src/main/java/pe/upn/sist1402a/dto/ItemGenericoDto.java` y aplicar el mismo procedimiento `F2`.
3. **Capa de Persistencia y Lógica:** Los repositorios, servicios y controladores asociados (`ItemGenericoRepository`, `ItemGenericoService`, `ItemGenericoController`) se actualizarán de forma coordinada.

---

## 2. El Flujo de Construcción en Cascada (De la BD al Controlador)

Cuando se implementa una nueva entidad desde cero, se debe seguir estrictamente este orden cronológico y unidireccional:

```text
[1. Model (@Entity)] -> [2. DTO (@Valid)] -> [3. Repository (JPA/EM)] -> [4. Service (@Service)]
 |
 v
[7. Verificacion] <- [6. data.sql] <- [5. Controller (@RestController)]
```

---

### Paso 1: Capa de Modelo / Entidad JPA (`model/`)
Crea la clase anotada con `@Entity` y `@Table(name = "tabla_en_plural")`:

```java
package pe.upn.sist1402a.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pacientes")
public class Paciente {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, length = 8, unique = true)
 private String dni;

 @Column(nullable = false, length = 60)
 private String nombre;

 @Column(nullable = false, length = 60)
 private String apellido;

 @Column(nullable = false)
 private Integer edad;

 @Column(nullable = false)
 private Double nivelHemoglobina;

 // Campo calculado dinámico en memoria (NO se almacena en la tabla)
 @Transient
 private String estadoAnemia;

 // Constructor sin argumentos obligatorio para JPA y Jackson
 public Paciente() {}

 public Paciente(Long id, String dni, String nombre, String apellido, Integer edad, Double nivelHemoglobina) {
 this.id = id;
 this.dni = dni;
 this.nombre = nombre;
 this.apellido = apellido;
 this.edad = edad;
 this.nivelHemoglobina = nivelHemoglobina;
 }

 public Long getId() { return id; }
 public void setId(Long id) { this.id = id; }
 public String getDni() { return dni; }
 public void setDni(String dni) { this.dni = dni; }
 public String getNombre() { return nombre; }
 public void setNombre(String nombre) { this.nombre = nombre; }
 public String getApellido() { return apellido; }
 public void setApellido(String apellido) { this.apellido = apellido; }
 public Integer getEdad() { return edad; }
 public void setEdad(Integer edad) { this.edad = edad; }
 public Double getNivelHemoglobina() { return nivelHemoglobina; }
 public void setNivelHemoglobina(Double nivelHemoglobina) { this.nivelHemoglobina = nivelHemoglobina; }

 public String getEstadoAnemia() {
 if (this.nivelHemoglobina == null) return "SIN_DATOS";
 return this.nivelHemoglobina < 11.0 ? "ANEMIA" : "NORMAL";
 }

 public void setEstadoAnemia(String estadoAnemia) {
 this.estadoAnemia = estadoAnemia;
 }
}
```

---

### Paso 2: Objeto DTO con Bean Validation (`dto/`)
Crea el DTO para transferir la carga útil de solicitudes entrantes asegurando validación declarativa:

```java
package pe.upn.sist1402a.dto;

import jakarta.validation.constraints.*;

public class PacienteDto {

 @NotBlank(message = "El DNI es obligatorio")
 @Size(min = 8, max = 8, message = "El DNI debe contener exactamente 8 dígitos")
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
 @DecimalMax(value = "25.0", message = "El nivel de hemoglobina no puede superar 25.0")
 private Double nivelHemoglobina;

 public PacienteDto() {}

 // Getters y Setters estándar...
}
```

---

### Paso 3: Capa de Persistencia Dual (`repository/`)

#### Enfoque A: Automatizado con Spring Data JPA
```java
package pe.upn.sist1402a.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.upn.sist1402a.model.Paciente;
import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

 Optional<Paciente> findByDni(String dni);

 // Consulta JPQL parametrizada segura contra inyección SQL
 @Query("SELECT p FROM Paciente p WHERE p.nivelHemoglobina < :limite")
 List<Paciente> buscarConHemoglobinaMenorA(@Param("limite") Double limite);
}
```

#### Enfoque B: Persistencia de Bajo Nivel con EntityManager
```java
package pe.upn.sist1402a.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.model.Paciente;
import java.util.List;
import java.util.Optional;

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
 public List<Paciente> listarTodos() {
 return entityManager.createQuery("SELECT p FROM Paciente p ORDER BY p.id ASC", Paciente.class)
 .getResultList();
 }

 @Transactional(readOnly = true)
 public Optional<Paciente> buscarPorId(Long id) {
 return Optional.ofNullable(entityManager.find(Paciente.class, id));
 }
}
```

---

### Paso 4: Capa de Servicio (`service/`)
Orquesta la lógica de negocio y realiza la conversión entre DTO y Entidad:

```java
package pe.upn.sist1402a.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.dto.PacienteDto;
import pe.upn.sist1402a.model.Paciente;
import pe.upn.sist1402a.repository.PacienteRepository;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PacienteService {

 private final PacienteRepository pacienteRepository;

 public PacienteService(PacienteRepository pacienteRepository) {
 this.pacienteRepository = pacienteRepository;
 }

 @Transactional(readOnly = true)
 public List<Paciente> listarTodos() {
 return pacienteRepository.findAll();
 }

 @Transactional(readOnly = true)
 public Optional<Paciente> buscarPorId(Long id) {
 return pacienteRepository.findById(id);
 }

 public Paciente registrar(PacienteDto dto) {
 Paciente paciente = new Paciente();
 paciente.setDni(dto.getDni());
 paciente.setNombre(dto.getNombre());
 paciente.setApellido(dto.getApellido());
 paciente.setEdad(dto.getEdad());
 paciente.setNivelHemoglobina(dto.getNivelHemoglobina());
 return pacienteRepository.save(paciente);
 }

 public boolean eliminar(Long id) {
 if (pacienteRepository.existsById(id)) {
 pacienteRepository.deleteById(id);
 return true;
 }
 return false;
 }

 @Transactional(readOnly = true)
 public List<Paciente> buscarConAnemia() {
 return pacienteRepository.buscarConHemoglobinaMenorA(11.0);
 }
}
```

---

### Paso 5: Controlador REST (`controller/`)
Expone la API asegurando códigos de estado HTTP correctos (`200`, `201`, `204`, `404`) y soporte de CORS:

```java
package pe.upn.sist1402a.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.dto.PacienteDto;
import pe.upn.sist1402a.model.Paciente;
import pe.upn.sist1402a.service.PacienteService;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class PacienteController {

 private final PacienteService pacienteService;

 public PacienteController(PacienteService pacienteService) {
 this.pacienteService = pacienteService;
 }

 @GetMapping
 public ResponseEntity<List<Paciente>> listarTodos() {
 return ResponseEntity.ok(pacienteService.listarTodos());
 }

 @GetMapping("/{id}")
 public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
 return pacienteService.buscarPorId(id)
 .map(ResponseEntity::ok)
 .orElse(ResponseEntity.notFound().build());
 }

 @PostMapping
 public ResponseEntity<Paciente> registrar(@Valid @RequestBody PacienteDto dto) {
 Paciente creado = pacienteService.registrar(dto);
 return ResponseEntity.status(HttpStatus.CREATED).body(creado);
 }

 @DeleteMapping("/{id}")
 public ResponseEntity<Void> eliminar(@PathVariable Long id) {
 if (pacienteService.eliminar(id)) {
 return ResponseEntity.noContent().build();
 }
 return ResponseEntity.notFound().build();
 }

 @GetMapping("/anemia")
 public ResponseEntity<List<Paciente>> obtenerConAnemia() {
 return ResponseEntity.ok(pacienteService.buscarConAnemia());
 }
}
```

---

### Paso 6: Semillas de Datos (`src/main/resources/data.sql`)
Agrega registros de prueba para garantizar datos inmediatos al iniciar:

```sql
INSERT INTO pacientes (dni, nombre, apellido, edad, nivel_hemoglobina) 
VALUES ('70123456', 'Carlos', 'Mendoza', 5, 9.8);

INSERT INTO pacientes (dni, nombre, apellido, edad, nivel_hemoglobina) 
VALUES ('70987654', 'María', 'Rojas', 4, 12.5);
```

---

### Paso 7: Comandos de Verificación Rápida

```powershell
# 1. Listar todos (200 OK)
Invoke-RestMethod -Uri http://localhost:8080/api/pacientes -Method Get | ConvertTo-Json -Depth 5

# 2. Registrar nuevo (201 Created)
Invoke-RestMethod -Uri http://localhost:8080/api/pacientes -Method Post -ContentType "application/json" -Body '{"dni":"75432109","nombre":"Elena","apellido":"Vargas","edad":3,"nivelHemoglobina":10.5}' | ConvertTo-Json

# 3. Probar validación con error (400 Bad Request estructurado)
try {
 Invoke-RestMethod -Uri http://localhost:8080/api/pacientes -Method Post -ContentType "application/json" -Body '{"dni":"","nombre":"","apellido":"","edad":-1,"nivelHemoglobina":-5.0}'
} catch {
 $_.ErrorDetails.Message
}
```

---

### Paso 8: Pruebas con Visual Studio Code (Thunder Client y requests.http)

1. **Colección Thunder Client:** Importar el archivo `thunder-collection_swad_t1.json` en la extensión Thunder Client de VS Code para acceder a toda la suite de peticiones organizadas por módulo.
2. **Peticiones Interactivas en `requests.http`:** Abrir el archivo `requests.http` en VS Code y hacer clic en `Send Request` sobre cualquier endpoint para inspeccionar status, cabeceras y carga útil JSON en tiempo real.
