# Guia de Arquitectura N-Capas, Extensibilidad del Dominio y Runbook de Desarrollo

> **Estandar de Transparencia Tecnica y Gobernanza de Codigo Abierto:**  
> Este documento constituye el manual oficial de extensibilidad, modelado de dominio y buenas practicas de ingenieria para el proyecto. Proporciona a desarrolladores, contribuidores y auditores tecnicos una guia exhaustiva para extender la plataforma con nuevos modulos de negocio en tiempo record, preservando estrictamente la arquitectura desacoplada en N-Capas, la estrategia de persistencia dual, el blindaje de datos mediante Bean Validation y el manejo centralizado de excepciones.

---

## 1. Modulos de Dominio Disponibles en la Plataforma

La arquitectura base incorpora tres modulos representativos que sirven como referencia y plantilla de diseno:

### Modulo A: Gestion Clinica y Diagnostico Hematologico (`Paciente`)
* **Ubicacion principal:** [`Paciente.java`](../src/main/java/pe/upn/sist1402a/model/Paciente.java)
* **Atributos de dominio:** `dni` (8 digitos unico), `nombre`, `apellido`, `edad`, `nivelHemoglobina`.
* **Regla de negocio en memoria:** Evaluacion dinamica del estado hematologico (`estadoAnemia`) mediante `@Transient`. Si la regla de umbral debe modificarse (por ejemplo, a `< 10.5` o `< 11.0`), se ajusta unicamente la logica del metodo de calculo sin alterar la estructura fisica de la base de datos.

### Modulo B: Control de Inventario y Gestion Logistica (`Producto`)
* **Ubicacion principal:** [`Producto.java`](../src/main/java/pe/upn/sist1402a/model/Producto.java)
* **Atributos de dominio:** `codigo`, `nombre`, `precio`, `stock`, `categoria`.
* **Regla de negocio en memoria:** Evaluacion del umbral de stock critico (`estadoStock`) evaluada dinamicamente con `@Transient` y consultas declarativas `@NamedQuery`.

### Modulo C: Dominio Parametrico Extensible para Nuevas Entidades (`ItemGenerico`)
* **Ubicacion principal:** [`ItemGenerico.java`](../src/main/java/pe/upn/sist1402a/model/ItemGenerico.java)
* **Procedimiento de adaptacion en 5 minutos mediante refactorizacion asistida (F2 en VS Code):**
  1. Abrir [`ItemGenerico.java`](../src/main/java/pe/upn/sist1402a/model/ItemGenerico.java).
  2. Posicionar el cursor sobre el identificador de clase `ItemGenerico`, presionar la tecla **`F2`** e ingresar el nombre de la nueva entidad de negocio requerida (ejemplo: `Vehiculo`, `Activo`, `Consulta`). El entorno refactorizara automaticamente la clase, el archivo fisico y todas las referencias cruzadas.
  3. Renombrar los atributos parametricos mediante `F2`:
     - `codigoIdentificador` -> campo clave (ej. `placa`, `serie` o `codigo`).
     - `denominacion` -> descripcion o nombre (ej. `modelo`, `descripcion`).
     - `valorNumericoPrincipal` -> valor cuantitativo (ej. `kilometraje`, `costo`).
     - `cantidadEntera` -> metrica de conteo o fecha (ej. `anioFabricacion`, `capacidad`).
     - `clasificacionCalculada` -> regla en memoria `@Transient` segun la especificacion.
  4. Replicar el mismo procedimiento en el DTO correspondiente ([`ItemGenericoDto.java`](../src/main/java/pe/upn/sist1402a/dto/ItemGenericoDto.java)).

---

## 2. Flujo Estandar de Construccion en Cascada (De la Base de Datos al Controlador)

Cuando se implementa una nueva entidad desde cero o se agrega una tabla al dominio, se debe seguir estrictamente la siguiente secuencia metodologica unidireccional:

```text
[1. Modelo (@Entity)] -> [2. DTO (@Valid)] -> [3. Repositorio (JPA/EM)] -> [4. Servicio (@Service)]
 |
 v
[7. Verificacion] <----- [6. data.sql] <----- [5. Controlador (@RestController)]
```

---

