package com.sismantec.conteoinventario.apiservices

import com.sismantec.conteoinventario.modelos.ConteoJSON
import com.sismantec.conteoinventario.modelos.LoginJSON
import com.sismantec.conteoinventario.modelos.LogoutJSON
import com.sismantec.conteoinventario.modelos.ResponseBodegas
import com.sismantec.conteoinventario.modelos.ResponseConexion
import com.sismantec.conteoinventario.modelos.ResponseInventario
import com.sismantec.conteoinventario.modelos.ResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface APIService {
    //CONEXION CON EL SERVIDOR
    @GET
    suspend fun obtenerConexion(@Url url:String): Response<ResponseConexion>

    //FUNCIONES DEL LOGIN
    @POST("Login/login/")
    suspend fun loginApp(@Body loginJSON: LoginJSON): Response<ResponseLogin>
    @PUT("Login/logout/")
    suspend fun LogoutApp(@Body logoutJSON: LogoutJSON): Response<ResponseConexion>

    //FUNCIONES DEL INVENTARIO RETROFIT
    @GET
    suspend fun obtenerBodegas(@Url url:String) : Response<List<ResponseBodegas>>
    @GET
    suspend fun obtenerInventario(@Url url:String): Response<List<ResponseInventario>>

    //FUNCION PARA ENVIAR EL CONTEO AL SERVIDOR
    @POST("inventario/registrar/")
    suspend fun enviarConteoServer(@Body conteoJSON: ConteoJSON): Response<ResponseConexion>
}