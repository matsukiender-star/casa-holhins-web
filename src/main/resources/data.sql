-- ============================================================================
-- Datos iniciales de Casa Holhins
--
-- Este script lo corre ConexionDB una sola vez, cuando detecta que la base esta
-- vacia. Aun asi uso MERGE con el id explicito en lugar de INSERT: si algun dia
-- alguien lo re-ejecuta a mano, actualiza las filas en vez de duplicarlas.
-- Con INSERT plano cada corrida agregaria los servicios otra vez.
-- ============================================================================

-- Usuarios del sistema.
-- Las contrasenas van hasheadas con bcrypt cost 12 (ver PasswordUtil), nunca en
-- claro. Aunque alguien viera este archivo, del hash no se saca la contrasena.
-- Credenciales de prueba: admin/admin123 y staff/staff123.
--
-- 'admin' es la Directora General; 'staff' representa a la secretaria, que es
-- quien coordina las citas en la operacion real de Casa Holhins.
MERGE INTO usuarios (username, password_hash, nombre_completo, rol, activo) KEY(username) VALUES
('admin', '$2a$12$.WrXD96YhPcFIHJ3eOPIwuxiBxdwBvrAPEXL7X4zKgFuZfJNZn7cS', 'Ana María Trejo Holhins', 'ADMIN', true),
('staff', '$2a$12$RYU7jYOz75mCKoyRLvbjZOIvPOLpVnZ0W.s4fy/nt47O82yHqeqnG', 'Secretaria', 'STAFF', true);

-- Catalogo real de servicios, levantado en la entrevista con la Directora.
-- No son datos de relleno: son los servicios que Casa Holhins ofrece hoy, con
-- sus duraciones y precios reales, por eso los diplomados tienen duraciones de
-- miles de minutos (3600 min = 60 horas de formacion certificada).
MERGE INTO servicios (id, nombre, descripcion, tipo, duracion_minutos, precio, activo) KEY(id) VALUES
(1,  'Consulta Holística',           'Consulta inicial para conocer al cliente y recomendar terapias', 'TERAPIA',   60,   250.00, true),
(2,  'Terapia Emocional',            'Terapia de liberación emocional profunda',                       'TERAPIA',   90,   350.00, true),
(3,  'Access Bars',                  'Terapia de puntos energéticos en la cabeza para liberar bloqueos','TERAPIA',   90,   400.00, true),
(4,  'Masaje Tao',                   'Masaje energético tradicional con técnicas taoístas',            'TERAPIA',   60,   550.00, true),
(5,  'Masaje Relajante',             'Masaje corporal para reducir estrés y tensión muscular',         'TERAPIA',   60,   250.00, true),
(6,  'Masaje Completo',              'Masaje corporal completo con aceites esenciales',                'TERAPIA',   90,   550.00, true),
(7,  'Terapia de Cuencos',           'Sesión de sonoterapia con cuencos tibetanos',                    'TERAPIA',   60,   400.00, true),
(8,  'Cirugía Cuántica',             'Sanación energética a distancia',                                'TERAPIA',   60,   440.00, true),
(9,  'Maratón de Terapias',          'Sesión intensiva de terapias combinadas (1-3 días)',             'TALLER',   240,  1500.00, true),
(10, 'Diplomado Masaje Holístico',   'Diplomado con certificación SEP-CONOCER',                        'DIPLOMADO',3600, 15000.00, true),
(11, 'Diplomado Cuencos',            'Diplomado de terapia con cuencos',                               'DIPLOMADO',2400,  2500.00, true),
(12, 'Taller de Access Bars',        'Taller introductorio a la técnica de Access Bars',               'TALLER',   480,  3500.00, true);
