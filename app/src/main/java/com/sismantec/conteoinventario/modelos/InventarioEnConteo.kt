package com.sismantec.conteoinventario.modelos

data class InventarioEnConteo(
    val idConteo: Int,
    val idInventario: Int,
    val codigoInventario: String,
    val descripcion: String,
    val unidades: Float,
    val fracciones: Float

)