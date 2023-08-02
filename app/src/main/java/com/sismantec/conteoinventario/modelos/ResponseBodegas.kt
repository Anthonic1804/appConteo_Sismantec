package com.sismantec.conteoinventario.modelos

import com.google.gson.annotations.SerializedName

data class ResponseBodegas(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)