# Guia de Verificacion de Integracion de API (Thunder Client, VS Code REST Client y cURL)

> **Proposito de Aseguramiento de Calidad (QA) y Transparencia:**  
> Este documento detalla los procedimientos estandarizados de prueba y verificacion de contratos REST para auditar el funcionamiento del backend en tiempo real. Proporciona instrucciones reproducibles tanto para evaluadores tecnicos y auditores de arquitectura como para desarrolladores que validan integraciones mediante **Thunder Client** en Visual Studio Code, clientes HTTP basados en RFC-7230 y comandos de terminal cURL.

---

## 1. Herramientas Soportadas para la Verificacion

El repositorio incluye soporte nativo y preconfigurado para tres metodologias de verificacion:

1. **Thunder Client en Visual Studio Code:** Interfaz grafica ligera para ejecucion visual e interactiva sin necesidad de software pesado externo.
2. **Archivo de Especificacion RFC-7230 (`docs/api-contracts.http`):** Permite ejecutar peticiones directamente desde el editor de codigo con la extension REST Client o Thunder Client.
3. **Coleccion Preconstruida (`docs/thunder-collection.json`):** Suite completa organizada por modulos funcionales.

---

## 2. Anatomia de la Interfaz de Prueba en VS Code (Thunder Client)

Al abrir la vista de Thunder Client en la barra lateral izquierda de VS Code y presionar **`New Request`**, se presenta el siguiente panel operativo:

```text
+-----------------------------------------------------------------------------------------+
| [GET v] [ http://localhost:8080/api/pacientes                                  ] [SEND] |
+-----------------------------------------------------------------------------------------+
| [Query]  [Headers]  [Auth]  [Body]  [Tests]                                             |
|                                                                                         |
|  (Configuracion de la peticion segun la pestana activa)                                 |
+-----------------------------------------------------------------------------------------+
| PANEL DERECHO DE RESPUESTA:                                                             |
| Status: 200 OK  |  Time: 18 ms  |  Size: 520 B                                          |
| [Response JSON estructurado, formateado y coloreado]                                    |
+-----------------------------------------------------------------------------------------+
```

Componentes principales:
1. **Selector de Metodo HTTP:** Desplegable para seleccionar `GET`, `POST`, `PUT`, `DELETE`.
2. **Caja de URL del Endpoint:** Direccion base del servicio (`http://localhost:8080/api/...`).
3. **Boton Send:** Dispara la transmision TCP/IP hacia el servidor Spring Boot.
4. **Pestanas de Configuracion:**
   - **`Body`:** Para enviar cargas utiles JSON en metodos `POST` y `PUT`.
   - **`Headers`:** Para especificar `Content-Type: application/json` o encabezados personalizados.
   - **`Query`:** Para parametros de filtrado en la URL (`?minimo=5`).
   - **`Auth`:** Para configurar cabeceras `Authorization: Bearer <token>`.
5. **Panel de Diagnostico y Respuesta:** Muestra el codigo de estado HTTP devuelto (`200 OK`, `201 Created`, `400 Bad Request`, `401 Unauthorized`), tiempo de respuesta y el cuerpo JSON.

---

## 3. Matriz de Casos de Prueba y Procedimientos de Verificacion

---

### Caso 1: Healthcheck y Verificacion de Disponibilidad (GET)
*Rutas auditadas:* `/api/saludo`, `/api/saludo/detallado`

1. Crear una peticion con metodo **`GET`**.
2. URL:
   ```text
   http://localhost:8080/api/saludo/detallado
   ```
3. Presionar **`Send`**.
4. **Criterio de exito:** Codigo HTTP `200 OK` con metadata de version, estado del servicio y alineacion de infraestructura.

---

### Caso 2: Registro de Entidades y Persistencia Relacional (POST con JSON)
*Rutas auditadas:* `/api/pacientes`, `/api/productos`, `/api/items`

1. Configurar el metodo en **`POST`**.
2. URL:
   ```text
   http://localhost:8080/api/pacientes
   ```
3. Seleccionar la pestana **`Body`** y marcar el formato **`JSON`**.
4. Cargar el payload estandar disponible en [`docs/payloads/02_entity_payload_standard.json`](payloads/02_entity_payload_standard.json):
   ```json
   {
     "dni": "12345678",
     "nombre": "Orlando",
     "apellido": "Dorival",
     "edad": 67,
     "nivelHemoglobina": 67.6
   }
   ```
5. Presionar **`Send`**.
6. **Criterio de exito:**
   - Codigo HTTP `201 Created`.
   - Cuerpo de respuesta con `id` incremental autogenerado por la base de datos y campo calculado en memoria `@Transient` (`"estadoAnemia": "NORMAL"`).

---

