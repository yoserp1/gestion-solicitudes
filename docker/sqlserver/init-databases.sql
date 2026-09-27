IF DB_ID(N'solicitudes_db') IS NULL
BEGIN
    CREATE DATABASE solicitudes_db;
END;
GO

IF DB_ID(N'indicadores_db') IS NULL
BEGIN
    CREATE DATABASE indicadores_db;
END;
GO
