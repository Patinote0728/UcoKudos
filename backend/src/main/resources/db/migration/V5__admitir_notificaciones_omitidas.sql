ALTER TABLE cola_notificacion DROP CONSTRAINT ck_cola_estado;

ALTER TABLE cola_notificacion
    ADD CONSTRAINT ck_cola_estado
    CHECK (estado IN ('PENDIENTE', 'PROCESANDO', 'REINTENTAR', 'ENVIADA', 'SIMULADA', 'OMITIDA', 'FALLIDA'));