### Paso 1: Capa de Modelo / Entidad JPA (`model/`)
Definir la entidad anotada con `@Entity` y `@Table(name = "tabla_en_plural")`:

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

    @Column(nullable = false)
    private Double nivelHemoglobina;

    // Regla de diseno: Campo calculado en tiempo de ejecucion (no se mapea a columna fisica)
    @Transient
    private String estadoAnemia;

    // Requisito mandatorio para instanciacion por reflexion (Hibernate y Jackson)
    public Paciente() {}

    // Getters, Setters y metodo de computo dinamico
    public String getEstadoAnemia() {
        if (this.nivelHemoglobina == null) return "SIN_DATOS";
        return this.nivelHemoglobina < 11.0 ? "ANEMIA" : "NORMAL";
    }
}
```

---

### Paso 2: Objeto de Transferencia de Datos con Validacion (`dto/`)
Definir la estructura que desacopla la entrada HTTP del modelo relacional, aplicando restricciones Jakarta Bean Validation:

```java
package pe.upn.sist1402a.dto;

import jakarta.validation.constraints.*;

public class PacienteDto {

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 digitos")
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "La hemoglobina es obligatoria")
    @DecimalMin(value = "1.0", message = "La hemoglobina debe ser mayor a 1.0")
    private Double nivelHemoglobina;

    public PacienteDto() {}
    // Getters y Setters...
}
```

---

### Paso 3: Capa de Persistencia y Acceso a Datos (`repository/`)

#### Opcion A: Interfaz Declarativa Spring Data JPA
```java
package pe.upn.sist1402a.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.upn.sist1402a.model.Paciente;
import java.util.List;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    
    // Consulta derivada automatica por convencion de nombre
    List<Paciente> findByNombreContainingIgnoreCase(String nombre);

    // Consulta JPQL parametrizada segura contra inyeccion SQL
    @Query("SELECT p FROM Paciente p WHERE p.nivelHemoglobina < :limite")
    List<Paciente> buscarConHemoglobinaMenorA(@Param("limite") Double limite);
}
```

#### Opcion B: Persistencia de Bajo Nivel con EntityManager
```java
package pe.upn.sist1402a.repository.manual;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.model.Paciente;
import java.util.List;

@Repository
@Transactional
public class PacienteEntityManagerRepository {

    @PersistenceContext
    private EntityManager em;

    public Paciente guardar(Paciente p) {
        if (p.getId() == null) { 
            em.persist(p); 
            return p; 
        } else { 
            return em.merge(p); 
        }
    }

    public List<Paciente> listarTodos() {
        return em.createQuery("SELECT p FROM Paciente p", Paciente.class).getResultList();
    }
}
```

---

### Paso 4: Capa de Servicio y Logica de Negocio (`service/`)

```java
package pe.upn.sist1402a.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.upn.sist1402a.dto.PacienteDto;
import pe.upn.sist1402a.exception.ResourceNotFoundException;
import pe.upn.sist1402a.model.Paciente;
import pe.upn.sist1402a.repository.springdata.PacienteRepository;
import pe.upn.sist1402a.service.IPacienteService;
import java.util.List;

@Service
@Transactional
public class PacienteServiceImpl implements IPacienteService {

    private final PacienteRepository repo;

    public PacienteServiceImpl(PacienteRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return repo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", id));
    }

    @Override
    public Paciente registrar(PacienteDto dto) {
        Paciente p = new Paciente();
        p.setDni(dto.getDni());
        p.setNombre(dto.getNombre());
        p.setNivelHemoglobina(dto.getNivelHemoglobina());
        return repo.save(p);
    }

