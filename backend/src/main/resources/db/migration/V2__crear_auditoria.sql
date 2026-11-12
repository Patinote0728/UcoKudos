CREATE TABLE evento_auditoria (
    id UUID PRIMARY KEY,
    tipo_evento VARCHAR(80) NOT NULL,
    actor_usuario_id UUID NOT NULL REFERENCES usuario(id),
    recurso_tipo VARCHAR(60) NOT NULL,
    recurso_id UUID NOT NULL,
    ocurrido_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ix_auditoria_recurso_fecha ON evento_auditoria(recurso_tipo, recurso_id, ocurrido_en DESC);
CREATE INDEX ix_auditoria_actor_fecha ON evento_auditoria(actor_usuario_id, ocurrido_en DESC);
