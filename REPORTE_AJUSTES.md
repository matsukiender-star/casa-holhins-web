# Reporte de ajustes — Casa Holhins Web System

**Fecha:** 7 de agosto de 2026
**Rama:** `feature/ajustes-cosmeticos-y-roadmap` (sale de `develop`)
**Base de comparación:** commit `7455a52` (MVP inicial)
**Cambio total:** 30 archivos, +966 / −188 líneas

---

## 1. Resultado de la auditoría: **regular**

El MVP llegó con una base sólida —MVC bien separado, bcrypt cost 12,
`PreparedStatement` en el 100% de las queries, `try-with-resources` en los 14 métodos
DAO y cero imports `javax.*`— pero con **dos funcionalidades rotas en tiempo de
ejecución que los tests no detectaban**, más un hueco de seguridad. El detalle completo
está en `AUDITORIA_MVP.md`; aquí va el saldo:

| # | Hallazgo | Gravedad | Estado |
|---|---|---|---|
| G1 | El CRUD de servicios no persistía: editar o desactivar se revertía solo | 🚨 Grave | ✅ Corregido |
| G2 | La baja lógica de clientes fallaba siempre (violación de CHECK) | 🚨 Grave | ✅ Corregido |
| G3 | Sin protección CSRF en ningún formulario | 🚨 Grave | ✅ Corregido |
| G4 | `AuthFilter` habría bloqueado el logo y el favicon | 🚨 Grave | ✅ Corregido |
| m1 | 14 `printStackTrace()` que ocultaban los errores | ⚠️ Menor | ✅ Corregido |
| m2 | `Integer.parseInt` sin protección → error 500 con `?id=abc` | ⚠️ Menor | ✅ Corregido |
| m3 | Sin pool de conexiones | ⚠️ Menor | ⏳ Documentado como deuda técnica |
| m4 | Cobertura de tests escasa (no cubre lo que estaba roto) | ⚠️ Menor | ⏳ Pendiente (ver §5) |
| m5 | `GenerateHash.class` suelto en la raíz | ⚠️ Menor | ✅ Eliminado |
| m6 | El repo no tenía historial de git | 🚧 Proceso | ✅ Resuelto |

### La causa de fondo de G1, que conviene entender

La URL de H2 traía `INIT=RUNSCRIPT FROM 'classpath:schema.sql'\;RUNSCRIPT FROM
'classpath:data.sql'`. Ese `INIT` **no corre una vez al arrancar: corre cada vez que se
abre una conexión**. Como cada método DAO abre la suya, `data.sql` se re-ejecutaba
constantemente y sus `MERGE` devolvían los servicios a sus valores originales. Lo
comprobé ejecutando código real contra la base:

```
Servicio 1 original : Terapia Access Bars | activo=true | $500.00
--- el usuario lo DESACTIVA desde la UI ---
Servicio 1 ahora    : activo=true          <-- se perdió
--- el usuario le cambia el nombre y el precio ---
Tras editar         : Terapia Access Bars | $500.00   <-- se perdió
```

Ahora la base se siembra **una sola vez**, y solo si está vacía. Tras el arreglo:

```
Servicio 1 ahora    : activo=false                      <-- persiste
Tras editar         : NOMBRE EDITADO POR EL USUARIO | $999.00   <-- persiste
```

Esto tuvo una consecuencia directa sobre la Parte 2.6: **el `data.sql` que traían las
instrucciones venía con `INSERT` plano**. Aplicado tal cual con el `INIT` original, los
12 servicios se habrían reinsertado en cada conexión hasta llenar el disco. Lo dejé en
formato `MERGE` con id explícito, que es idempotente aunque alguien lo re-ejecute a mano.

---

## 2. Archivos modificados

### Correcciones funcionales (commit `92ef1bb`)

| Archivo | Qué cambió |
|---|---|
| `dao/ConexionDB.java` | Se quita `INIT=RUNSCRIPT` de la URL; siembra única y `synchronized`; logging |
| `resources/schema.sql` | `'INACTIVO'` agregado al CHECK de `estatus`; comentarios |
| `resources/data.sql` | Catálogo real de 12 servicios; `staff` renombrado a "Secretaria" |
| `util/CsrfUtil.java` | **Nuevo.** Token por sesión con comparación en tiempo constante |
| `filter/AuthFilter.java` | Reconoce `/img/` y `/favicon`; compara ruta relativa al contexto |
| `servlet/LoginServlet.java` | Valida CSRF; rota el id de sesión al autenticar |
| `servlet/ClienteServlet.java` | Valida CSRF; parseo defensivo de `id` y `page` |
| `servlet/ServicioServlet.java` | Valida CSRF; parseo defensivo de `id`, `precio` y duración |

