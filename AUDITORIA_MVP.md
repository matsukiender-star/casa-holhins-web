# Auditoría técnica del MVP — Casa Holhins Web System

**Fecha:** 6 de agosto de 2026
**Alcance:** revisión de solo lectura del código, la configuración y la base de datos.
**Baseline verificado:** `mvn clean test` → **BUILD SUCCESS, 6/6 tests pasan, 0 warnings críticos.**

---

## Resumen ejecutivo

El proyecto **compila, empaqueta y pasa sus tests sin problemas**, y la parte de seguridad
que el profesor valora está bien resuelta: cero imports `javax.*`, bcrypt con cost 12,
`PreparedStatement` en todas las queries y `try-with-resources` en todos los DAOs.

Sin embargo, **dos funcionalidades del MVP están rotas en tiempo de ejecución y los tests no
las detectan**, porque no hay pruebas que las cubran y porque los errores se tragan con
`printStackTrace()` sin llegar nunca al usuario. Las verifiqué ejecutando código real contra
la base H2, no por inspección visual:

1. **El CRUD de servicios no persiste nada.** Desactivar un servicio, cambiarle el nombre o
   el precio parece funcionar en pantalla, pero se revierte solo en la siguiente petición.
2. **La baja lógica de clientes falla siempre** con una violación de constraint.

Ambos tienen la misma raíz de fondo: decisiones de configuración de la base H2. Son
corregibles con cambios pequeños y localizados. Hasta que se arreglen, el sistema no debería
demostrarse en vivo tocando esos botones.

**Veredicto: regular.** Base sólida y bien construida, con dos defectos funcionales graves
que hay que corregir antes de la entrega.

---

## 🚨 Problemas graves

### G1 — El CRUD de servicios es inoperante: todo cambio se revierte solo

**Qué pasa.** La URL de conexión en `ConexionDB.java` lleva `INIT=RUNSCRIPT FROM
'classpath:schema.sql'\;RUNSCRIPT FROM 'classpath:data.sql'`. La cláusula `INIT` de H2 **se
ejecuta cada vez que se abre una conexión**, no una sola vez al arrancar. Y como cada método
de los DAOs abre su propia conexión, `data.sql` se re-ejecuta en la práctica en cada
operación. Los `MERGE ... KEY(id)` de `data.sql` sobrescriben las filas 1 a 5 de `servicios`
devolviéndolas a sus valores originales, incluido `activo = true`.

**Verificación empírica.** Ejecuté esto contra la base real:

```
Servicio 1 original : Terapia Access Bars | activo=true | $500.00
--- el usuario lo DESACTIVA desde la UI ---
Servicio 1 ahora    : activo=true          <-- la desactivación se perdió
--- el usuario le cambia el nombre y el precio ---
Tras editar         : Terapia Access Bars | $500.00   <-- la edición se perdió
```

Y la prueba directa de que el script se re-ejecuta:

```
tras DELETE, en la MISMA conexión: 0
en una conexión NUEVA:            5   <-- data.sql volvió a correr
```

**Impacto.** Los botones "Editar" y "Desactivar" de la pantalla de servicios no hacen nada
permanente y **no muestran ningún error**: el usuario cree que guardó. Es el peor tipo de
fallo porque es silencioso. Afecta solo a los servicios con id 1-5 (los de `data.sql`); un
servicio nuevo creado desde la UI sí persiste, porque `data.sql` no lo toca.

**Nota importante para el cambio de `data.sql` que viene en la Parte 2.6:** el archivo nuevo
que traen las instrucciones usa `INSERT INTO servicios (...)` sin `MERGE` y sin `id`
explícito. Si se aplica tal cual con el `INIT` actual, **los 12 servicios se insertarían de
nuevo en cada conexión** y la tabla crecería sin límite hasta llenar el disco. No se debe
aplicar en formato `INSERT` mientras `INIT=RUNSCRIPT` siga activo.

