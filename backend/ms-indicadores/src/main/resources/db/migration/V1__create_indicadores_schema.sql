CREATE TABLE evento_procesado (
    event_id UNIQUEIDENTIFIER NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    procesado_en DATETIMEOFFSET(7) NOT NULL,
    CONSTRAINT pk_evento_procesado PRIMARY KEY (event_id)
);

CREATE TABLE solicitud_proyeccion (
    solicitud_id UNIQUEIDENTIFIER NOT NULL,
    categoria_id UNIQUEIDENTIFIER NOT NULL,
    categoria_codigo VARCHAR(30) NOT NULL,
    categoria_nombre NVARCHAR(100) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    registrada_en DATETIMEOFFSET(7) NOT NULL,
    actualizada_en DATETIMEOFFSET(7) NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT pk_solicitud_proyeccion PRIMARY KEY (solicitud_id),
    CONSTRAINT ck_proyeccion_estado CHECK (estado IN ('REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA'))
);

CREATE INDEX ix_proyeccion_periodo ON solicitud_proyeccion (registrada_en, categoria_id, estado);

CREATE TABLE transicion_proyeccion (
    event_id UNIQUEIDENTIFIER NOT NULL,
    solicitud_id UNIQUEIDENTIFIER NOT NULL,
    categoria_id UNIQUEIDENTIFIER NOT NULL,
    estado_destino VARCHAR(20) NOT NULL,
    ocurrido_en DATETIMEOFFSET(7) NOT NULL,
    CONSTRAINT pk_transicion_proyeccion PRIMARY KEY (event_id),
    CONSTRAINT fk_transicion_solicitud FOREIGN KEY (solicitud_id) REFERENCES solicitud_proyeccion(solicitud_id),
    CONSTRAINT ck_transicion_estado CHECK (estado_destino IN ('REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA'))
);

CREATE INDEX ix_transicion_tendencia ON transicion_proyeccion (ocurrido_en, categoria_id, estado_destino);
