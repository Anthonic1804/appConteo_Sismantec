package com.sismantec.conteoinventario.controladores

import android.content.Context
import android.widget.Toast
import com.sismantec.conteoinventario.apiservices.APIService
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.ResponseBodegas
import com.sismantec.conteoinventario.modelos.ResponseInventario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class InventarioController {

    private val funciones = Funciones()

    //FUNCION PARA OBTENER BODEGAS
    fun obtenerBodegas(context: Context){
        val url = funciones.getServidor(funciones.getPreferences(context).ip, funciones.getPreferences(context).puerto)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<List<ResponseBodegas>> = funciones.getRetrofit(url).create(APIService::class.java)
                    .obtenerBodegas("Bodega")
                withContext(Dispatchers.Main){
                    if(call.isSuccessful){
                        val respuesta = call.body()
                        respuesta?.let {
                            for(item in respuesta){
                                val id: Int = item.id
                                val nombre: String = item.nombre

                                println("BODEGA ID: $id, NOMBRE: $nombre")

                                //AQUI SE IMPLEMENTARA LA INSERCION EN LA BD CON ROOM
                            }
                        }
                        println("-----------------------------------------\n\n")

                        obtenerInventario(context)

                    }else{
                        Toast.makeText(context, "ERROR AL OBTENER LAS BODEGAS", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    //FUNCION PARA OBTENER EL INVENTARIO
    fun obtenerInventario(context: Context){
        val url = funciones.getServidor(funciones.getPreferences(context).ip, funciones.getPreferences(context).puerto)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<List<ResponseInventario>> = funciones.getRetrofit(url).create(APIService::class.java)
                    .obtenerInventario("inventario/")

                withContext(Dispatchers.Main){
                    if(call.isSuccessful){
                        val respuesta = call.body()

                        respuesta?.let {
                            for (item in respuesta){
                                val id: Int = item.id
                                val codigo: String = item.codigo
                                val descripcion: String = item.descripcion

                                println("PRODUCTO ID: $id, CODIGO: $codigo, DESCRIPCION: $descripcion")

                                //AQUI SE REALIZARÁ LA INSERCIÓN DE LA DATA CON ROOM
                            }
                        }

                        Toast.makeText(context, "LECTURA CORRECTA", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "ERROR AL OBTENER EL INVENTARIO", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}