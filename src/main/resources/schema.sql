-- ============================================================================
-- Esquema de la base de Casa Holhins (H2)
--
-- Se ejecuta una vez al arrancar, desde ConexionDB.inicializarSiHaceFalta().
-- Todas las tablas van con CREATE TABLE IF NOT EXISTS para que correr el script
-- de nuevo no truene ni borre nada.
-- ============================================================================

-- Usuarios que entran al sistema. No guardamos contrasenas en claro, solo el
-- hash de bcrypt (columna generosa de 255 porque el hash trae salt y cost).
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN','STAFF')),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clientes de Casa Holhins.
--
-- El estatus lleva 'INACTIVO' a proposito: asi es como marcamos una baja.
-- Nunca borramos un cliente de verdad (baja logica), porque su historial sirve
-- para el seguimiento y porque un DELETE no se deshace. ClienteDAO.bajaLogica()
-- escribe justamente ese valor, y listarPaginado() filtra por != 'INACTIVO'.
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(100),
    fecha_nacimiento DATE,
    notas TEXT,
    estatus VARCHAR(20) DEFAULT 'ACTIVO' CHECK (estatus IN ('ACTIVO','LAPSADO','NUEVO','INACTIVO')),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_visita TIMESTAMP
);

-- Catalogo de servicios: terapias, talleres, cursos y diplomados.
--
-- duracion_minutos admite valores grandes a proposito, porque un diplomado se
-- mide en decenas de horas (3600 minutos = 60 horas de formacion).
-- El precio va en DECIMAL y no en DOUBLE: con dinero nunca se usa punto
-- flotante, porque 0.1 + 0.2 no da exactamente 0.3 y los centavos se pierden.
CREATE TABLE IF NOT EXISTS servicios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('TERAPIA','CURSO','DIPLOMADO','TALLER')),
    duracion_minutos INT,
    precio DECIMAL(10,2) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
