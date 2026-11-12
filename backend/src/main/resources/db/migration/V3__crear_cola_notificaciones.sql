CREATE TABLE cola_notificacion (
    id UUID PRIMARY KEY,
    reconocimiento_kudo_id UUID NOT NULL REFERENCES reconocimiento_kudo(id) ON DELETE CASCADE,
    destinatario_id UUID NOT NULL REFERENCES usuario(id),
    canal VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    intentos INTEGER NOT NULL DEFAULT 0,
    proximo_intento_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    bloqueado_hasta TIMESTAMPTZ,
    ultimo_error VARCHAR(1000),
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completado_en TIMESTAMPTZ,
    CONSTRAINT uq_cola_kudo_canal UNIQUE (reconocimiento_kudo_id, canal),
    CONSTRAINT ck_cola_canal CHECK (canal IN ('CORREO', 'WHATSAPP', 'PUSH')),
    CONSTRAINT ck_cola_estado CHECK (estado IN ('PENDIENTE', 'PROCESANDO', 'REINTENTAR', 'ENVIADA', 'SIMULADA', 'FALLIDA')),
    CONSTRAINT ck_cola_intentos CHECK (intentos >= 0)
);

CREATE INDEX ix_cola_pendiente ON cola_notificacion(estado, proximo_intento_en, creado_en);
CREATE INDEX ix_cola_destinatario ON cola_notificacion(destinatario_id, creado_en DESC);
