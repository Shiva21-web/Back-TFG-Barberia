-- ============================================
-- SCRIPT DE DATOS DE PRUEBA - BARBERÍA
-- Sistema de Gestión de Citas
-- ============================================

USE peluqueria_db;

-- ============================================
-- INSERTAR CLIENTES DE PRUEBA - SOLO HOMBRES
-- ============================================

INSERT INTO cliente (nombre, apellidos, telefono, email, contrasena) VALUES
('Juan', 'Martínez Rodríguez', '623456789', 'juan.martinez@email.com', 'password123'),
('Pedro', 'López Gómez', '645678901', 'pedro.lopez@email.com', 'password123'),
('Carlos', 'Rodríguez Martín', '667890123', 'carlos.rodriguez@email.com', 'password123'),
('Miguel', 'Pérez Hernández', '689012345', 'miguel.perez@email.com', 'password123'),
('David', 'Jiménez Moreno', '601234567', 'david.jimenez@email.com', 'password123'),
('Sergio', 'Ramírez Torres', '612345678', 'sergio.ramirez@email.com', 'password123'),
('Javier', 'Morales Vega', '634567890', 'javier.morales@email.com', 'password123'),
('Andrés', 'Castro Núñez', '656789012', 'andres.castro@email.com', 'password123'),
('Roberto', 'Díaz Muñoz', '678901234', 'roberto.diaz@email.com', 'password123'),
('Fernando', 'Gil Romero', '690123456', 'fernando.gil@email.com', 'password123');

-- ============================================
-- INSERTAR SERVICIOS DE PRUEBA - BARBERÍA
-- ============================================
-- NOTA: Todos los servicios duran exactamente 30 minutos

INSERT INTO servicio (nombre, descripcion, duracion_minutos, precio) VALUES
('Corte de pelo hombre', 'Corte tradicional o moderno con lavado incluido', 30, 15.00),
('Barba y bigote', 'Arreglo y perfilado profesional de barba', 30, 10.00),
('Corte + barba', 'Servicio completo: corte de pelo y arreglo de barba', 30, 20.00),
('Rapado', 'Rapado completo con máquina', 30, 12.00),
('Arreglo de cejas', 'Perfilado y arreglo de cejas masculinas', 30, 8.00),
('Afeitado clásico', 'Afeitado tradicional con navaja y toallas calientes', 30, 15.00),
('Corte niño', 'Corte de pelo para niños hasta 12 años', 30, 10.00);

-- ============================================
-- INSERTAR USUARIO ADMIN
-- ============================================

INSERT INTO usuario_admin (username, contrasena, nombre, email) 
VALUES ('admin', 'admin', 'Administrador', 'admin@barberia.com')
ON DUPLICATE KEY UPDATE username=username;

-- ============================================
-- INSERTAR CITAS DE PRUEBA - BARBERÍA
-- ============================================
-- IMPORTANTE: Todos los servicios duran 30 minutos
-- El índice único (fecha, hora) previene citas duplicadas en el mismo horario

-- Citas de hoy (cada 30 minutos)
INSERT INTO cita (id_cliente, id_servicio, fecha, hora, estado, observaciones) VALUES
(1, 1, CURDATE(), '09:00:00', 'COMPLETADA', 'Cliente satisfecho'),
(2, 3, CURDATE(), '09:30:00', 'COMPLETADA', 'Cliente satisfecho'),
(3, 2, CURDATE(), '10:00:00', 'CONFIRMADA', 'Solo arreglo de barba'),
(4, 1, CURDATE(), '10:30:00', 'CONFIRMADA', 'Corte estilo clásico'),
(5, 4, CURDATE(), '11:00:00', 'CONFIRMADA', 'Primera vez en la barbería');

-- Citas de mañana (cada 30 minutos)
INSERT INTO cita (id_cliente, id_servicio, fecha, hora, estado, observaciones) VALUES
(6, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', 'CONFIRMADA', 'Corte regular'),
(7, 6, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:30:00', 'CONFIRMADA', 'Afeitado tradicional'),
(8, 2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00', 'CONFIRMADA', 'Arreglo de barba'),
(9, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:30:00', 'CONFIRMADA', 'Servicio completo'),
(10, 7, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:00:00', 'CONFIRMADA', 'Corte de niño');

-- Citas para dentro de 2 días
INSERT INTO cita (id_cliente, id_servicio, fecha, hora, estado, observaciones) VALUES
(1, 3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:00:00', 'CONFIRMADA', 'Corte + Barba'),
(2, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:30:00', 'CONFIRMADA', 'Rapado completo'),
(3, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:00:00', 'CONFIRMADA', 'Cliente habitual'),
(4, 5, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '10:30:00', 'CONFIRMADA', 'Arreglo de cejas');

-- Citas para dentro de 3 días
INSERT INTO cita (id_cliente, id_servicio, fecha, hora, estado, observaciones) VALUES
(5, 6, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:00:00', 'CONFIRMADA', 'Afeitado clásico'),
(6, 2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:30:00', 'CONFIRMADA', 'Barba y bigote'),
(7, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00:00', 'CONFIRMADA', 'Corte moderno'),
(8, 7, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:30:00', 'CONFIRMADA', 'Primera visita niño');

-- Citas para la próxima semana
INSERT INTO cita (id_cliente, id_servicio, fecha, hora, estado, observaciones) VALUES
(9, 1, DATE_ADD(CURDATE(), INTERVAL 7 DAY), '09:00:00', 'CONFIRMADA', 'Corte regular'),
(10, 3, DATE_ADD(CURDATE(), INTERVAL 7 DAY), '09:30:00', 'CONFIRMADA', 'Servicio completo'),
(1, 2, DATE_ADD(CURDATE(), INTERVAL 8 DAY), '10:00:00', 'CONFIRMADA', 'Arreglo de barba'),
(2, 1, DATE_ADD(CURDATE(), INTERVAL 9 DAY), '10:30:00', 'CONFIRMADA', 'Corte de pelo');

-- Algunas citas canceladas (para datos históricos)
INSERT INTO cita (id_cliente, id_servicio, fecha, hora, estado, observaciones) VALUES
(3, 3, DATE_ADD(CURDATE(), INTERVAL 10 DAY), '09:00:00', 'CANCELADA', 'Cliente canceló'),
(4, 1, DATE_ADD(CURDATE(), INTERVAL 11 DAY), '09:30:00', 'CANCELADA', 'Reprogramada');

-- ============================================
-- VERIFICAR DATOS INSERTADOS
-- ============================================

SELECT 'Clientes registrados:' AS Tabla, COUNT(*) AS Total FROM cliente
UNION ALL
SELECT 'Servicios disponibles:', COUNT(*) FROM servicio
UNION ALL
SELECT 'Citas programadas:', COUNT(*) FROM cita;

SELECT estado, COUNT(*) AS cantidad
FROM cita
GROUP BY estado
ORDER BY cantidad DESC;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================
-- - Sistema convertido a BARBERÍA (solo para hombres)
-- - Todos los servicios duran exactamente 30 minutos
-- - El índice único (fecha, hora) previene citas duplicadas
-- - Las citas están espaciadas cada 30 minutos
-- - Contraseña de todos los clientes: password123
-- - Admin: username=admin, password=admin
-- ============================================