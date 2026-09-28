DECLARE @ahora DATETIMEOFFSET(7) = SYSDATETIMEOFFSET();

DECLARE @registrada UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000001';
DECLARE @enAtencion UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000002';
DECLARE @resuelta UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000003';
DECLARE @cerrada UNIQUEIDENTIFIER = '20000000-0000-0000-0000-000000000004';

INSERT INTO solicitud (
    id, codigo, asunto, descripcion, categoria_id, prioridad, estado,
    solicitante_id, analista_id, creada_en, actualizada_en, version
) VALUES
    (@registrada, 'DEMO-2026-0001', N'Coordinación de acceso',
        N'Solicitud de prueba disponible para ser tomada por un analista.',
        '10000000-0000-0000-0000-000000000001', 'MEDIA', 'REGISTRADA',
        'solicitante-demo', NULL, DATEADD(DAY, -6, @ahora), DATEADD(DAY, -6, @ahora), 0),
    (@enAtencion, 'DEMO-2026-0002', N'Incidente de soporte',
        N'Solicitud de prueba actualmente atendida por el analista demo.',
        '10000000-0000-0000-0000-000000000002', 'ALTA', 'EN_ATENCION',
        'solicitante-demo', 'analista-demo', DATEADD(DAY, -5, @ahora), DATEADD(DAY, -4, @ahora), 1),
    (@resuelta, 'DEMO-2026-0003', N'Restablecimiento completado',
        N'Solicitud de prueba resuelta y disponible para revisión del supervisor.',
        '10000000-0000-0000-0000-000000000002', 'ALTA', 'RESUELTA',
        'solicitante-demo', 'analista-demo', DATEADD(DAY, -4, @ahora), DATEADD(DAY, -2, @ahora), 2),
    (@cerrada, 'DEMO-2026-0004', N'Consulta administrativa cerrada',
        N'Solicitud de prueba con el ciclo de atención completo.',
        '10000000-0000-0000-0000-000000000003', 'BAJA', 'CERRADA',
        'solicitante-demo', 'analista-demo', DATEADD(DAY, -3, @ahora), @ahora, 3);

INSERT INTO historial_estado (
    id, solicitud_id, estado_origen, estado_destino, actor_id, actor_rol, motivo, ocurrido_en
) VALUES
    (NEWID(), @registrada, NULL, 'REGISTRADA', 'solicitante-demo', 'SOLICITANTE',
        N'Solicitud de demostración registrada', DATEADD(DAY, -6, @ahora)),
    (NEWID(), @enAtencion, NULL, 'REGISTRADA', 'solicitante-demo', 'SOLICITANTE',
        N'Solicitud de demostración registrada', DATEADD(DAY, -5, @ahora)),
    (NEWID(), @enAtencion, 'REGISTRADA', 'EN_ATENCION', 'analista-demo', 'ANALISTA',
        N'Inicio de atención de demostración', DATEADD(DAY, -4, @ahora)),
    (NEWID(), @resuelta, NULL, 'REGISTRADA', 'solicitante-demo', 'SOLICITANTE',
        N'Solicitud de demostración registrada', DATEADD(DAY, -4, @ahora)),
    (NEWID(), @resuelta, 'REGISTRADA', 'EN_ATENCION', 'analista-demo', 'ANALISTA',
        N'Inicio de atención de demostración', DATEADD(DAY, -3, @ahora)),
    (NEWID(), @resuelta, 'EN_ATENCION', 'RESUELTA', 'analista-demo', 'ANALISTA',
        N'Resolución satisfactoria de demostración', DATEADD(DAY, -2, @ahora)),
    (NEWID(), @cerrada, NULL, 'REGISTRADA', 'solicitante-demo', 'SOLICITANTE',
        N'Solicitud de demostración registrada', DATEADD(DAY, -3, @ahora)),
    (NEWID(), @cerrada, 'REGISTRADA', 'EN_ATENCION', 'analista-demo', 'ANALISTA',
        N'Inicio de atención de demostración', DATEADD(DAY, -2, @ahora)),
    (NEWID(), @cerrada, 'EN_ATENCION', 'RESUELTA', 'analista-demo', 'ANALISTA',
        N'Resolución satisfactoria de demostración', DATEADD(DAY, -1, @ahora)),
    (NEWID(), @cerrada, 'RESUELTA', 'CERRADA', 'supervisor-demo', 'SUPERVISOR',
        N'Cierre supervisor de demostración', @ahora);

INSERT INTO observacion (id, solicitud_id, contenido, actor_id, actor_rol, creada_en) VALUES
    (NEWID(), @enAtencion, N'Observación de seguimiento para la demostración.',
        'analista-demo', 'ANALISTA', DATEADD(DAY, -4, @ahora)),
    (NEWID(), @resuelta, N'Validación técnica completada durante la demostración.',
        'analista-demo', 'ANALISTA', DATEADD(DAY, -2, @ahora));