package com.sismantec.conteoinventario.modelos

data class ConteoJSON (
    val fechaInicio: String,
    val fechaFin: String,
    val fechaEnvio: String,
    val ubicacion: String,
    val idBodega: Int,
    val idAjusteInventario: Int,
    val idEmpleado: Int,
    val empleado: String,
    val detalle: ArrayList<DetalleConteoJSON>
        )