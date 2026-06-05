-- ============================================
-- SCRIPT DE MIGRACIÓN - SISTEMA DE AUTENTICACIÓN
-- Sistema de Gestión de Citas - Peluquería
-- ============================================
-- 
-- IMPORTANTE: Este script es OPCIONAL y redundante si ya ejecutaste
-- schema.sql y data.sql en orden. Lo mantenemos para compatibilidad.
-- 
-- Es seguro ejecutarlo múltiples veces (idempotente).
-- ============================================

USE peluqueria_db;

-- ============================================
-- 1. AÑADIR CAMPO CONTRASEÑA A TABLA CLIENTE (si no existe)
-- ============================================

-- Verificar si la columna ya existe antes de añadirla
-- Nota: Usamos un procedimiento temporal porque MySQL no soporta
-- ALTER TABLE ADD COLUMN IF NOT EXISTS directamente

DELIMITER $$

DROP PROCEDURE IF EXISTS AddColumnIfNotExists$$

CREATE PROCEDURE AddColumnIfNotExists()
BEGIN
    DECLARE column_exists INT;
    
    SELECT COUNT(*) INTO column_exists
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'peluqueria_db'
    AND TABLE_NAME = 'cliente'
    AND COLUMN_NAME = 'contrasena';
    
    IF column_exists = 0 THEN
        ALTER TABLE cliente 
        ADD COLUMN contrasena VARCHAR(255) NOT NULL DEFAULT 'password123';
    END IF;
END$$

DELIMITER ;

CALL AddColumnIfNotExists();
DROP PROCEDURE AddColumnIfNotExists;

-- ============================================
-- 2. CREAR TABLA USUARIO_ADMIN (si no existe)
-- ============================================

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
-- 3. INSERTAR USUARIO ADMIN POR DEFECTO (si no existe)
-- ============================================

-- Insertar admin con username 'admin' y contraseña 'admin'
-- ON DUPLICATE KEY UPDATE evita error si ya existe
INSERT INTO usuario_admin (username, contrasena, nombre, email) 
VALUES ('admin', 'admin', 'Administrador', 'admin@peluqueria.com')
ON DUPLICATE KEY UPDATE username=username;

-- ============================================
-- 4. VERIFICACIÓN
-- ============================================

SELECT '✅ Script migration_auth.sql ejecutado correctamente' AS Mensaje;
SELECT 'Total usuarios admin:' AS Info, COUNT(*) AS Total FROM usuario_admin;
SELECT 'Columna contrasena existe en cliente' AS Info;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================
-- - Este script es seguro para ejecutar múltiples veces (idempotente)
-- - Si schema.sql y data.sql ya se ejecutaron, este script es redundante
-- - Los clientes tendrán contraseña por defecto: 'password123'
-- - El admin tiene username 'admin' y contraseña 'admin'
-- - Las contraseñas se guardan en texto plano (solo para desarrollo/TFG)
-- - En producción real, usar bcrypt o similar
-- ============================================