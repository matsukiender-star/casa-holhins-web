# Fase III. Ejecución — Casa Holhins Web System

**Alumno:** Angel Gabriel Carrizales Trejo
**Certificado:** Java — Universidad Tecmilenio
**Fecha:** 7 de agosto de 2026
**Repositorio:** https://github.com/matsukiender-star/casa-holhins-web

---

## Índice

1. [Repositorio del proyecto (Criterio 1 · 30 pts)](#1-repositorio-del-proyecto-criterio-1--30-pts)
2. [Integración con Trello (Criterio 2 · 25 pts)](#2-integración-con-trello-criterio-2--25-pts)
3. [Integración continua (Criterio 3 · 25 pts)](#3-integración-continua-criterio-3--25-pts)
4. [Diagrama de arquitectura (Criterio 4 · 20 pts)](#4-diagrama-de-arquitectura-criterio-4--20-pts)
5. [Firma y autorización](#5-firma-y-autorización)

---

## 1. Repositorio del proyecto (Criterio 1 · 30 pts)

**URL:** https://github.com/matsukiender-star/casa-holhins-web
**Visibilidad:** público · **Licencia:** MIT

### 1.1 Branches

El proyecto sigue un flujo de tres niveles: las funcionalidades se desarrollan en ramas `feature/*` o `docs/*`, se integran en `develop`, y `develop` se promociona a `master` cuando el conjunto está estable.

| Rama | Rol | Estado |
|------|-----|--------|
| `master` | Código estable. Rama por defecto del repositorio. Etiquetada `v1.0.0`. | Activa |
| `develop` | Integración de funcionalidades antes de promocionar a `master`. | Activa |
| `feature/correccion-bugs-crud` | Corrección de los bugs detectados en pruebas de aceptación. | Mergeada en `develop` (PR #13) |
| `docs/final-touches` | Documentación, reporte de bugs y corrección del trigger de CI. | Mergeada en `develop` (PR #14) |
| `feature/ajustes-cosmeticos-y-roadmap` | Ajustes visuales y realineación del roadmap. | Mergeada (histórica, commit `51c9159`) |
| `main` | Rama previa al cambio de nomenclatura, conservada por compatibilidad. | Conservada |

**Ver:** https://github.com/matsukiender-star/casa-holhins-web/branches

### 1.2 Milestones

| Milestone | Estado | Issues | Descripción |
|-----------|--------|--------|-------------|
| **Beta** | Cerrado | 9 | Desarrollo del MVP con los módulos base: autenticación, CRM de clientes, catálogo de servicios y dashboard. |
| **GA** | Cerrado | 3 | Release estable: integración continua, documentación y corrección de los bugs detectados en pruebas de aceptación. |

**Ver:** https://github.com/matsukiender-star/casa-holhins-web/milestones

### 1.3 Issues

**12 issues**, todos cerrados y asociados a su milestone. Cada uno sigue la estructura **Descripción · Análisis · Solución implementada · Criterios de aceptación**, y la sección de solución referencia los commits reales del repositorio.

| # | Título | Milestone |
|---|--------|-----------|
| [#1](https://github.com/matsukiender-star/casa-holhins-web/issues/1) | Setup del proyecto Maven con estructura MVC | Beta |
| [#2](https://github.com/matsukiender-star/casa-holhins-web/issues/2) | Modelo de datos H2 con schema.sql y data.sql | Beta |
| [#3](https://github.com/matsukiender-star/casa-holhins-web/issues/3) | Módulo de Autenticación con bcrypt y sesiones | Beta |
| [#4](https://github.com/matsukiender-star/casa-holhins-web/issues/4) | Módulo CRM Clientes con CRUD completo y baja lógica | Beta |
| [#5](https://github.com/matsukiender-star/casa-holhins-web/issues/5) | Módulo Catálogo de Servicios | Beta |
| [#6](https://github.com/matsukiender-star/casa-holhins-web/issues/6) | Dashboard con métricas básicas | Beta |
| [#7](https://github.com/matsukiender-star/casa-holhins-web/issues/7) | AuthFilter y protección de rutas | Beta |
| [#8](https://github.com/matsukiender-star/casa-holhins-web/issues/8) | Protección CSRF en todos los formularios que escriben | Beta |
| [#9](https://github.com/matsukiender-star/casa-holhins-web/issues/9) | Tests unitarios JUnit 5 con cobertura de regresión | Beta |
| [#10](https://github.com/matsukiender-star/casa-holhins-web/issues/10) | GitHub Actions CI con ejecución de tests | GA |
| [#11](https://github.com/matsukiender-star/casa-holhins-web/issues/11) | Documentación: README, Wiki y diagrama de arquitectura | GA |
| [#12](https://github.com/matsukiender-star/casa-holhins-web/issues/12) | Bug: no se puede dar de baja un cliente ni crear un servicio en bases existentes | GA |

**Ver:** https://github.com/matsukiender-star/casa-holhins-web/issues?q=is%3Aissue

> **Nota sobre el issue #12.** No es un requisito planificado, sino un bug real encontrado al probar el sistema desplegado para grabar la demostración. Se documenta con el mismo rigor que los demás porque el análisis de su causa raíz —una corrección de esquema que nunca alcanzaba a las bases ya existentes, y unas excepciones que se descartaban en silencio— resultó ser el hallazgo técnico más relevante del proyecto.

### 1.4 Pull Requests

| PR | Origen → Destino | Contenido |
|----|------------------|-----------|
| [#13](https://github.com/matsukiender-star/casa-holhins-web/pull/13) | `feature/correccion-bugs-crud` → `develop` | Corrección de la baja de clientes y el alta de servicios; visibilidad de errores de escritura |
| [#14](https://github.com/matsukiender-star/casa-holhins-web/pull/14) | `docs/final-touches` → `develop` | Documentación, reporte de bugs y corrección del trigger de CI |
| [#15](https://github.com/matsukiender-star/casa-holhins-web/pull/15) | `develop` → `master` | Promoción a estable para el cierre de Fase III |

Los tres están mergeados, con descripción detallada, y cada uno disparó su corrida de integración continua.

**Ver:** https://github.com/matsukiender-star/casa-holhins-web/pulls?q=is%3Apr

### 1.5 Código y versionado

- **Tag de versión:** `v1.0.0` — https://github.com/matsukiender-star/casa-holhins-web/releases/tag/v1.0.0
- **Código:** ~20 clases Java, 8 vistas JSP, scripts `schema.sql` y `data.sql`, empaquetado como WAR de Jakarta EE 10.
- **Convención de commits:** Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`, `test:`, `style:`, `ci:`, `release:`) en la totalidad del historial.

### 1.6 Capturas de pantalla requeridas

> *[Insertar aquí las capturas listadas en `capturas/`: branches, milestones, issues, un issue abierto mostrando la estructura Descripción/Análisis/Solución, pull requests y tag de versión.]*

---

## 2. Integración con Trello (Criterio 2 · 25 pts)

**Tablero:** https://trello.com/invite/b/6a75348f08fd2808dafb8c1e/ATTI23449941e7d677158b54683279b08554064D4057/casa-holhins-web

El tablero de Trello es la herramienta de gestión visual del proyecto y refleja los mismos 12 requisitos que se registran como Issues en GitHub, organizados por estado de avance.

**Estructura del tablero:**

| Lista | Contenido |
|-------|-----------|
| Backlog | Requisitos identificados pendientes de iniciar |
| En progreso | Requisitos en desarrollo activo |
| En revisión | Requisitos con Pull Request abierto |
| Terminado | Requisitos completados y mergeados |

**Etiquetas por tipo de trabajo:** Backend · Frontend · Docs · DevOps · Testing · Bug

**Correspondencia con GitHub:** cada tarjeta corresponde a uno de los 12 issues del repositorio (§1.3), con el mismo título, de modo que el tablero y el repositorio cuentan la misma historia desde dos vistas distintas.

### 2.1 Capturas de pantalla requeridas

> *[Insertar aquí: captura del tablero completo con las tarjetas visibles en sus listas, y captura de una tarjeta abierta mostrando su vínculo con el issue de GitHub correspondiente.]*

---

## 3. Integración continua (Criterio 3 · 25 pts)

### 3.1 Sustitución justificada de la herramienta

El criterio solicita **Travis-CI o Drone**. Se optó por **GitHub Actions** por dos razones:

1. **Disponibilidad.** Travis-CI retiró su plan gratuito para proyectos de uso regular, incluidos los públicos. Mantener el proyecto en él exigiría una suscripción de pago.
2. **Coherencia con el hosting.** El repositorio ya vive en GitHub; Actions es su sistema de CI nativo, lo que elimina la necesidad de un servicio externo con permisos sobre el repositorio.

Las capacidades técnicas evaluadas son equivalentes: ejecución automática por evento, runners efímeros, caché de dependencias, ejecución de la suite de pruebas y badge público de estado.

### 3.2 Configuración

**Archivo:** `.github/workflows/ci.yml`

| Aspecto | Valor |
|---------|-------|
| Disparadores | `push` a `master` y `develop`; `pull_request` hacia `master` y `develop` |
| JDK | 11, distribución Temurin |
| Caché | Dependencias de Maven |
| Comando | `mvn -B test --file pom.xml` |
| Seguridad | Las actions se referencian **por SHA completo**, no por etiqueta |

> **Sobre el uso de SHA completo:** una etiqueta como `@v4` puede reapuntarse a otro commit por quien mantiene la action; un SHA no. Es la recomendación de la guía de endurecimiento de GitHub frente a ataques de cadena de suministro.

### 3.3 Pruebas JUnit

**14 pruebas JUnit 5**, todas en verde. El criterio pide al menos una.

| Clase | Pruebas | Cubre |
|-------|---------|-------|
| `MigracionTest` | 2 | Regresión de los dos bugs bloqueantes: reparación del `CHECK` de estatus y realineación de contadores de identidad |
| `ClienteDAOTest` | 2 | Alta, consulta, actualización y baja lógica (regresión G2) |
| `ServicioDAOTest` | 2 | Persistencia de servicios entre conexiones (regresión G1) y desactivación |
| `UsuarioDAOTest` | 2 | Consulta y autenticación de usuarios |
| `UsuarioTest` | 4 | Modelo de usuario y derivación del nombre corto |
| `ClienteTest` | 1 | Modelo de cliente |
| `PasswordUtilTest` | 1 | Hash y verificación con bcrypt |

Las pruebas corren contra una base H2 **en memoria**, de modo que no dependen del entorno ni alteran datos reales.

### 3.4 Estado del pipeline

Últimas corridas, todas con conclusión `success`:

| Run | Rama | Evento | Resultado |
|-----|------|--------|-----------|
| #13 | `master` | push | ✅ success |
| #12 | `develop` | pull_request | ✅ success |
| #11 | `develop` | push | ✅ success |
| #10 | `docs/final-touches` | pull_request | ✅ success |
| #9 | `develop` | push | ✅ success |
| #8 | `feature/correccion-bugs-crud` | pull_request | ✅ success |
| #7 | `master` | push | ✅ success |

El historial completo incluye corridas fallidas iniciales (#1–#4), correspondientes a la estabilización real del pipeline. Se conservan deliberadamente: demuestran que la integración continua se usó como herramienta de trabajo y no como configuración decorativa.

**Dashboard:** https://github.com/matsukiender-star/casa-holhins-web/actions

### 3.5 Capturas de pantalla requeridas

> *[Insertar aquí: captura de la última corrida en verde mostrando el conteo de pruebas y el tiempo, y captura del listado de corridas con el historial.]*

---

## 4. Diagrama de arquitectura (Criterio 4 · 20 pts)

![Arquitectura Casa Holhins Web System](./arquitectura_casa_holhins.png)

El sistema sigue el patrón **Modelo-Vista-Controlador** sobre Jakarta EE 10, organizado en seis capas:

**1. Cliente.** Navegadores web de escritorio, tableta y móvil.

**2. Presentación.** JSP con JSTL 3.0 para el renderizado del lado del servidor, Bootstrap 5 para la interfaz responsiva con la paleta corporativa de Casa Holhins (`--holhins-dark-sage`, `--holhins-eucalyptus`, `--holhins-light-sage`, `--holhins-natural-cream`), definida como variables CSS en `holhins.css`.

**3. Control.** Cuatro servlets — `LoginServlet`, `ClienteServlet`, `ServicioServlet` y `DashboardServlet` — más `AuthFilter`, que intercepta todas las peticiones y exige sesión iniciada salvo en `/login` y los recursos estáticos. Los servlets que escriben validan token CSRF mediante `CsrfUtil`.

**4. Modelo.** POJOs de dominio (`Usuario`, `Cliente`, `Servicio`) sin lógica de persistencia.

**5. Persistencia.** DAOs (`UsuarioDAO`, `ClienteDAO`, `ServicioDAO`) que encapsulan todo el SQL mediante `PreparedStatement`. Base de datos **H2 embebida en archivo**, accedida por JDBC. `ConexionDB` centraliza la obtención de conexiones y la inicialización del esquema: ejecuta `schema.sql`, siembra con `data.sql` solo si la base está vacía, y ejecuta una migración idempotente que pone al día las bases creadas con versiones anteriores del esquema.

**6. DevOps y runtime.** Apache Tomcat 10.1 en el puerto 8080, Maven como herramienta de construcción, GitHub como repositorio, GitHub Actions con JUnit 5 para la integración continua y Trello para la gestión del proyecto.

**Archivo:** `arquitectura_casa_holhins.png`, versionado en la raíz del repositorio y referenciado desde el README.

---

## 5. Firma y autorización

Este proyecto se desarrolla con **autorización formal de la Directora General de Casa Holhins, Ana María Trejo Holhins**, para publicar el código bajo licencia MIT y desplegarlo para uso operativo real de la empresa.

El desarrollador, Angel Gabriel Carrizales Trejo, funge como CFO/Consultor de Casa Holhins, lo que permitió un levantamiento de requerimientos directo y continuo con la usuaria final —incluida la entrevista con la Directora General y la secretaria que dio origen al roadmap documentado en el README.

La carta de consentimiento firmada se anexa a esta entrega.
