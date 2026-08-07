# Detalles Técnicos y Arquitectura

## Arquitectura
Casa Holhins Web CRM sigue el patrón arquitectónico **MVC (Modelo-Vista-Controlador)**.
1. **Modelo**: Clases POJO simples para representar los datos de negocio (`Cliente`, `Servicio`, `Usuario`).
2. **Vista**: Páginas JSP (JavaServer Pages) que utilizan Bootstrap 5 para un diseño responsivo y moderno.
3. **Controlador**: Servlets de Java que procesan las peticiones HTTP, validan los datos y llaman a la capa de acceso a datos (DAO).

El sistema usa una capa intermedia `DAO` (Data Access Object) para encapsular la comunicación con la base de datos, usando sentencias `PreparedStatement` preparadas para evitar Inyecciones SQL.

## Stack Tecnológico
* **Lenguaje:** Java 11+
* **Plataforma Web:** Jakarta EE (Servlets, JSP)
* **Servidor de Aplicaciones:** Apache Tomcat 10.1+
* **Base de Datos:** H2 Database Engine (modo embebido por archivo persistente)
* **Frontend:** HTML5, CSS3, Bootstrap 5, FontAwesome, JavaScript vainilla.
* **Seguridad:** BCrypt (para hashes de contraseñas), `SecureRandom` para mitigación de ataques web.
* **Testing:** JUnit 5 (Pruebas unitarias y de integración sobre la base de datos en modo mem).

## Modelo de Datos
La base de datos se inicializa automáticamente al primer arranque mediante `schema.sql` y `data.sql`:
- **`usuarios`**: Contiene la información de login. Las contraseñas están fuertemente protegidas con BCrypt. Roles permitidos: ADMIN y STAFF.
- **`clientes`**: Información general del cliente. Utiliza estatus lógicos (`ACTIVO`, `LAPSADO`, `NUEVO`, `INACTIVO`) en lugar de borrar registros permanentemente.
- **`servicios`**: El catálogo completo (Terapias, Talleres, Diplomados, Cursos). Incluye descripciones, precios en formato Decimal, y duraciones en minutos. También soporta bajas lógicas (`activo = FALSE`).

## Seguridad Avanzada (Protección CSRF)
Para asegurar que las peticiones que modifican la base de datos provengan genuinamente del sistema y no de sitios maliciosos, se implementó protección contra ataques de Falsificación de Petición en Sitios Cruzados (CSRF).

### ¿Cómo funciona?
1. **Generación:** Al iniciar la sesión, se genera un token único y aleatorio usando `SecureRandom` + `UUID.randomUUID()`. Este token se almacena en los atributos de la sesión del usuario.
2. **Distribución:** Cada formulario en JSP (crear cliente, editar servicio, etc.) incluye este token en un campo oculto (`<input type="hidden" name="csrf_token">`).
3. **Validación:** El filtro principal (`SecurityFilter`) intercepta todas las peticiones `POST`. Extrae el token enviado por el cliente y lo compara (en **tiempo constante**, usando `MessageDigest.isEqual` para evitar ataques de timing) con el token almacenado en sesión. Si no coinciden, la petición es rechazada con un código HTTP 403 Forbidden.
4. **Prevención de Fixation:** Al momento del inicio de sesión (Login), se llama a `request.changeSessionId()` para rotar el identificador de la sesión de Tomcat y mitigar posibles vulnerabilidades de robo o fijación de sesión (Session Fixation).
