package com.sismantec.conteoinventario.modelos

import com.google.gson.annotations.SerializedName

data class ResponseInventario(
    @SerializedName("id") val id: Int,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("descripcion") val descripcion: String
)