### Caso 3: Validacion Declarativa y Manejo Global de Errores (400 Bad Request)
*Objetivo:* Auditar la respuesta del interceptor `@RestControllerAdvice` ante violaciones de restricciones Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`).

1. Configurar la peticion en **`POST`**.
2. URL:
   ```text
   http://localhost:8080/api/pacientes
   ```
3. En la pestana **`Body` -> `JSON`**, cargar el payload invalido de [`docs/payloads/04_entity_payload_validation_violation.json`](payloads/04_entity_payload_validation_violation.json):
   ```json
   {
     "dni": "123",
     "nombre": "",
     "apellido": "",
     "edad": -5,
     "nivelHemoglobina": -2.0
   }
   ```
4. Presionar **`Send`**.
5. **Criterio de exito:**
   - Codigo HTTP `400 Bad Request`.
   - Objeto JSON estructurado conteniendo el mapa exhaustivo de infracciones por campo:
     ```json
     {
       "timestamp": "2026-09-26T...",
       "status": 400,
       "error": "Bad Request",
       "message": "Error de validacion en los campos enviados en la solicitud",
       "path": "/api/pacientes",
       "fieldErrors": {
         "dni": "El DNI debe contener exactamente 8 digitos",
         "nombre": "El nombre debe tener entre 2 y 60 caracteres",
         "apellido": "El apellido es obligatorio",
         "edad": "La edad no puede ser menor a 0",
         "nivelHemoglobina": "El nivel de hemoglobina debe ser al menos 0.1"
       }
     }
     ```

---

### Caso 4: Consultas Parametrizadas y Filtrado JPQL en URL (Query Params)
*Rutas auditadas:* `/api/productos/stock?minimo=5`, `/api/pacientes/anemia`

*Metodo de ejecucion:*
- **Opcion A (URL directa):** Ingresar `http://localhost:8080/api/productos/stock?minimo=5` y presionar **`Send`**.
- **Opcion B (Pestana Query):**
  1. En URL: `http://localhost:8080/api/productos/stock`.
  2. En la pestana **`Query`**, definir Key: `minimo`, Value: `5`.
  3. Presionar **`Send`**.
* **Criterio de exito:** Codigo HTTP `200 OK` retornando exclusivamente los registros que cumplan con la condicion de stock insuficiente evaluada por la consulta declarativa `@NamedQuery`.

---

### Caso 5: Autenticacion Stateless con JSON Web Tokens (JWT)
*Rutas auditadas:* `/api/auth/login` y endpoints protegidos

1. **Fase 1: Generacion de Credencial (Token):**
   - Metodo: **`POST`**
   - URL: `http://localhost:8080/api/auth/login`
   - Body -> JSON (usar [`docs/payloads/01_auth_credentials.json`](payloads/01_auth_credentials.json)):
     ```json
     {
       "username": "admin",
       "password": "admin123"
     }
     ```
   - Presionar **`Send`**.
   - Respuesta obtenida:
     ```json
     {
       "token": "eyJhbGciOiJIUzI1NiJ9...",
       "tokenType": "Bearer",
       "username": "admin"
     }
     ```
   - Copiar el valor alfanumerico del token.

2. **Fase 2: Consumo de Recursos con Token de Autorizacion:**
   - En una peticion a un endpoint restringido, ingresar a la pestana **`Auth`**.
   - Seleccionar **`Bearer Token`** y pegar la cadena criptografica.
   - Presionar **`Send`**.

---

### Caso 6: Verificacion de Persistencia de Bajo Nivel (EntityManager)
*Rutas auditadas:* `/api/legacy/pacientes`

1. Enviar peticion **`POST`** a `http://localhost:8080/api/legacy/pacientes` con el payload [`docs/payloads/05_entity_payload_legacy_persistence.json`](payloads/05_entity_payload_legacy_persistence.json).
2. **Criterio de exito:** El registro es procesado mediante el metodo `persist()` directo de `EntityManager`, confirmando la coexistencia de la arquitectura dual de persistencia.

---

## 4. Recomendaciones de Productividad para Sesiones de Auditoria

1. **Uso del Registro Historico (Activity History):**  
   Thunder Client almacena cada solicitud enviada en la pestana **Activity** de la barra lateral. Permite recargar cualquier peticion previa con un solo clic sin reescribir URLs ni cuerpos JSON.
2. **Duplicacion Rapida de Pestanas:**  
   Al auditar multiples metodos para un mismo recurso (`GET`, `POST`, `DELETE`), hacer clic derecho sobre la pestana de la peticion activa y seleccionar **Duplicate** para cambiar el verbo HTTP de inmediato.
3. **Auditoria Directa en Memoria (Consola H2):**  
   Para comprobar fisicamente que los registros insertados via REST residen en el motor relacional:
   - Acceder en el navegador a: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
   - JDBC URL: `jdbc:h2:mem:enterprise_db`
   - Usuario: `sa` | Contrasena: *(dejar en blanco)*
   - Ejecutar la sentencia SQL: `SELECT * FROM PACIENTES;`
