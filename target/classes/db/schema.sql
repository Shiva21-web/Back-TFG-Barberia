-- ============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS
-- Sistema de Gestión de Citas - Peluquería
-- ============================================

-- Borrar la base de datos si existe
DROP DATABASE IF EXISTS peluqueria_db;

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS peluqueria_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE peluqueria_db;

-- ============================================
-- TABLA: CLIENTE
-- ============================================
-- Almacena la información de los clientes de la peluquería
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL DEFAULT 'password123',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Índices para mejorar el rendimiento de búsquedas
    INDEX idx_apellidos (apellidos),
    INDEX idx_email (email),
    INDEX idx_telefono (telefono)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: SERVICIO
-- ============================================
-- Almacena los servicios que ofrece la peluquería
CREATE TABLE IF NOT EXISTS servicio (
    id_servicio BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT,
    duracion_minutos INT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    
    -- Validaciones a nivel de base de datos
    CONSTRAINT chk_duracion CHECK (duracion_minutos > 0),
    CONSTRAINT chk_precio CHECK (precio > 0),
    
    -- Índice para búsquedas por nombre
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: CITA
-- ============================================
-- Almacena las citas programadas
CREATE TABLE IF NOT EXISTS cita (
    id_cita BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_cliente BIGINT NOT NULL,
    id_servicio BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado ENUM('CONFIRMADA', 'COMPLETADA', 'CANCELADA') DEFAULT 'CONFIRMADA',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Claves foráneas
    CONSTRAINT fk_cita_cliente 
        FOREIGN KEY (id_cliente) 
        REFERENCES cliente(id_cliente)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    CONSTRAINT fk_cita_servicio 
        FOREIGN KEY (id_servicio) 
        REFERENCES servicio(id_servicio)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    -- Índices para mejorar el rendimiento
    INDEX idx_fecha (fecha),
    INDEX idx_fecha_hora (fecha, hora),
    INDEX idx_estado (estado),
    INDEX idx_cliente (id_cliente),
    INDEX idx_servicio (id_servicio),
    
    -- Índice único compuesto para evitar citas duplicadas en el mismo horario
    UNIQUE INDEX idx_fecha_hora_unique (fecha, hora)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: USUARIO_ADMIN
-- ============================================
-- Almacena los usuarios administradores (peluqueros)
CREATE TABLE IF NOT EXISTS usuario_admin (
    id_admin BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Índices para búsquedas
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- VISTAS ÚTILES
-- ============================================

-- Vista: Citas con información completa
CREATE OR REPLACE VIEW vista_citas_completas AS
SELECT 
    c.id_cita,
    c.fecha,
    c.hora,
    c.estado,
    c.observaciones,
    c.fecha_creacion,
    cl.id_cliente,
    cl.nombre AS cliente_nombre,
    cl.apellidos AS cliente_apellidos,
    cl.telefono AS cliente_telefono,
    cl.email AS cliente_email,
    s.id_servicio,
    s.nombre AS servicio_nombre,
    s.descripcion AS servicio_descripcion,
    s.duracion_minutos,
    s.precio
FROM cita c
INNER JOIN cliente cl ON c.id_cliente = cl.id_cliente
INNER JOIN servicio s ON c.id_servicio = s.id_servicio;

-- Vista: Resumen de citas por cliente
CREATE OR REPLACE VIEW vista_resumen_clientes AS
SELECT 
    cl.id_cliente,
    cl.nombre,
    cl.apellidos,
    cl.telefono,
    cl.email,
    COUNT(c.id_cita) AS total_citas,
    SUM(CASE WHEN c.estado = 'COMPLETADA' THEN 1 ELSE 0 END) AS citas_completadas,
    SUM(CASE WHEN c.estado = 'CANCELADA' THEN 1 ELSE 0 END) AS citas_canceladas,
    SUM(CASE WHEN c.estado = 'CONFIRMADA' THEN 1 ELSE 0 END) AS citas_confirmadas
FROM cliente cl
LEFT JOIN cita c ON cl.id_cliente = c.id_cliente
GROUP BY cl.id_cliente, cl.nombre, cl.apellidos, cl.telefono, cl.email;

-- Vista: Servicios más solicitados
CREATE OR REPLACE VIEW vista_servicios_populares AS
SELECT 
    s.id_servicio,
    s.nombre,
    s.precio,
    COUNT(c.id_cita) AS veces_solicitado,
    SUM(s.precio) AS ingresos_totales
FROM servicio s
LEFT JOIN cita c ON s.id_servicio = c.id_servicio AND c.estado = 'COMPLETADA'
GROUP BY s.id_servicio, s.nombre, s.precio
ORDER BY veces_solicitado DESC;

-- ============================================
-- PROCEDIMIENTOS ALMACENADOS
-- ============================================

-- Procedimiento: Obtener citas del día
DELIMITER //
CREATE PROCEDURE sp_citas_del_dia(IN p_fecha DATE)
BEGIN
    SELECT 
        c.id_cita,
        c.fecha,
        c.hora,
        c.estado,
        CONCAT(cl.nombre, ' ', cl.apellidos) AS cliente,
        s.nombre AS servicio,
        s.duracion_minutos,
        s.precio
    FROM cita c
    INNER JOIN cliente cl ON c.id_cliente = cl.id_cliente
    INNER JOIN servicio s ON c.id_servicio = s.id_servicio
    WHERE c.fecha = p_fecha
    ORDER BY c.hora;
END //
DELIMITER ;

-- Procedimiento: Verificar disponibilidad de horario
DELIMITER //
CREATE PROCEDURE sp_verificar_disponibilidad(
    IN p_fecha DATE,
    IN p_hora TIME,
    IN p_duracion_minutos INT,
    OUT p_disponible BOOLEAN
)
BEGIN
    DECLARE citas_conflicto INT;
    
    SELECT COUNT(*) INTO citas_conflicto
    FROM cita c
    INNER JOIN servicio s ON c.id_servicio = s.id_servicio
    WHERE c.fecha = p_fecha
    AND c.estado IN ('CONFIRMADA')
    AND (
        -- La nueva cita comienza durante una cita existente
        (p_hora >= c.hora AND p_hora < ADDTIME(c.hora, SEC_TO_TIME(s.duracion_minutos * 60)))
        OR
        -- La nueva cita termina durante una cita existente
        (ADDTIME(p_hora, SEC_TO_TIME(p_duracion_minutos * 60)) > c.hora 
         AND ADDTIME(p_hora, SEC_TO_TIME(p_duracion_minutos * 60)) <= ADDTIME(c.hora, SEC_TO_TIME(s.duracion_minutos * 60)))
        OR
        -- La nueva cita cubre completamente una cita existente
        (p_hora <= c.hora AND ADDTIME(p_hora, SEC_TO_TIME(p_duracion_minutos * 60)) >= ADDTIME(c.hora, SEC_TO_TIME(s.duracion_minutos * 60)))
    );
    
    SET p_disponible = (citas_conflicto = 0);
END //
DELIMITER ;

-- ============================================
-- TRIGGERS
-- ============================================

-- Trigger: Validar que la fecha de la cita no sea en el pasado
DELIMITER //
CREATE TRIGGER trg_validar_fecha_cita_insert
BEFORE INSERT ON cita
FOR EACH ROW
BEGIN
    IF NEW.fecha < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede crear una cita con fecha pasada';
    END IF;
END //
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_validar_fecha_cita_update
BEFORE UPDATE ON cita
FOR EACH ROW
BEGIN
    IF NEW.fecha < CURDATE() AND NEW.fecha != OLD.fecha THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede modificar una cita a una fecha pasada';
    END IF;
END //
DELIMITER ;

-- ============================================
-- COMENTARIOS FINALES
-- ============================================
-- Este script crea todas las tablas necesarias para el sistema de gestión de citas
-- Incluye índices para optimizar consultas frecuentes
-- Contiene vistas para simplificar consultas complejas
-- Implementa procedimientos almacenados para operaciones comunes
-- Define triggers para validaciones automáticas
-- ============================================
