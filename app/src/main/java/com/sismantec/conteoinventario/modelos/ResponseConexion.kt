package com.sismantec.conteoinventario.modelos

import com.google.gson.annotations.SerializedName

data class ResponseConexion(
    @SerializedName("error") val codigoRespuesta: Int,
    @SerializedName("response") val respuesta: String
)