    @Override
    public void eliminar(Long id) {
        Paciente p = buscarPorId(id);
        repo.delete(p);
    }
}
```

---

### Paso 5: Controlador REST Semantico (`controller/`)

```java
package pe.upn.sist1402a.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.upn.sist1402a.dto.PacienteDto;
import pe.upn.sist1402a.model.Paciente;
import pe.upn.sist1402a.service.IPacienteService;
import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    private final IPacienteService service;

    public PacienteController(IPacienteService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Paciente> registrar(@Valid @RequestBody PacienteDto dto) {
        return new ResponseEntity<>(service.registrar(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

### Paso 6: Poblado Inicial de Datos (`src/main/resources/data.sql`)
Incluir registros semilla para asegurar disponibilidad inmediata al levantar el perfil de pruebas:

```sql
INSERT INTO pacientes (dni, nombre, apellido, edad, nivel_hemoglobina) VALUES ('70123456', 'Carlos', 'Mendoza', 5, 9.8);
INSERT INTO pacientes (dni, nombre, apellido, edad, nivel_hemoglobina) VALUES ('70987654', 'Maria', 'Rojas', 4, 12.5);
```

---

### Paso 7: Verificacion y Auditoria en Ejecucion

Iniciar la aplicacion desde la terminal:
```powershell
.\mvnw.cmd spring-boot:run
```

Portales de inspeccion disponibles en el navegador:
1. **Frontend Nativo Ligero:** `http://localhost:8080/index.html` (interfaz desacoplada con cliente JavaScript `fetch()` para validaciones visuales).
2. **Documentacion OpenAPI 3 / Swagger UI:** `http://localhost:8080/swagger-ui/index.html`.
3. **Consola H2 Database:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:enterprise_db`, Usuario: `sa`).

---

## 3. Matriz de Referencia de Anotaciones Tecnicas

| Anotacion | Proposito Tecnico y Racional de Arquitectura |
| :--- | :--- |
| `@Transient` | Especifica que el campo **no debe mapearse como columna fisica en la base de datos**. Se calcula dinamicamente en memoria y Jackson lo serializa en el payload JSON. |
| `@PersistenceContext` | Inyecta la instancia del `EntityManager` administrado por el contenedor JPA para operaciones de ciclo de vida manual. |
| `@Transactional` | Define limites de transaccion atomicos (ACID). Cualquier excepcion no controlada desencadena la reversion automatica (*rollback*). |
| `@Valid` | Desencadena el procesamiento de las reglas Jakarta Bean Validation en el `@RequestBody` antes de invocar la logica del metodo. |
| `@RestControllerAdvice` | Interceptor global de excepciones (*AOP*). Centraliza la captura de errores (`MethodArgumentNotValidException`, `ResourceNotFoundException`) y emite respuestas estandarizadas con codigos HTTP 400, 404 y 500. |
| `@CrossOrigin(origins = "*")` | Configura el encabezado CORS para permitir que aplicaciones cliente en otros dominios o puertos consuman el API sin restricciones perimetrales del navegador. |
| `@Query` y `@Param` | Permite definir consultas JPQL personalizadas. El enlace con `:param` y `@Param` es mandatorio para **inmunizar la aplicacion contra inyecciones SQL/JPQL**. |

---

## 4. Fundamentacion Arquitectonica y Preguntas de Auditoria

1. **¿Por que se emplean DTOs en lugar de exponer entidades `@Entity` directamente en los controladores?**  
   *Racional:* Garantiza el desacoplamiento de capas, previene la vulnerabilidad de sobreasignacion masiva (*Mass Assignment*), evita referencias circulares durante la serializacion JSON y protege el esquema de base de datos contra exposiciones no intencionadas.

2. **¿Por que es obligatorio el constructor vacio en entidades JPA?**  
   *Racional:* Tanto el motor ORM (Hibernate) para reconstruir objetos a partir de ResultSets JDBC como la libreria de serializacion (Jackson) requieren instanciar clases mediante reflexion (`Class.getDeclaredConstructor().newInstance()`).

3. **¿Cual es la distincion tecnica entre `EntityManager` y `JpaRepository`?**  
   *Racional:* `EntityManager` es la interfaz estandar y de bajo nivel de JPA que ofrece control granular sobre el ciclo de vida de los estados de persistencia (*transient*, *persistent*, *detached*, *removed*). `JpaRepository` es una abstraccion de alto nivel de Spring Data que autogenera implementaciones CRUD en tiempo de ejecucion mediante interfaces y proxies dinamicos.

4. **¿Por que esta prohibida la concatenacion de literales en consultas JPQL?**  
   *Racional:* Concatenar cadenas de texto en sentencias de consulta introduce vulnerabilidades criticas de inyeccion. Se deben emplear parametros tipados con nombre (`:param`) enlazados via `@Param`.

5. **¿Como se alinea esta infraestructura con el ODS 09 (Industria, Innovacion e Infraestructura)?**  
   *Racional:* El sistema provee una infraestructura digital interoperable, robusta y escalable que reemplaza registros manuales propensos a errores por servicios distribuidos de alta disponibilidad, fortaleciendo la capacidad tecnologica del sector productivo y de servicios.