**🔧 Recomendación.** Sacar la inicialización del `INIT` de la URL y ejecutarla una sola vez
al arrancar la aplicación (un `ServletContextListener`, o un bloque estático con una bandera
que corra los scripts si las tablas están vacías). Alternativa mínima si se quiere tocar lo
menos posible: dejar el `INIT` solo con `schema.sql` (que es idempotente por el `CREATE TABLE
IF NOT EXISTS`) y sembrar los datos una única vez. Manteniendo `MERGE` en `data.sql` mientras
tanto, nunca `INSERT`.

---

### G2 — La baja lógica de clientes falla siempre (violación de CHECK)

**Qué pasa.** `ClienteDAO.bajaLogica()` ejecuta:

```java
UPDATE clientes SET estatus='INACTIVO' WHERE id=?
```

Pero el CHECK de `schema.sql` no admite ese valor:

```sql
estatus VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estatus IN ('ACTIVO','LAPSADO','NUEVO'))
```

`'INACTIVO'` no está en la lista, así que H2 rechaza el UPDATE.

**Verificación empírica:**

```
Cliente insertado id=1 | activos antes = 1
--- llamando bajaLogica(1) ---
JdbcSQLIntegrityConstraintViolationException: Violación de Check constraint: "CONSTRAINT_64"
UPDATE clientes SET estatus='INACTIVO' WHERE id=?
activos después = 1
estatus tras la baja = NUEVO       <-- el cliente sigue igual
```

**Impacto.** Dar de baja un cliente nunca funciona. La excepción se captura con
`printStackTrace()`, así que va a la consola de Tomcat y el usuario ve un redirect normal a
la lista, con el cliente todavía ahí. Además, la consulta `listarPaginado` filtra por
`estatus != 'INACTIVO'`, un valor que por diseño nunca puede existir en la tabla: ese filtro
es código muerto.

**🔧 Recomendación.** Añadir `'INACTIVO'` a la lista del CHECK en `schema.sql`. Es un cambio
de una línea y deja coherentes el esquema, el DAO y la consulta de listado.

---

### G3 — Sin protección CSRF en ningún formulario

`grep -riE 'csrf|token'` sobre `src/main/` no devuelve **ninguna** coincidencia. Los cuatro
formularios POST (login, alta/edición de cliente, alta/edición de servicio, baja) aceptan
peticiones sin verificar origen. Con una sesión abierta, una página externa podría enviar un
POST a `/clientes` y dar de alta o modificar registros.

**🔧 Recomendación.** Generar un token aleatorio por sesión al hacer login, guardarlo en la
`HttpSession`, emitirlo como `<input type="hidden">` en cada formulario y compararlo al
inicio de cada `doPost`. No requiere dependencias nuevas: `SecureRandom` y `UUID` bastan.

---

### G4 — `AuthFilter` bloqueará el logo y el favicon antes de que existan

Este lo señalo porque **impide la Parte 2.1 antes de empezarla**. El filtro considera
estáticas solo estas rutas:

```java
boolean isStatic = uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/");
```

Las instrucciones piden crear `src/main/webapp/img/` y un `favicon.svg` en la raíz. Ninguna
de las dos coincide con el patrón (`/img/` no es `/images/`), así que en la pantalla de login
—donde por definición no hay sesión— **el navegador pedirá el logo, el filtro lo redirigirá al
login y la imagen no cargará**. El logo se vería roto justo en la pantalla donde más se luce.

**🔧 Recomendación.** Ampliar la condición a `/img/`, `/favicon`, y de paso `/webjars/` o
fuentes si se añaden después. Conviene además comparar contra la ruta relativa al contexto
(`req.getRequestURI().substring(req.getContextPath().length())`) en vez de `contains()`, que
es más laxo de lo necesario.

---

## ⚠️ Problemas menores

