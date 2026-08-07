# Issues para GitHub (Milestones: Beta y GA)

## Issue 1: Setup del proyecto Maven con estructura MVC (Milestone: Beta)
### Descripción
Crear el cascarón del proyecto web Java con Maven, instalando dependencias (Jakarta EE, H2, BCrypt, JUnit) y definiendo el modelo MVC de carpetas.
### Análisis
Debe ser compatible con Java 11 y Tomcat 10.1.55 usando el namespace `jakarta.*`.
### Solución
1. Configurar `pom.xml`.
2. Crear directorios para modelo, dao, servlet, etc.
### Criterios de aceptación
- [x] El proyecto compila con `mvn clean compile`.
- [x] Contiene estructura MVC.

## Issue 2: Implementar modelo de datos H2 con schema.sql y data.sql (Milestone: Beta)
### Descripción
Definir las tablas principales (usuarios, clientes, servicios) y proveer la data inicial en una BD embebida.
### Análisis
La base de datos será H2 para evitar dependencias externas.
### Solución
1. Crear `schema.sql`.
2. Crear `data.sql` con usuarios y servicios iniciales.
### Criterios de aceptación
- [x] H2 inicia correctamente.
- [x] Datos base están presentes.

## Issue 3: Implementar módulo de Autenticación con bcrypt y sesiones (Milestone: Beta)
### Descripción
Formulario de login, servlet manejador y uso de BCrypt para seguridad.
### Análisis
Hay que prevenir acceso no autorizado validando passwords hasheadas.
### Solución
1. Crear `LoginServlet`.
2. Crear clase utilitaria para BCrypt.
### Criterios de aceptación
- [x] El hash bcrypt funciona.
- [x] Generación de sesión HTTP exitosa.

## Issue 4: Implementar módulo CRM Clientes con CRUD completo (Milestone: Beta)
### Descripción
Gestión de listado, alta, edición y detalle de clientes.
### Análisis
Se usará paginación básica de 10 clientes por página y bajas lógicas.
### Solución
1. Crear `ClienteDAO` y `ClienteServlet`.
2. JSPs correspondientes.
### Criterios de aceptación
- [x] Se pueden crear y listar clientes.
- [x] Soporta baja lógica.

## Issue 5: Implementar módulo Catálogo de Servicios (Milestone: Beta)
### Descripción
Catálogo interno de las terapias, cursos y talleres con sus precios.
### Análisis
Debe soportar filtro por tipo de servicio.
### Solución
1. `ServicioDAO` y `ServicioServlet`.
2. Vistas con lista y tarjetas Bootstrap.
### Criterios de aceptación
- [x] Se muestran tarjetas de servicios.
- [x] Filtro funcional.

## Issue 6: Implementar Dashboard con métricas básicas (Milestone: Beta)
### Descripción
Pantalla principal tras el login con resúmenes del sistema.
### Análisis
Debe mostrar total de clientes y últimos registros.
### Solución
1. `DashboardServlet` que inyecte métricas.
2. Vista amigable con estadísticas y tabla.
### Criterios de aceptación
- [x] Muestra nombre del usuario logueado.
- [x] Visualiza clientes recientes.

## Issue 7: Implementar AuthFilter y protección de rutas (Milestone: Beta)
### Descripción
Bloquear acceso a páginas internas si no hay sesión.
### Análisis
Cualquier intento a rutas privadas redirigirá al login.
### Solución
1. Crear un `Filter` con `urlPatterns = /*`.
### Criterios de aceptación
- [x] Redirección automática al `/login`.

## Issue 8: Escribir tests unitarios JUnit 5 (Milestone: Beta)
### Descripción
Validar la lógica fundamental del modelo, seguridad y DAO.
### Análisis
Usar H2 en memoria para que no altere la base de producción.
### Solución
1. Escribir tests para PasswordUtil, Usuarios y Clientes.
### Criterios de aceptación
- [x] Al menos 8 métodos de prueba.
- [x] Pasan en local.

## Issue 9: Configurar GitHub Actions CI con ejecución de tests (Milestone: GA)
### Descripción
Workflow automatizado para correr los tests en la nube.
### Análisis
Necesario Java 11 Temurin.
### Solución
1. Crear `.github/workflows/ci.yml`.
### Criterios de aceptación
- [x] Corre `mvn test` con cada push.

## Issue 10: Documentación completa: README, Wiki, capturas, roadmap (Milestone: GA)
### Descripción
Dejar lista la documentación técnica y funcional de la aplicación.
### Análisis
Exigencia de la universidad (20 puntos).
### Solución
1. Completar README y páginas Wiki.
### Criterios de aceptación
- [x] README con capturas y roadmap.
- [x] 3 páginas Wiki redactadas.
