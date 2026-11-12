CREATE TABLE solicitud_registro (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL UNIQUE REFERENCES usuario(id) ON DELETE CASCADE,
    facultad_id UUID REFERENCES facultad(id),
    programa_id UUID REFERENCES programa_academico(id),
    rol_aprobador VARCHAR(32) NOT NULL,
    estado VARCHAR(16) NOT NULL DEFAULT 'PENDIENTE',
    aprobador_usuario_id UUID REFERENCES usuario(id),
    decidido_en TIMESTAMPTZ,
    comentario VARCHAR(1000),
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_solicitud_registro_estado CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')),
    CONSTRAINT ck_solicitud_registro_rol_aprobador CHECK (rol_aprobador IN (
        'ADMINISTRADOR_SISTEMA', 'RECTOR', 'COORDINADOR_ACADEMICO',
        'COORDINADOR_FACULTAD', 'COORDINADOR_PROGRAMA'
    ))
);

CREATE INDEX ix_solicitud_registro_aprobacion ON solicitud_registro(rol_aprobador, estado, creado_en);

CREATE TABLE codigo_verificacion (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    canal VARCHAR(16) NOT NULL,
    hash_codigo VARCHAR(100) NOT NULL,
    expira_en TIMESTAMPTZ NOT NULL,
    intentos INTEGER NOT NULL DEFAULT 0,
    usado_en TIMESTAMPTZ,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_codigo_verificacion_usuario_canal UNIQUE (usuario_id, canal),
    CONSTRAINT ck_codigo_verificacion_canal CHECK (canal IN ('CORREO', 'CELULAR')),
    CONSTRAINT ck_codigo_verificacion_intentos CHECK (intentos >= 0)
);

CREATE INDEX ix_codigo_verificacion_expiracion ON codigo_verificacion(expira_en);
