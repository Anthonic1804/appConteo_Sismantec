package com.sismantec.conteoinventario.modelos

import com.google.gson.annotations.SerializedName

data class ResponseLogin (
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val empleado: String,
    @SerializedName("generar_Token") val esAdmin: Int
        )

