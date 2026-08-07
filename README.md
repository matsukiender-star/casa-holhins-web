# Casa Holhins Web System

![CI Status](https://github.com/matsukiender-star/casa-holhins-web/actions/workflows/ci.yml/badge.svg)

Sistema web CRM integral desarrollado para Casa Holhins, empresa de bienestar holístico en Tizayuca, Hidalgo. 
Este MVP permite administrar clientes y un catálogo de servicios (terapias, cursos, diplomados) de manera centralizada.

## Arquitectura del Proyecto

![Arquitectura Casa Holhins](./arquitectura_casa_holhins.png)

El sistema sigue un patrón Modelo-Vista-Controlador (MVC) apoyado por Servlets y JSP bajo Jakarta EE 10. La capa de datos utiliza JDBC contra una base de datos embebida H2.

## Screenshots

### Login
[ Espacio para Screenshot Login ]

### Dashboard Principal
[ Espacio para Screenshot Dashboard ]

### Catálogo de Servicios
[ Espacio para Screenshot Servicios ]

## Requerimientos Técnicos

- **Java JDK 11**
- **Apache Tomcat 10.1+**
- **Maven 3.9+**

## Instalación y Configuración

1. Clonar este repositorio:
   ```bash
   git clone https://github.com/matsukiender-star/casa-holhins-web.git
   cd casa-holhins-web
   ```

2. Empaquetar el proyecto con Maven:
   ```bash
   mvn clean package
   ```

3. El archivo WAR se generará en `target/casa-holhins-web.war`.

## Ejecución (Deploy)

1. Copiar el archivo WAR al directorio `webapps` de Tomcat:
   ```bash
   cp target/casa-holhins-web.war ~/dev-tools/apache-tomcat-10.1.55/webapps/
   ```

2. Arrancar Tomcat:
   ```bash
   ~/dev-tools/apache-tomcat-10.1.55/bin/startup.sh
   ```

3. Abrir el navegador en:
   `http://localhost:8080/casa-holhins-web/`

## Manual de Uso

### Manual para Administrador
El administrador (usuario `admin`, password `admin123`) tiene acceso completo a:
- **Gestión de Clientes:** Alta, edición, baja lógica (Inactivar).
- **Catálogo de Servicios:** Crear nuevas terapias, talleres, diplomados o cursos y establecer sus precios.
- **Dashboard:** Visualizar de un vistazo los últimos clientes registrados y los contadores totales de activos.

### Manual para Usuario Final (Staff)
El usuario staff (usuario `staff`, password `staff123`) puede acceder para revisar información operativa, aunque en versiones futuras los roles restringirán capacidades sensibles.

## Roadmap de Módulos (Futuro)

Este MVP cubre los Módulos 1 a 3. Quedan pendientes en el roadmap:
- [ ] **Módulo 4: Pagos.** Integración para cobro y registro de pagos de sesiones y cursos.
- [ ] **Módulo 5: Inscripciones.** Control de asistencia y cupos para talleres y diplomados.
- [ ] **Módulo 6: Referidos.** Sistema de lealtad y descuentos por invitar a otros clientes.
- [ ] **Módulo 7: Encuestas.** Formularios de satisfacción tras cada terapia.
- [ ] **Módulo 8: Reportes.** Exportación de métricas de ingresos a PDF y Excel.

## Enlaces Útiles
- [Wiki del Proyecto](https://github.com/matsukiender-star/casa-holhins-web/wiki)

## Guía de Contribución

1. Crea una rama para tu feature: `git checkout -b feature/mi-nueva-funcionalidad`
2. Realiza commits pequeños y descriptivos: `git commit -m "feat: agrega formulario de clientes"`
3. Empuja tu rama al repositorio: `git push origin feature/mi-nueva-funcionalidad`
4. Crea un Pull Request contra la rama `develop`.

## Licencia

Este proyecto está bajo la Licencia MIT.
