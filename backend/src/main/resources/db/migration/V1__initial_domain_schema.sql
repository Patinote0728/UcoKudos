CREATE TABLE facultad (
    id UUID PRIMARY KEY,
    nombre VARCHAR(160) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE programa_academico (
    id UUID PRIMARY KEY,
    facultad_id UUID NOT NULL REFERENCES facultad(id),
    nombre VARCHAR(160) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_programa_facultad_nombre UNIQUE (facultad_id, nombre)
);

CREATE TABLE usuario (
    id UUID PRIMARY KEY,
    correo_institucional VARCHAR(254) NOT NULL UNIQUE,
    numero_celular VARCHAR(32) NOT NULL UNIQUE,
    nombre_completo VARCHAR(180) NOT NULL,
    hash_contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(32) NOT NULL,
    estado_aprobacion VARCHAR(16) NOT NULL DEFAULT 'PENDIENTE',
    correo_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    celular_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT FALSE,
    facultad_id UUID REFERENCES facultad(id),
    programa_id UUID REFERENCES programa_academico(id),
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_usuario_rol CHECK (rol IN (
        'ADMINISTRADOR_SISTEMA', 'RECTOR', 'COORDINADOR_ACADEMICO', 'COORDINADOR_FACULTAD',
        'COORDINADOR_PROGRAMA', 'DOCENTE', 'ESTUDIANTE'
    )),
    CONSTRAINT ck_usuario_estado_aprobacion CHECK (estado_aprobacion IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')),
    CONSTRAINT ck_usuario_activo_requiere_aprobacion_y_verificacion
        CHECK (activo = (estado_aprobacion = 'APROBADA' AND correo_verificado AND celular_verificado)),
    CONSTRAINT ck_usuario_alcance_rol CHECK (
        (rol = 'COORDINADOR_FACULTAD' AND facultad_id IS NOT NULL AND programa_id IS NULL)
        OR (rol = 'COORDINADOR_PROGRAMA' AND programa_id IS NOT NULL AND facultad_id IS NULL)
        OR (rol NOT IN ('COORDINADOR_FACULTAD', 'COORDINADOR_PROGRAMA')
            AND facultad_id IS NULL AND programa_id IS NULL)
    )
);

CREATE UNIQUE INDEX uq_un_rector_activo ON usuario(rol)
    WHERE rol = 'RECTOR' AND activo;
CREATE UNIQUE INDEX uq_un_coordinador_academico_activo ON usuario(rol)
    WHERE rol = 'COORDINADOR_ACADEMICO' AND activo;
CREATE UNIQUE INDEX uq_un_coordinador_facultad_activo ON usuario(facultad_id)
    WHERE rol = 'COORDINADOR_FACULTAD' AND activo;
CREATE UNIQUE INDEX uq_un_coordinador_programa_activo ON usuario(programa_id)
    WHERE rol = 'COORDINADOR_PROGRAMA' AND activo;

CREATE TABLE vinculacion_usuario_programa (
    usuario_id UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    programa_academico_id UUID NOT NULL REFERENCES programa_academico(id),
    tipo_vinculacion VARCHAR(16) NOT NULL,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, programa_academico_id, tipo_vinculacion),
    CONSTRAINT ck_tipo_vinculacion CHECK (tipo_vinculacion IN ('ESTUDIANTE', 'DOCENTE'))
);

CREATE INDEX ix_vinculacion_programa_tipo ON vinculacion_usuario_programa(programa_academico_id, tipo_vinculacion);

CREATE TABLE categoria_kudo (
    id UUID PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reconocimiento_kudo (
    id UUID PRIMARY KEY,
    autor_id UUID NOT NULL REFERENCES usuario(id),
    destinatario_id UUID NOT NULL REFERENCES usuario(id),
    categoria_kudo_id UUID NOT NULL REFERENCES categoria_kudo(id),
    mensaje TEXT NOT NULL,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_kudo_mensaje_no_vacio CHECK (LENGTH(TRIM(mensaje)) > 0)
);

CREATE INDEX ix_kudo_destinatario_creado ON reconocimiento_kudo(destinatario_id, creado_en DESC);
CREATE INDEX ix_kudo_autor_creado ON reconocimiento_kudo(autor_id, creado_en DESC);

CREATE TABLE adjunto_kudo (
    id UUID PRIMARY KEY,
    reconocimiento_kudo_id UUID NOT NULL REFERENCES reconocimiento_kudo(id) ON DELETE CASCADE,
    clave_almacenamiento VARCHAR(512) NOT NULL UNIQUE,
    nombre_archivo_original VARCHAR(255) NOT NULL,
    tipo_contenido VARCHAR(150) NOT NULL,
    tamano_bytes BIGINT NOT NULL,
    suma_verificacion_sha256 CHAR(64) NOT NULL,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_adjunto_tamano_positivo CHECK (tamano_bytes > 0)
);

INSERT INTO categoria_kudo (id, nombre) VALUES
    ('10000000-0000-0000-0000-000000000001', 'Rendimiento Académico'),
    ('10000000-0000-0000-0000-000000000002', 'Mejor Investigador'),
    ('10000000-0000-0000-0000-000000000003', 'Empatía'),
    ('10000000-0000-0000-0000-000000000004', 'Comunicación Asertiva'),
    ('10000000-0000-0000-0000-000000000005', 'Orientación al Logro'),
    ('10000000-0000-0000-0000-000000000006', 'Trabajo en Equipo'),
    ('10000000-0000-0000-0000-000000000007', 'Trabajo Bajo Presión'),
    ('10000000-0000-0000-0000-000000000008', 'Visión Estratégica'),
    ('10000000-0000-0000-0000-000000000009', 'Innovación'),
    ('10000000-0000-0000-0000-000000000010', 'Liderazgo'),
    ('10000000-0000-0000-0000-000000000011', 'Transformación Digital'),
    ('10000000-0000-0000-0000-000000000012', 'Mentor Inspirador'),
    ('10000000-0000-0000-0000-000000000013', 'Espíritu UCO'),
    ('10000000-0000-0000-0000-000000000014', 'Servicio a la Comunidad'),
    ('10000000-0000-0000-0000-000000000015', 'Compromiso Institucional');
