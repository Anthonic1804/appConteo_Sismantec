package com.sismantec.conteoinventario.controladores

import android.content.Context
import android.widget.Toast
import com.sismantec.conteoinventario.R
import com.sismantec.conteoinventario.apiservices.APIService
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.LoginJSON
import com.sismantec.conteoinventario.modelos.ResponseLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginController {
    private var funciones = Funciones()

    //FUNCION PARA VALIDAR CAMPOS DEL LOGIN
    fun validarCampos(usuario: String, clave: String): Boolean{
        var validos = true
        if(usuario.isEmpty() or clave.isEmpty()){
            validos = false
        }
        return validos
    }

    fun iniciarSesion(context: Context, usuario: String, clave: String, callback: (Boolean) -> Unit) {
        val prefs = funciones.getPreferences(context)
        val url = funciones.getServidor(prefs.ip, prefs.puerto)

        val credenciales = LoginJSON(
            usuario.uppercase(),
            clave
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<ResponseLogin> = funciones.getRetrofit(url).create(APIService::class.java)
                    .loginApp(credenciales)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val respuesta = response.body()

                        val prefs2 = context.getSharedPreferences("serverData", Context.MODE_PRIVATE)
                        val editor = prefs2.edit()
                        editor.putString("empleado", respuesta?.empleado.toString())
                        editor.putInt("idEmpleado", respuesta?.id.toString().toInt())
                        editor.apply()

                        funciones.toastMensaje(context, "BIENVENIDO ${respuesta?.empleado.toString()}", 1)
                        callback(true)
                    } else {
                        funciones.toastMensaje(context, "DATOS INCORRECTOS", 0)
                        callback(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "ERROR: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                callback(true)
            }
        }
    }


}