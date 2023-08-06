package com.sismantec.conteoinventario.database

class TablasConteo {

    //TABLA INVENTARIO
    fun inventario(): String {
        return "CREATE TABLE inventario(" +
                "Id INTEGER PRIMARY KEY NOT NULL," +
                "Codigo VARCHAR(25) NOT NULL," +
                "Descripcion VARCHAR(100) NOT NULL)"
    }

    //TABLA BODEGAS
    fun bodega(): String {
        return "CREATE TABLE bodegas(" +
                "Id INTEGER PRIMARY KEY NOT NULL," +
                "Nombre VARCHAR(50) NOT NULL)"
    }

    // CREAR TABLA CONTEO INVENTARIO
    fun conteoInventario(): String {
        return "CREATE TABLE conteoInventario (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Nombre_empleado VARCHAR(50)  NOT NULL," +
                "Ubicacion VARCHAR(100) NOT NULL," +
                "Id_Bodega INTEGER NOT NULL," +
                "Estado VARCHAR(15) NOT NULL," +
                "Fecha_inicio TIMESTAMP DEFAULT NULL NULL," +
                "Fecha_fin TIMESTAMP NULL DEFAULT NULL," +
                "Fecha_envio TIMESTAMP NULL DEFAULT NULL," +
                "Id_ajuste_inventario INTEGER DEFAULT NULL NULL," +
                "Tipo_conteo VARCHAR(1) NOT NULL)"
    }

    // CREAR TABLA DETALLE CONTEO
    fun detalleConteo(): String {
        return "CREATE TABLE detalleConteo (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Id_conteo_inventario INTEGER NOT NULL," +
                "Id_inventario INTEGER NOT NULL," +
                "Unidades NUMERIC(18, 6) NULL," +
                "Fracciones NUMERIC(18, 6) NULL)"
    }
}