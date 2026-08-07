-- admin / admin123 y staff / staff123 (hash generado via BCrypt)
-- cost 12: $2a$12$6/YQfQe.S41HDBz40rF5p.uPIfSj2HnJgZ9m3Z3B/rZ3jM7yS/ZpC para admin123
-- cost 12: $2a$12$n76rV4b6j/R1YwYIqz3/mOTc7J3j3I8n.D8uM2QkK/mYvXf0y5aE. para staff123

MERGE INTO usuarios (username, password_hash, nombre_completo, rol, activo) KEY(username) VALUES
('admin', '$2a$12$.WrXD96YhPcFIHJ3eOPIwuxiBxdwBvrAPEXL7X4zKgFuZfJNZn7cS', 'Ana María Trejo Holhins', 'ADMIN', true),
('staff', '$2a$12$RYU7jYOz75mCKoyRLvbjZOIvPOLpVnZ0W.s4fy/nt47O82yHqeqnG', 'Usuario Staff', 'STAFF', true);

MERGE INTO servicios (id, nombre, descripcion, tipo, duracion_minutos, precio, activo) KEY(id) VALUES
(1, 'Terapia Access Bars', 'Liberación de puntos de vista en la cabeza', 'TERAPIA', 60, 500.00, true),
(2, 'Masaje Holístico', 'Masaje integral para equilibrar cuerpo y mente', 'TERAPIA', 60, 600.00, true),
(3, 'Reiki Nivel 1', 'Iniciación al sistema de sanación natural', 'CURSO', 480, 1500.00, true),
(4, 'Diplomado de Masaje Holístico', 'Formación completa como terapeuta', 'DIPLOMADO', 7200, 12000.00, true),
(5, 'Taller de Barras', 'Certificación internacional en Access Bars', 'TALLER', 480, 7500.00, true);
