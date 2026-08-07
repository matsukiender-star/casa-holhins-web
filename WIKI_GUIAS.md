# Guías y Manuales

## Instalación y Ejecución Local

### Prerrequisitos
- **Java 11:** Usa un gestor como SDKMAN (`sdk install java 11.0.21-tem`).
- **Apache Maven 3.9+:** Usado para resolución de dependencias y empaquetado.
- **Apache Tomcat 10.1+:** Servidor de aplicaciones web (requerido por Jakarta EE 10).

### Desarrollo Local (Run)
Puedes utilizar Maven y un plugin para correr localmente, o simplemente empaquetarlo y mandarlo a Tomcat.

1. **Testear el código:**
   ```bash
   mvn clean test
   ```

2. **Empaquetar la aplicación:**
   ```bash
   mvn clean package
   ```
   Esto compila y genera el archivo `target/casa-holhins-web.war`.

## Despliegue en Servidor Local (Tomcat)

Si tienes Tomcat descargado en una carpeta local (ej: `~/dev-tools/apache-tomcat-10.1.55`):

1. **Copia el WAR generado** hacia la carpeta `webapps` del servidor Tomcat:
   ```bash
   cp target/casa-holhins-web.war ~/dev-tools/apache-tomcat-10.1.55/webapps/
   ```

2. **Arranca el servidor:**
   Ve a la carpeta `bin` de tu Tomcat y ejecuta:
   ```bash
   ./startup.sh
   ```

3. **Verifica:**
   Tomcat automáticamente descomprimirá el `.war` y creará la carpeta `/casa-holhins-web`.
   Abre un navegador y visita `http://localhost:8080/casa-holhins-web/`.

## Usuarios por Defecto
El script `data.sql` precarga la BD con dos perfiles de acceso (passwords reales listos para usar):
- **Administrador:** `admin` / `admin123`
- **Staff:** `staff` / `staff123`
