# Reporte de cierre — Fase III

**Fecha:** 7 de agosto de 2026
**Repositorio:** https://github.com/matsukiender-star/casa-holhins-web

---

## ✅ Lo que quedó listo

### Bugs (PASO 1)

Las pruebas de aceptación encontraron **dos bugs bloqueantes**, causados por un tercer problema de fondo. Los tres están corregidos y verificados end-to-end contra la base de producción.

| Bug | Estado |
|---|---|
| No se podía dar de baja a un cliente | ✅ Corregido |
| No se podía crear un servicio nuevo | ✅ Corregido |
| Los fallos de escritura no se mostraban en ningún lado | ✅ Corregido |
| La ruta de la base depende del directorio de arranque de Tomcat | ⚠️ Documentado, diferido a Fase IV |
| Saludo con nombre completo (petición de la Directora) | ✅ Atendido |

Las correcciones históricas **G1–G4 se verificaron y siguen vigentes**. Detalle completo en `BUGS_ENCONTRADOS.md`.

### Branches (PASO 2)

- `master` creada y establecida como **rama por defecto** del repositorio.
- `main` **conservada**, no borrada. Se optó por crear `master` junto a `main` en lugar de renombrar: el procedimiento original (`git push origin --delete main`) habría sido rechazado por GitHub, porque no se puede eliminar la rama por defecto antes de cambiarla.
- El trigger de `pull_request` del CI solo escuchaba `develop`, de modo que **los PR hacia `master` no disparaban integración continua**. Corregido: ahora escucha ambas ramas.

### Milestones e Issues (PASO 3)

- **2 milestones**, ambos cerrados: **Beta** (9 issues) y **GA** (3 issues).
- **12 issues**, todos cerrados, cada uno con **Descripción · Análisis · Solución implementada · Criterios de aceptación**, y con referencias a los commits reales del repositorio.

