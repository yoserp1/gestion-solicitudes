DECLARE @ahora DATETIMEOFFSET(7) = SYSDATETIMEOFFSET();
DECLARE @registrada UNIQUEIDENTIFIER = NEWID();
DECLARE @enAtencion UNIQUEIDENTIFIER = NEWID();
DECLARE @resuelta UNIQUEIDENTIFIER = NEWID();
DECLARE @cerrada UNIQUEIDENTIFIER = NEWID();

INSERT INTO solicitud_proyeccion (
    solicitud_id, categoria_id, categoria_codigo, categoria_nombre,
    estado, registrada_en, actualizada_en, version
) VALUES
    (@registrada, '10000000-0000-0000-0000-000000000001', 'COORDINACION', N'Coordinación interna',
        'REGISTRADA', DATEADD(DAY, -6, @ahora), DATEADD(DAY, -6, @ahora), 0),
    (@enAtencion, '10000000-0000-0000-0000-000000000002', 'SOPORTE', N'Soporte operativo',
        'EN_ATENCION', DATEADD(DAY, -5, @ahora), DATEADD(DAY, -4, @ahora), 1),
    (@resuelta, '10000000-0000-0000-0000-000000000002', 'SOPORTE', N'Soporte operativo',
        'RESUELTA', DATEADD(DAY, -4, @ahora), DATEADD(DAY, -2, @ahora), 2),
    (@cerrada, '10000000-0000-0000-0000-000000000003', 'OTROS', N'Otras solicitudes',
        'CERRADA', DATEADD(DAY, -3, @ahora), @ahora, 3);

DECLARE @eventos TABLE (
    event_id UNIQUEIDENTIFIER NOT NULL,
    solicitud_id UNIQUEIDENTIFIER NOT NULL,
    categoria_id UNIQUEIDENTIFIER NOT NULL,
    estado_destino VARCHAR(20) NOT NULL,
    ocurrido_en DATETIMEOFFSET(7) NOT NULL
);

INSERT INTO @eventos VALUES
    (NEWID(), @registrada, '10000000-0000-0000-0000-000000000001', 'REGISTRADA', DATEADD(DAY, -6, @ahora)),
    (NEWID(), @enAtencion, '10000000-0000-0000-0000-000000000002', 'REGISTRADA', DATEADD(DAY, -5, @ahora)),
    (NEWID(), @enAtencion, '10000000-0000-0000-0000-000000000002', 'EN_ATENCION', DATEADD(DAY, -4, @ahora)),
    (NEWID(), @resuelta, '10000000-0000-0000-0000-000000000002', 'REGISTRADA', DATEADD(DAY, -4, @ahora)),
    (NEWID(), @resuelta, '10000000-0000-0000-0000-000000000002', 'EN_ATENCION', DATEADD(DAY, -3, @ahora)),
    (NEWID(), @resuelta, '10000000-0000-0000-0000-000000000002', 'RESUELTA', DATEADD(DAY, -2, @ahora)),
    (NEWID(), @cerrada, '10000000-0000-0000-0000-000000000003', 'REGISTRADA', DATEADD(DAY, -3, @ahora)),
    (NEWID(), @cerrada, '10000000-0000-0000-0000-000000000003', 'EN_ATENCION', DATEADD(DAY, -2, @ahora)),
    (NEWID(), @cerrada, '10000000-0000-0000-0000-000000000003', 'RESUELTA', DATEADD(DAY, -1, @ahora)),
    (NEWID(), @cerrada, '10000000-0000-0000-0000-000000000003', 'CERRADA', @ahora);

INSERT INTO transicion_proyeccion (event_id, solicitud_id, categoria_id, estado_destino, ocurrido_en)
SELECT event_id, solicitud_id, categoria_id, estado_destino, ocurrido_en
FROM @eventos;

INSERT INTO evento_procesado (event_id, event_type, procesado_en)
SELECT event_id, 'EventoSinteticoPrueba', @ahora
FROM @eventos;
