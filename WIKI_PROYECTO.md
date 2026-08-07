# Proyecto y Arquitectura

## Arquitectura General
El proyecto sigue el patrón **MVC (Model-View-Controller)**:
- **Modelo:** Clases Java simples (POJOs) y DAOs (Data Access Objects) conectados a H2.
- **Vista:** Archivos JSP que renderizan HTML utilizando Bootstrap 5.
- **Controlador:** Servlets de Jakarta EE que orquestan el flujo y la lógica de negocio.

## Módulos Implementados (MVP)
1. **Autenticación (AuthFilter):** Gestiona el acceso al sistema asegurando que solo usuarios válidos con sesiones activas interactúen con los endpoints protegidos.
2. **Dashboard:** Pantalla principal con KPI y métricas al instante.
3. **CRM de Clientes:** Altas, visualizaciones, ediciones y bajas lógicas de pacientes y clientes regulares.
4. **Catálogo de Servicios:** Administración de los cursos, diplomados y terapias.

## Modelo de Datos
La base de datos (H2) cuenta con tres entidades primarias:
- `usuarios`: Administradores y Staff. Contraseñas seguras con BCrypt.
- `clientes`: Información de contacto y estado (`ACTIVO`, `INACTIVO`, `LAPSADO`, `NUEVO`).
- `servicios`: Descripciones, tipo (Terapia/Curso/Diplomado/Taller), duración y precio.
