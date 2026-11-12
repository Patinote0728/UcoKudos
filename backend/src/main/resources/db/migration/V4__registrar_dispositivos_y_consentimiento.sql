ALTER TABLE usuario
    ADD COLUMN whatsapp_autorizado BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE dispositivo_push (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    firebase_installation_id VARCHAR(255) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ix_dispositivo_push_usuario_activo ON dispositivo_push(usuario_id, activo);
