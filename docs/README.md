# Documentacion Tecnica, Gobernanza de Arquitectura y Suite de Verificacion

> **Compromiso con la Transparencia Tecnica y la Calidad de Software:**  
> Este directorio contiene la documentacion tecnica exhaustiva, especificaciones de diseno arquitectonico, manuales de integracion y suites de verificacion de contratos REST del proyecto. Su proposito es garantizar la transferibilidad, auditabilidad y mantenibilidad de la base de codigo conforme a estandares industriales de ingenieria de software.

---

## Indice de Documentacion

El directorio se organiza en los siguientes componentes modulares:

1. **[Guia de Arquitectura N-Capas, Extensibilidad y Runbook de Desarrollo](ARCHITECTURE_AND_EXTENSIBILITY_GUIDE.md):**  
   Manual exhaustivo para desarrolladores e ingenieros de software sobre como extender el dominio de la aplicacion, modelar nuevas entidades con JPA, implementar DTOs con Bean Validation, crear consultas JPQL seguras y exponer controladores semanticos respetando el diseno N-Capas.

2. **[Guia de Verificacion de Integracion de API (Thunder Client, REST Client y cURL)](API_TESTING_AND_VERIFICATION_GUIDE.md):**  
   Manual de aseguramiento de la calidad (QA) y procedimientos paso a paso para auditar visual y programaticamente cada endpoint, incluyendo validacion de codigos de estado HTTP (200, 201, 400 Bad Request) y politicas de autenticacion JWT.

3. **[Especificacion de Contratos REST RFC-7230 (`api-contracts.http`)](api-contracts.http):**  
   Archivo ejecutable de peticiones HTTP para probar de forma directa e interactiva todos los endpoints desde Visual Studio Code utilizando Thunder Client, REST Client o IntelliJ HTTP Client.

4. **[Coleccion Exportada de Thunder Client (`thunder-collection.json`)](thunder-collection.json):**  
   Suite completa de pruebas preconfigurada y categorizada por modulos funcionales para importacion rapida en entornos de prueba.

5. **[Directorio de Cargas Utiles de Prueba (`payloads/`)](payloads/):**  
   Coleccion de archivos JSON de prueba (fixtures) con datos representativos para validar registros estandar, escenarios con reglas de negocio, infracciones de validacion y autenticacion.