### Ajustes visuales y comentarios (commit `afaa886`)

| Archivo | Qué cambió |
|---|---|
| `webapp/img/logo-holhins.svg` | **Nuevo.** Logo original (blanco), para el navbar |
| `webapp/img/logo-holhins-dark.svg` | **Nuevo.** Recoloreado a `#4A6363`, para el login |
| `webapp/favicon.svg` | **Nuevo.** Versión oscura, para la pestaña del navegador |
| `includes/header.jsp` | Logo de 40 px en el navbar, enlace al favicon, token en sesión |
| `includes/footer.jsp` | Se retira el chiste; línea institucional en verde eucalipto |
| `views/login.jsp` | Logo de 80 px centrado sobre el título; token CSRF |
| `views/dashboard.jsp` | Métricas centradas; encabezados de tabla alineados |
| `views/clientes/lista.jsp` | Columnas ID/Estatus/Acciones centradas; fila de "sin resultados" |
| `views/servicios/lista.jsp` | Encabezado alineado; comentarios |
| `views/*/form.jsp` | Token CSRF y comentario de cabecera |
| `css/holhins.css` | Clases del logo, pie, métricas y alineación de tablas |
| `dao/*.java`, `modelo/*.java`, `util/PasswordUtil.java` | Comentarios estilo Angel; logging |
| `pom.xml` | Comentario de cabecera explicando cada dependencia clave |

### Documentación (commit `58eca51`)

| Archivo | Qué cambió |
|---|---|
| `README.md` | Sección "Sobre Casa Holhins" + roadmap de 6 iteraciones reales |
| `AUDITORIA_MVP.md` | **Nuevo.** Auditoría técnica completa |
| `REPORTE_AJUSTES.md` | **Nuevo.** Este documento |

### Sobre la paleta (Parte 2.4)

**No hizo falta cambiar ningún color.** `holhins.css` ya usaba exactamente los cuatro
valores oficiales (`#4A6363`, `#7D9A9A`, `#B5C5BE`, `#F5F1E8`) y las fuentes ya eran
Cambria para títulos y Calibri para el cuerpo. Busqué `#2E7D32` y otros verdes ajenos en
todo el proyecto: no había ninguno. Solo agregué clases nuevas y `Georgia` como respaldo
de Cambria, por si alguien abre el sistema desde un equipo sin esa tipografía.

---

## 3. Verificación de que el WAR corre

Todo ejecutado y comprobado, no supuesto.

**Compilación y pruebas**

```
mvn clean package  →  BUILD SUCCESS
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
Building war: target/casa-holhins-web.war
```

Los 6 tests pasaban antes de mis cambios y siguen pasando después, sin flakes.

**Despliegue en Tomcat 10.1.55**

```
HTTP 302  /casa-holhins-web/            → redirige a /login
HTTP 200  /casa-holhins-web/login
HTTP 200  /casa-holhins-web/img/logo-holhins-dark.svg   (image/svg+xml, sin sesión)
HTTP 200  /casa-holhins-web/favicon.svg
HTTP 200  /casa-holhins-web/css/holhins.css
HTTP 302  /casa-holhins-web/dashboard   → redirige a /login (sin sesión)
```

Sin una sola excepción en `catalina.out`.

**Prueba de extremo a extremo del login y el CSRF**

```
1. POST /login SIN token                      → HTTP 403   (rechazado, correcto)
2. POST /login CON token, password incorrecta → HTTP 200   (regresa al formulario)
3. POST /login CON token, admin/admin123      → HTTP 302 → /dashboard
4. GET  /dashboard con sesión                 → "Bienvenid@, Ana María Trejo Holhins"
                                                 métricas: 0 clientes, 12 servicios
5. GET  /servicios                            → los 12 servicios reales del catálogo
6. Pie de página                              → línea institucional, sin el chiste
```

**Verificación visual**

