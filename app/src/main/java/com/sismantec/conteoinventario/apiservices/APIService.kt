package com.sismantec.conteoinventario.apiservices

import com.sismantec.conteoinventario.modelos.LoginJSON
import com.sismantec.conteoinventario.modelos.ResponseConexion
import com.sismantec.conteoinventario.modelos.ResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface APIService {
    @GET
    suspend fun obtenerConexion(@Url url:String): Response<ResponseConexion>

    @POST("Login/login/")
    suspend fun loginApp(@Body loginJSON: LoginJSON): Response<ResponseLogin>
}