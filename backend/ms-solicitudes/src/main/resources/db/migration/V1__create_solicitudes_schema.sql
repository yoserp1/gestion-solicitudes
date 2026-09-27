CREATE SEQUENCE solicitud_codigo_seq AS BIGINT START WITH 1 INCREMENT BY 1;

CREATE TABLE categoria (
    id UNIQUEIDENTIFIER NOT NULL,
    codigo VARCHAR(30) NOT NULL,
    nombre NVARCHAR(100) NOT NULL,
    activa BIT NOT NULL CONSTRAINT df_categoria_activa DEFAULT 1,
    CONSTRAINT pk_categoria PRIMARY KEY (id),
    CONSTRAINT uk_categoria_codigo UNIQUE (codigo)
);

CREATE TABLE solicitud (
    id UNIQUEIDENTIFIER NOT NULL,
    codigo VARCHAR(15) NOT NULL,
    asunto NVARCHAR(150) NOT NULL,
    descripcion NVARCHAR(2000) NOT NULL,
    categoria_id UNIQUEIDENTIFIER NOT NULL,
    prioridad VARCHAR(10) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    solicitante_id VARCHAR(100) NOT NULL,
    analista_id VARCHAR(100) NULL,
    creada_en DATETIMEOFFSET(7) NOT NULL,
    actualizada_en DATETIMEOFFSET(7) NOT NULL,
    version BIGINT NOT NULL CONSTRAINT df_solicitud_version DEFAULT 0,
    CONSTRAINT pk_solicitud PRIMARY KEY (id),
    CONSTRAINT uk_solicitud_codigo UNIQUE (codigo),
    CONSTRAINT fk_solicitud_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    CONSTRAINT ck_solicitud_prioridad CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA')),
    CONSTRAINT ck_solicitud_estado CHECK (estado IN ('REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA'))
);

CREATE INDEX ix_solicitud_bandeja ON solicitud (estado, categoria_id, prioridad, creada_en DESC);
CREATE INDEX ix_solicitud_solicitante ON solicitud (solicitante_id, creada_en DESC);

CREATE TABLE observacion (
    id UNIQUEIDENTIFIER NOT NULL,
    solicitud_id UNIQUEIDENTIFIER NOT NULL,
    contenido NVARCHAR(2000) NOT NULL,
    actor_id VARCHAR(100) NOT NULL,
    actor_rol VARCHAR(20) NOT NULL,
    creada_en DATETIMEOFFSET(7) NOT NULL,
    CONSTRAINT pk_observacion PRIMARY KEY (id),
    CONSTRAINT fk_observacion_solicitud FOREIGN KEY (solicitud_id) REFERENCES solicitud(id),
    CONSTRAINT ck_observacion_actor_rol CHECK (actor_rol IN ('SOLICITANTE', 'ANALISTA', 'SUPERVISOR'))
);

CREATE INDEX ix_observacion_solicitud ON observacion (solicitud_id, creada_en);

CREATE TABLE historial_estado (
    id UNIQUEIDENTIFIER NOT NULL,
    solicitud_id UNIQUEIDENTIFIER NOT NULL,
    estado_origen VARCHAR(20) NULL,
    estado_destino VARCHAR(20) NOT NULL,
    actor_id VARCHAR(100) NOT NULL,
    actor_rol VARCHAR(20) NOT NULL,
    motivo NVARCHAR(500) NOT NULL,
    ocurrido_en DATETIMEOFFSET(7) NOT NULL,
    CONSTRAINT pk_historial_estado PRIMARY KEY (id),
    CONSTRAINT fk_historial_solicitud FOREIGN KEY (solicitud_id) REFERENCES solicitud(id),
    CONSTRAINT ck_historial_destino CHECK (estado_destino IN ('REGISTRADA', 'EN_ATENCION', 'RESUELTA', 'CERRADA')),
    CONSTRAINT ck_historial_actor_rol CHECK (actor_rol IN ('SOLICITANTE', 'ANALISTA', 'SUPERVISOR'))
);

CREATE INDEX ix_historial_solicitud ON historial_estado (solicitud_id, ocurrido_en);

CREATE TABLE idempotencia_comando (
    clave UNIQUEIDENTIFIER NOT NULL,
    operacion VARCHAR(80) NOT NULL,
    actor_id VARCHAR(100) NOT NULL,
    request_hash CHAR(64) NOT NULL,
    recurso_id UNIQUEIDENTIFIER NULL,
    creado_en DATETIMEOFFSET(7) NOT NULL,
    CONSTRAINT pk_idempotencia_comando PRIMARY KEY (clave)
);

CREATE TABLE outbox_evento (
    event_id UNIQUEIDENTIFIER NOT NULL,
    aggregate_id UNIQUEIDENTIFIER NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    payload NVARCHAR(MAX) NOT NULL,
    ocurrido_en DATETIMEOFFSET(7) NOT NULL,
    publicado_en DATETIMEOFFSET(7) NULL,
    intentos INT NOT NULL CONSTRAINT df_outbox_intentos DEFAULT 0,
    CONSTRAINT pk_outbox_evento PRIMARY KEY (event_id),
    CONSTRAINT ck_outbox_payload_json CHECK (ISJSON(payload) = 1)
);

CREATE INDEX ix_outbox_pendiente ON outbox_evento (publicado_en, ocurrido_en);

INSERT INTO categoria (id, codigo, nombre, activa) VALUES
    ('10000000-0000-0000-0000-000000000001', 'COORDINACION', N'Coordinación interna', 1),
    ('10000000-0000-0000-0000-000000000002', 'SOPORTE', N'Soporte operativo', 1),
    ('10000000-0000-0000-0000-000000000003', 'OTROS', N'Otras solicitudes', 1);
