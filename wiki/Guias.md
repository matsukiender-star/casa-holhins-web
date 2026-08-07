# Guías de Uso e Instalación

Esta guía indica cómo desplegar el proyecto y empezar a usarlo en tu entorno local.

## Requisitos Previos
- **Java**: 11 o superior.
- **Maven**: 3.8+ (Recomendado utilizar SDKMAN para manejar las versiones).
- **Servidor Web**: Apache Tomcat 10.1+.

## Clonar y Compilar
1. Clona el repositorio oficial (o conecta tu copia local usando `git remote add origin`).
2. Abre la terminal en el directorio `casa-holhins-web`.
3. Ejecuta el comando de Maven para compilar y empaquetar la aplicación:
   ```bash
   mvn clean package
   ```
4. Si las pruebas pasan, encontrarás un archivo generado en `target/casa-holhins-web.war`.

## Despliegue en Tomcat
1. Copia el archivo WAR a la carpeta `webapps` de tu Tomcat 10:
   ```bash
   cp target/casa-holhins-web.war /ruta/a/tomcat-10.1.x/webapps/
   ```
2. Inicia Tomcat (ejecutando `bin/startup.sh` en Linux/Mac o `bin/startup.bat` en Windows).
3. Tomcat descomprimirá automáticamente el WAR y tu aplicación estará disponible en:
   `http://localhost:8080/casa-holhins-web/`

## Credenciales Iniciales
Al arrancar por primera vez, la base de datos se siembra de manera automática y genera estos dos usuarios de acceso:

- **Directora General:**
  - Usuario: `admin`
  - Contraseña: `admin123`
- **Recepción/Staff:**
  - Usuario: `staff`
  - Contraseña: `staff123`

## Crear Contraseñas Seguras para Nuevos Usuarios
Si necesitas insertar manualmente usuarios nuevos directo a la base de datos, ¡nunca pongas la contraseña en texto claro! Puedes generar un hash válido BCrypt utilizando la utilidad incorporada:

```bash
# Navegar a la carpeta correcta
cd src/test/java/mx/holhins/util/

# Si deseas, puedes compilar la utilidad desde tu IDE, o usarla como referencia 
# para imprimir un hash directamente en la consola con la libreria Favre:
# System.out.println(BCrypt.withDefaults().hashToString(12, "MiNuevoPassword123".toCharArray()));
```
