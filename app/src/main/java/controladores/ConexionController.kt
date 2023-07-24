package controladores

import android.content.SharedPreferences

class ConexionController {

    private lateinit var preferencias: SharedPreferences
    private val instancia = "CONFIG_SERVIDOR"


    //FUNCION PARA VALIDAR CAMPOS DE CONEXION
    fun validarCampos(ip: String, puerto: String): Boolean{
        var validos = true
        if(ip.isEmpty() or puerto.isEmpty()){
            validos = false
        }
        return validos
    }

    //FUNCION PARA OBTENER EL SERVIDOR
    private fun getServidor(ip: String, puerto: String): String {
        return "http://${ip}:${puerto}"
    }

}