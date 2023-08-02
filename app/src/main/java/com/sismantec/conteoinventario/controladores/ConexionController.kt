package com.sismantec.conteoinventario.controladores

import android.content.Context
import android.widget.Toast
import com.sismantec.conteoinventario.apiservices.APIService
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.ResponseConexion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ConexionController {

    private var funciones = Funciones()

    //FUNCION PARA VALIDAR CAMPOS DE CONEXION
    fun validarCampos(ip: String, puerto: String): Boolean{
        var validos = true
        if(ip.isEmpty() or puerto.isEmpty()){
            validos = false
        }
        return validos
    }

    //FUNCION PARA CONECTAR AL SERVIDOR
    fun conectarServidor(ip: String, puerto: String, context: Context, callback: (Boolean) -> Unit) {
        val url = funciones.getServidor(ip, puerto)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<ResponseConexion> = funciones.getRetrofit(url).create(APIService::class.java)
                    .obtenerConexion("Conexion/conexion")

                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        Toast.makeText(context, "CONEXION EXITOSA SERVIDOR: $url", Toast.LENGTH_SHORT).show()
                        callback(true)
                    } else {
                        Toast.makeText(context, "ERROR EN LA RESPUESTA DEL SERVIDOR", Toast.LENGTH_SHORT).show()
                        callback(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "ERROR: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                callback(false)
            }
        }
    }


}