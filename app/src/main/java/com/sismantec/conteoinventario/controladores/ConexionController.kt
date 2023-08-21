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
                        funciones.toastMensaje(context, "CONEXION EXITOSA SERVIDOR", 1)
                        callback(true)
                    } else {
                        funciones.toastMensaje(context, "ERROR EN LA RESPUESTA DEL SERVIDOR", 0)
                        callback(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "ERROR DE CONEXION CON EL SERVIDOR", Toast.LENGTH_SHORT).show()
                }
                callback(false)
            }
        }
    }

    //FUNCION PARA ELIMINAR LOS DATOS DE LA APP
    fun eliminarDataApp(context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val db = funciones.getDataBase(context).writableDatabase
            try {
                db.beginTransaction()
                db.execSQL("DELETE FROM bodegas")
                db.execSQL("DELETE FROM inventario")
                db.execSQL("DELETE FROM conteoInventario")
                db.execSQL("DELETE FROM detalleConteo")

                db.setTransactionSuccessful()
                db.endTransaction()

                val pref = context.getSharedPreferences("serverData", Context.MODE_PRIVATE)
                val editor = pref.edit()
                editor.remove("empleado")
                editor.remove("idEmpleado")
                editor.remove("esAdmin")
                editor.remove("ip")
                editor.remove("puerto")
                editor.apply()

            }catch (e: Exception){
                throw Exception("ERROR AL ELIMINAR LA INFORMACION DE LA APP " + e.message)
            }finally {
                db.close()
            }
        }
    }

}