Se crearon 12 en lugar de los 10 previstos: la protección CSRF se separó en su propio issue (#8), y se añadió el #12 para el bug encontrado en pruebas de aceptación. El #12 es probablemente la mejor evidencia de proceso del conjunto, porque documenta un problema real con su análisis de causa raíz.

### Pull Requests (PASO 4)

**4 PR, todos mergeados**, dejando visible el flujo de tres niveles:

| PR | Origen → Destino |
|----|------------------|
| #13 | `feature/correccion-bugs-crud` → `develop` |
| #14 | `docs/final-touches` → `develop` |
| #15 | `develop` → `master` |
| #16 | `develop` → `master` |

### Tag (PASO 5)

`v1.0.0` existe en local y en remoto. No requirió acción.

> **Opcional:** el tag apunta a un commit anterior a las correcciones de hoy. Si prefieres que la versión etiquetada incluya los arreglos, puede crearse un `v1.1.0` sobre el `master` actual. No es necesario para la Fase III.

### CI (PASO 6)

**Verde.** Siete corridas nuevas, todas con conclusión `success`, incluidas las de `pull_request` hacia `master` que antes no se disparaban.

**14 pruebas JUnit** (antes 9). La adición relevante es `MigracionTest`, que degrada la base a propósito para reproducir el estado de la base de producción y verifica que la migración la repara — cubriendo un punto ciego real de la suite anterior.

### Diagrama (PASO 7)

`arquitectura_casa_holhins.png` versionado en la raíz (390 KB) y referenciado desde el README con ruta relativa correcta.

### Documento de entrega (PASO 8)

`Fase3_Entrega_Carrizales_Trejo.md` en la raíz del repositorio, con la evidencia de los cuatro criterios.

### Paquete (PASO 9)

```
~/Documentos/Fase3_Carrizales_Trejo.zip   (6.7 MB)
```

Contiene el documento de entrega, el reporte de bugs, el diagrama, el WAR compilado, la carta de autorización y la carpeta `capturas/`.

---

## 📸 Lo que tienes que hacer tú

### 1. Tomar las capturas de pantalla

Están listadas con sus URLs exactas en `Fase3_Entrega/capturas/_QUE_CAPTURAR.txt`. Son diez, todas de GitHub y Trello.

### 2. Poblar el tablero de Trello

**Este es el punto más importante que queda.** El tablero está vacío o casi, y el Criterio 2 vale **25 puntos**.

El documento de entrega describe la estructura que debe tener, así que hay que construirla para que lo descrito y lo real coincidan:

- **Listas:** Backlog · En progreso · En revisión · Terminado
- **12 tarjetas** en *Terminado*, una por cada issue de GitHub, **con el mismo título** (los tienes en la §1.3 del documento de entrega)
- **Etiquetas:** Backend · Frontend · Docs · DevOps · Testing · Bug
- Idealmente, activar el **Power-Up de GitHub** y vincular cada tarjeta con su issue: es lo que demuestra la *integración* que pide el criterio, no solo la existencia del tablero

Verifica también que el enlace de invitación siga activo y dé acceso de lectura al evaluador.

### 3. Convertir el documento a PDF

`Fase3_Entrega_Carrizales_Trejo.md` → PDF, e insertar las capturas en los lugares marcados con *[Insertar aquí...]*.

### 4. Regenerar el ZIP

Después de añadir las capturas y el PDF:

```bash
cd ~/Documentos && rm -f Fase3_Carrizales_Trejo.zip && zip -r Fase3_Carrizales_Trejo.zip Fase3_Entrega/
```

---

## 🚨 Pendientes y observaciones

**Ninguno bloquea la entrega.**

1. **Trello vacío** — 25 puntos en juego. Es lo único con impacto real en la calificación que sigue pendiente. Ver arriba.
2. **Limitación conocida B4** — la ruta de la base depende del directorio de arranque de Tomcat. Documentada en el README y en `BUGS_ENCONTRADOS.md`, con mitigación efectiva (arrancar desde `bin/`). Diferida a Fase IV.
3. **Petición de la Directora diferida** — ver los servicios que ha tomado cada cliente. Requiere una tabla de relación cliente–servicio, que es la base del módulo de Agenda ya previsto como Iteración 2 del roadmap. No es un ajuste de pantalla.
4. **Sin archivo `LICENSE`** — el README declara MIT pero no existe el archivo, así que GitHub no muestra la licencia en la barra lateral. Treinta segundos desde el asistente de GitHub.
5. **Enlaces rotos en la Wiki** — `Home.md` apunta a `../README.md` y a `../REPORTE_IMPLEMENTACION.md`; el segundo ya no existe y las rutas relativas no resuelven del wiki al repositorio. Menor, pero visible si el evaluador entra a la Wiki.

---

## 🔐 IMPORTANTE: revocar el token de GitHub

El *personal access token* está en texto plano dentro de `.git/config`:

```
https://matsukiender-star:ghp_...@github.com/matsukiender-star/casa-holhins-web.git
```

**Este token tiene permisos muy amplios** — entre otros `repo`, `workflow`, `admin:org`, `admin:enterprise`, `delete:packages` y `gist`. No se limita a este repositorio: alcanza a toda tu cuenta y a las organizaciones donde tengas acceso administrativo. Cualquiera que lo lea, en tu máquina o en un respaldo, tiene ese mismo alcance.

**En cuanto entregues:**

1. Ve a **GitHub → Settings → Developer settings → Personal access tokens** y **revócalo**.
2. Reconfigura el remoto sin credenciales incrustadas:
   ```bash
   cd ~/dev/casa-holhins-web
   git remote set-url origin https://github.com/matsukiender-star/casa-holhins-web.git
   ```
3. Autentícate con un *credential helper* o con `gh auth login` en lugar de incrustar el token en la URL.

> Nota: el token nunca estuvo en el repositorio público — `.git/config` no se versiona. El riesgo es local, pero el alcance del token lo hace serio igualmente.

---

## Resumen de evidencia

| Criterio | Elemento | Estado |
|----------|----------|--------|
| 1 (30 pts) | Branches, Issues, Milestones, PRs | ✅ Completo |
| 2 (25 pts) | Tablero de Trello | ⚠️ Enlazado; **falta poblarlo** |
| 3 (25 pts) | CI con pruebas JUnit | ✅ Verde, 14 pruebas |
| 4 (20 pts) | Diagrama de arquitectura | ✅ Completo |
