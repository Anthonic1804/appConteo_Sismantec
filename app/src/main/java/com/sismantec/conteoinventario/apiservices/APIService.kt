package com.sismantec.conteoinventario.apiservices

import com.sismantec.conteoinventario.modelos.ResponseConexion
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun obtenerConexion(@Url url:String): Response<ResponseConexion>
}