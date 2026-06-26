-- =====================================================================
-- Script de creacion de base de datos y tabla para Registro de Personas
-- Motor: MySQL 8
-- =====================================================================

CREATE DATABASE IF NOT EXISTS registro_personas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE registro_personas;

CREATE TABLE IF NOT EXISTS personas (
    dni                         VARCHAR(15)  NOT NULL,
    apellido_nombre             VARCHAR(150) NOT NULL,
    nacionalidad                VARCHAR(80)  NOT NULL,
    edad                        TINYINT UNSIGNED NOT NULL,
    domicilio                   VARCHAR(200) NOT NULL,
    apellido_nombre_padre       VARCHAR(150) NOT NULL,
    domicilio_padre             VARCHAR(200) NOT NULL,

    CONSTRAINT pk_personas PRIMARY KEY (dni),
    CONSTRAINT chk_personas_edad CHECK (edad >= 0 AND edad <= 130)
) ENGINE = InnoDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_spanish_ci;

-- Usuario de aplicacion dedicado (recomendado: no usar root desde la app)
-- Ajustar password segun politica de seguridad del entorno.
-- CREATE USER IF NOT EXISTS 'app_registro'@'%' IDENTIFIED BY 'CambiarEstaClave!2026';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON registro_personas.personas TO 'app_registro'@'%';
-- FLUSH PRIVILEGES;