Tomé una captura del login con Brave en modo headless: el logo blanco se ve correctamente
en el navbar verde, la versión oscura se lee bien sobre la tarjeta, el título y el
subtítulo están centrados y el pie aparece sobrio en verde eucalipto.

---

## 4. Capturas que te faltan tomar

El README tiene tres huecos con el texto `[ Espacio para Screenshot ... ]`. Con Tomcat
corriendo y sesión iniciada como `admin`, captura y guarda en la raíz del proyecto:

1. **Login** — se luce el logo. Sugerencia: `screenshot-login.png`
2. **Dashboard** — con las métricas centradas. `screenshot-dashboard.png`
3. **Catálogo de servicios** — se ven los 12 servicios reales. `screenshot-servicios.png`

En Hyprland: `grim -g "$(slurp)" screenshot-login.png`. Después sustituye los tres
marcadores del README por `![Login](./screenshot-login.png)` y equivalentes.

**Falta también `arquitectura_casa_holhins.png`**, que el README ya referencia en la
sección de Arquitectura pero **no existe en el repositorio**: ahora mismo esa imagen sale
rota. O la generas, o quitas la línea.

---

## 5. Cosas que debes validar tú

1. **Prueba a mano el CRUD que estaba roto.** Yo lo verifiqué con código, pero conviene
   que lo veas en pantalla: desactiva un servicio, recarga y confirma que sigue
   desactivado; edítale el precio y confirma que se guardó; da de baja a un cliente y
   confirma que desaparece del listado.

2. **Borra `./data/holhins.mv.db` antes de la demostración final.** La base de archivo
   conserva lo que ya había, incluidos los 5 servicios genéricos viejos. Como ahora solo
   se siembra cuando está vacía, los 12 servicios reales aparecen únicamente en una base
   nueva. El `.gitignore` ya excluye `data/`, así que basta con borrar la carpeta.

3. **El PR a `develop` lo tienes que abrir tú.** No hay remoto configurado y `gh` no está
   instalado. La rama local ya está lista; falta `git push -u origin
   feature/ajustes-cosmeticos-y-roadmap` y abrir el PR desde la web. Ojo: **el repo no
   tenía ningún commit** cuando empecé, así que creé el commit inicial del MVP en
   `master`, saqué `develop` de ahí y luego la rama de trabajo. Revisa que ese historial
   te parezca bien antes de publicarlo.

4. **`GenerateHash.java` sigue en la raíz.** Es la utilidad con la que se generaron los
   hashes de `data.sql`. Borré el `.class` compilado; el `.java` lo dejé por si lo
   necesitas para agregar usuarios, pero conviene moverlo a `src/test/` antes de entregar.

5. **Añade tests de lo que estaba roto.** Es la recomendación con más valor de cara al
   profesor: dos tests —"doy de baja un cliente y ya no aparece en el listado" y
   "desactivo un servicio y sigue desactivado tras releer"— habrían cazado G1 y G2 al
   instante. Que el MVP tuviera 6 tests en verde con dos módulos rotos es justamente el
   argumento de por qué la cobertura importa más que el número.

6. **Revisa el texto del roadmap.** Lo puse tal cual venía en tus instrucciones. Como
   describe conversaciones reales con tu mamá y con la secretaria, conviene que confirmes
   que los detalles operativos están bien contados antes de que lo lea el profesor.

7. **Considera documentar el CSRF en la Wiki.** Es un tema que da puntos y ahora está
   implementado de verdad: token por sesión, comparación en tiempo constante para evitar
   ataques de temporización, y rotación del id de sesión al autenticar contra fijación de
   sesión.

---

## 6. Lo que quedó pendiente a propósito

- **Pool de conexiones.** Cada operación sigue abriendo su propia conexión. Para el
  volumen de este sistema no representa un problema, y meter HikariCP significaría una
  dependencia nueva, cosa que la regla 2 pedía evitar.
- **Validación del lado del servidor más allá del parseo.** Hoy la validación es del lado
  del cliente (`required`, `pattern`) más el parseo defensivo. Faltaría validar longitudes
  y formatos en el servlet, porque las validaciones HTML se saltan con cualquier cliente
  HTTP.
- **Los `target/` con `.class`** dentro de los proyectos no se tocaron: son salida normal
  de Maven y `.gitignore` ya los excluye.
