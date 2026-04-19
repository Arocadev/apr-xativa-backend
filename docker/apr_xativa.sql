CREATE DATABASE IF NOT EXISTS apr_xativa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE apr_xativa;

-- ─── USUARIOS ───────────────────────────────────────────────────────────────
CREATE TABLE usuarios (
    id               BIGINT       PRIMARY KEY AUTO_INCREMENT,
    dni              VARCHAR(10)  NOT NULL UNIQUE,
    nombre           VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(150) NOT NULL,
    email            VARCHAR(150) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    rol              ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    tipo             VARCHAR(5)   NOT NULL,       -- A.1, B, C, H.2, R ...
    num_camas        INT          NOT NULL DEFAULT 0,  -- para tipología H.*
    num_plazas       INT          NOT NULL DEFAULT 0,  -- para tipología H.* y C
    num_trabajadores INT          NOT NULL DEFAULT 0,  -- para tipología R
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ─── VEHÍCULOS ──────────────────────────────────────────────────────────────
CREATE TABLE vehiculos (
    id          BIGINT      PRIMARY KEY AUTO_INCREMENT,
    matricula   VARCHAR(10) NOT NULL,
    usuario_id  BIGINT      NOT NULL,
    tipo_acred  ENUM('LIBRE','ACREDITADO') NOT NULL,
    activo      BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vehiculo_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT uq_matricula_usuario UNIQUE (matricula, usuario_id)
);

-- ─── DOCUMENTOS ACREDITATIVOS ───────────────────────────────────────────────
CREATE TABLE documentos (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    usuario_id  BIGINT       NOT NULL,
    tipo_doc    VARCHAR(80)  NOT NULL,   -- 'empadronamiento', 'contrato_alquiler'...
    ruta        VARCHAR(500) NOT NULL,   -- ruta en el servidor
    subido_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_documento_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ─── SOLICITUDES DE AUTORIZACIÓN ────────────────────────────────────────────
CREATE TABLE solicitudes (
    id             BIGINT    PRIMARY KEY AUTO_INCREMENT,
    usuario_id     BIGINT    NOT NULL,
    estado         ENUM('PENDIENTE','APROBADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    observaciones  TEXT,
    admin_id       BIGINT    NULL,          -- admin que la gestiona
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gestionada_at  TIMESTAMP NULL,

    CONSTRAINT fk_solicitud_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_solicitud_admin FOREIGN KEY (admin_id)
        REFERENCES usuarios(id) ON DELETE SET NULL
);

-- ─── DERECHOS DE ACCESO ─────────────────────────────────────────────────────
CREATE TABLE derechos_acceso (
    id            BIGINT      PRIMARY KEY AUTO_INCREMENT,
    usuario_id    BIGINT      NOT NULL,
    vehiculo_id   BIGINT      NOT NULL,
    tipo_derecho  ENUM('PERMANENTE','PUNTUAL') NOT NULL,
    tipo_acred    ENUM('LIBRE','ACREDITADO')   NOT NULL,
    fecha_inicio  DATE        NOT NULL,
    fecha_fin     DATE        NOT NULL,
    activo        BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_derecho_usuario  FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_derecho_vehiculo FOREIGN KEY (vehiculo_id)
        REFERENCES vehiculos(id) ON DELETE CASCADE,
    CONSTRAINT chk_fechas CHECK (fecha_fin >= fecha_inicio)
);

-- ─── ÍNDICES ────────────────────────────────────────────────────────────────
CREATE INDEX idx_vehiculos_usuario   ON vehiculos       (usuario_id);
CREATE INDEX idx_vehiculos_matricula ON vehiculos       (matricula);
CREATE INDEX idx_documentos_usuario  ON documentos      (usuario_id);
CREATE INDEX idx_solicitudes_usuario ON solicitudes     (usuario_id);
CREATE INDEX idx_solicitudes_estado  ON solicitudes     (estado);
CREATE INDEX idx_derechos_usuario    ON derechos_acceso (usuario_id);
CREATE INDEX idx_derechos_vehiculo   ON derechos_acceso (vehiculo_id);
CREATE INDEX idx_derechos_fecha      ON derechos_acceso (fecha_inicio, fecha_fin);

-- ─── DATOS DE PRUEBA ────────────────────────────────────────────────────────
-- Contraseñas en texto plano: 'password123' (se hashearán con BCrypt en el backend)
INSERT INTO usuarios (dni, nombre, apellidos, email, password, rol, tipo) VALUES
('00000001A', 'Admin',     'Sistema APR',     'admin@apr-xativa.es',   '$2a$10$placeholder', 'ADMIN', 'A.1'),
('12345678B', 'Carlos',    'García López',    'carlos@email.com',      '$2a$10$placeholder', 'USER',  'A.1'),
('87654321C', 'María',     'Martínez Ruiz',   'maria@email.com',       '$2a$10$placeholder', 'USER',  'B'),
('11111111D', 'Restaurante','El Mercat SL',   'mercat@email.com',      '$2a$10$placeholder', 'USER',  'I'),
('22222222E', 'Hotel',     'Xàtiva Palace',   'hotel@email.com',       '$2a$10$placeholder', 'USER',  'H.2');

UPDATE usuarios SET num_plazas = 20 WHERE dni = '22222222E';