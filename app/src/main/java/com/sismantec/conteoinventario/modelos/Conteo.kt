package com.sismantec.conteoinventario.modelos

data class Conteo(
    val id: Int,
    val nombreEmpleado: String,
    val ubicacion: String,
    val idBodega: Int,
    val estado: String,
    val fechaInicio: String,
    val fechaFin: String,
    val fechaEnvio: String,
    val id_ajuste_inventario: Int,
    val tipoConteo:String
    )