-- ============================================================
-- Taller Mecanico - creacion de base de datos MySQL 8
-- Ejecutar como root:
--   mysql -h 127.0.0.1 -P 3308 -u root < db/crear-bd.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS bd_mecanica
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'taller_app'@'localhost' IDENTIFIED BY 'taller123';
CREATE USER IF NOT EXISTS 'taller_app'@'127.0.0.1' IDENTIFIED BY 'taller123';

GRANT ALL PRIVILEGES ON bd_mecanica.* TO 'taller_app'@'localhost';
GRANT ALL PRIVILEGES ON bd_mecanica.* TO 'taller_app'@'127.0.0.1';

FLUSH PRIVILEGES;

-- Verificacion
SHOW DATABASES LIKE 'bd_mecanica';