- **Errores tragados en toda la capa DAO.** 14 llamadas a `printStackTrace()`
  (`ClienteDAO` 6, `ServicioDAO` 6, `UsuarioDAO` 1, `ConexionDB` 1). Los fallos de base de
  datos nunca llegan a la vista: los métodos devuelven `null`, `0` o listas vacías como si
  todo hubiera ido bien. Esto es lo que hizo invisibles a G1 y G2. Mínimo: registrar con
  `java.util.logging` y propagar una excepción de aplicación para que el servlet muestre un
  mensaje.

- **`Integer.parseInt` sin protección.** En `ClienteServlet` y `ServicioServlet` los
  parámetros `id` y `page` se parsean directo. Una URL como `/clientes?id=abc` lanza
  `NumberFormatException` y devuelve un **error 500** en lugar de un 400 o un redirect.

- **Sin pool de conexiones.** Cada método DAO abre y cierra su propia `Connection`. Con el
  `INIT=RUNSCRIPT` actual, además, cada apertura re-ejecuta dos scripts SQL completos. Para el
  volumen de este MVP no es un problema real, pero conviene mencionarlo como deuda técnica
  consciente en la documentación.

- **Construcción de SQL por concatenación en `ServicioDAO.listarPorTipo`.** La línea
  `"SELECT * FROM servicios WHERE activo = TRUE " + (tipo != null ... ? "AND tipo = ?" : "")`
  concatena, pero **no es una vulnerabilidad**: lo que se concatena es un literal fijo del
  código y el valor del filtro sí viaja parametrizado. Lo anoto solo como patrón frágil que
  invita a un error futuro si alguien lo extiende sin cuidado.

- **Cobertura de tests escasa y desalineada con el riesgo.** Los 6 tests cubren dos modelos,
  el hash de contraseñas, la búsqueda de usuario y un alta de cliente. **No hay ni un test de
  `bajaLogica`, ni de `ServicioDAO`, ni de los servlets** — precisamente las dos zonas donde
  están G1 y G2. Un test de "doy de baja y compruebo que ya no aparece" habría cazado G2 al
  instante.

- **`GenerateHash.java` y `GenerateHash.class` sueltos en la raíz.** Utilidad desechable para
  generar los hashes de `data.sql`. El `.class` sí lo cubre el `.gitignore`, el `.java` no.
  Conviene moverlo a `src/test/` o borrarlo antes de entregar.

- **El CI apunta a ramas que no existen.** `ci.yml` dispara en `master` y `develop`, pero el
  repositorio **no tiene ni un solo commit** (ver más abajo). El workflow en sí está bien
  escrito: checkout v3, JDK 11 Temurin, caché de Maven, `mvn -B test`.

- **Desalineación de versiones de Java.** El `pom.xml` compila con `source/target 11`, el CI
  usa JDK 11, pero el JDK local es 21 y Tomcat 10.1.55 corre sobre él. Funciona, pero conviene
  unificar o dejar constancia de por qué se fija en 11.

---

## 🚧 Bloqueo de proceso: el repositorio no tiene historial

`git log` está vacío y `git status` marca **todo el proyecto como no rastreado** (`??`). No
existen las ramas `master` ni `develop`.

Esto choca con la regla 5 del encargo (*"trabaja en una branch nueva
`feature/ajustes-cosmeticos-y-roadmap` que sale de `develop`, al terminar haz PR a
`develop`"*): no se puede ramificar desde algo que no existe, ni abrir un PR sin remoto.

**🔧 Recomendación.** Antes de tocar código: commit inicial del MVP tal como está en `master`,
crear `develop` a partir de ahí, y de ahí sí la rama de trabajo. Así el historial refleja la
secuencia real y el PR tendrá una base contra la que comparar. Esto además es evidencia de
disciplina de versionado, que es justo lo que el profesor valoró en el Consultorio Clínico.

---

## ✅ Lo que está bien

- **Cero `javax.*`.** Verificado con grep sobre `.java`, `.jsp` y `.xml`: todo es `jakarta.*`,
  y el `web.xml` declara correctamente el namespace de Jakarta EE 10 con `version="6.0"`. Es
  justo el detalle que el profesor elogió en CS13304.
- **Separación MVC correcta.** Los servlets solo leen parámetros, delegan en el DAO y hacen
  forward o redirect. No hay SQL ni lógica de negocio en los servlets, ni acceso a `request`
  desde los DAOs. `DashboardServlet` es un buen ejemplo: pide dos contadores y una lista, y
  pasa el control a la vista.
- **`try-with-resources` bien aplicado en todos los DAOs**, incluidos los `ResultSet` anidados.
  No hay fugas de `Connection`: revisé los 14 métodos de acceso a datos.
- **Contraseñas con bcrypt cost 12.** `PasswordUtil.hashPassword` usa
  `BCrypt.withDefaults().hashToString(12, ...)`. En la base solo hay hashes `$2a$12$...`,
  ninguna contraseña en claro. Verifiqué que ambos usuarios de ejemplo autentican
  correctamente: `admin/admin123` y `staff/staff123` → LOGIN OK.
- **`PreparedStatement` en el 100% de las queries**, con los valores siempre parametrizados.
  Probé además que el CHECK de `tipo` en `servicios` rechaza valores inventados.
- **Los tests corren limpios y sin flakes.** 6/6 en 3.8 s, y repetidos varias veces dan el
  mismo resultado. Usan H2 en memoria con `setTestMode(true)`, bien aislados de la base de
  archivo.
- **El WAR compila sin warnings críticos.** `mvn clean package` termina en BUILD SUCCESS.
- **`schema.sql` es idempotente** gracias a `CREATE TABLE IF NOT EXISTS`, que es lo que evita
  que el problema G1 sea aún peor de lo que ya es.
- **La paleta de `holhins.css` ya es la oficial.** Las cuatro variables coinciden exactamente
  con los valores pedidos (`#4A6363`, `#7D9A9A`, `#B5C5BE`, `#F5F1E8`) y las fuentes ya son
  Cambria para títulos y Calibri para el cuerpo. **No hay ningún `#2E7D32` ni verdes ajenos**
  en el proyecto: ese punto de la Parte 2.4 ya está cumplido.

---

## 🔧 Orden recomendado de corrección

1. **G2** (una línea en `schema.sql`) — el más barato y desbloquea la baja de clientes.
2. **G4** (una línea en `AuthFilter`) — hay que hacerlo *antes* de meter el logo.
3. **G1** (rediseñar la inicialización de la base) — el más laborioso y el que condiciona
   cómo se aplica el `data.sql` nuevo de la Parte 2.6.
4. **G3** (token CSRF) — mayor esfuerzo; si el tiempo aprieta, se puede documentar como
   limitación conocida en el README y dejarlo para la Iteración 2.
5. Menores: parseo defensivo de `id`, logging en lugar de `printStackTrace`, y añadir tests
   de `bajaLogica` y `ServicioDAO` que habrían cazado G1 y G2.

---

## Nota sobre el logo (para la Parte 2.2)

Inspeccioné `/home/matsukiender/Descargas/Logo de casa.svg`: 81 KB, viewBox 375×375, y **todos
sus trazos son `fill="#ffffff"`** — es un logo íntegramente blanco, sin más colores.

Consecuencia práctica: se verá perfecto sobre el navbar verde salvia (`#4A6363`), pero será
**invisible sobre el crema del login** (`#F5F1E8`), que es fondo claro. Habrá que forzar el
color con CSS en ese contexto (por ejemplo con `filter` o sirviendo el SVG en línea para poder
sobrescribir el `fill`). Lo mismo aplica al favicon, que en una pestaña clara desaparecería.
Lo dejo señalado para resolverlo al aplicar los ajustes cosméticos.

---

*Auditoría de solo lectura: no se modificó ningún archivo del proyecto. Las verificaciones se
hicieron con clases de sonda compiladas fuera del árbol del proyecto, contra la base H2 en
memoria.*
