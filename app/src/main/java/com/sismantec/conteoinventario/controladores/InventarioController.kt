package com.sismantec.conteoinventario.controladores

import android.content.ContentValues
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
        val db = funciones.getDataBase(context).writableDatabase
        val url = funciones.getServidor(funciones.getPreferences(context).ip, funciones.getPreferences(context).puerto)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<List<ResponseBodegas>> = funciones.getRetrofit(url).create(APIService::class.java)
                    .obtenerBodegas("Bodega")
                withContext(Dispatchers.Main){
                    if(call.isSuccessful){
                        val respuesta = call.body()
                        db.beginTransaction()
                        db.execSQL("DELETE FROM bodegas")

                        respuesta?.let {
                            for(item in respuesta){
                                //AQUI SE IMPLEMENTARA LA INSERCION EN LA BD
                                val data = ContentValues()
                                data.put("Id", item.id)
                                data.put("Nombre", item.nombre)
                                db.insert("bodegas", null, data)
                            }
                            db.setTransactionSuccessful()
                        }
                        db.endTransaction()
                        db.close()
                    }else{
                        Toast.makeText(context, "ERROR AL OBTENER LAS BODEGAS", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }finally {
                obtenerInventario(context)
            }
        }
    }


    //FUNCION PARA OBTENER EL INVENTARIO
    fun obtenerInventario(context: Context){
        val db = funciones.getDataBase(context).writableDatabase
        val url = funciones.getServidor(funciones.getPreferences(context).ip, funciones.getPreferences(context).puerto)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<List<ResponseInventario>> = funciones.getRetrofit(url).create(APIService::class.java)
                    .obtenerInventario("inventario/")

                withContext(Dispatchers.Main){
                    if(call.isSuccessful){
                        val respuesta = call.body()
                        db.beginTransaction()
                        db.execSQL("DELETE FROM inventario")

                        respuesta?.let {
                            for (item in respuesta){
                                //AQUI SE REALIZARÁ LA INSERCIÓN DE LA DATABASE
                                val data = ContentValues()
                                data.put("Id", item.id)
                                data.put("Codigo", item.codigo)
                                data.put("Descripcion", item.descripcion)
                                db.insert("inventario", null, data)
                            }
                            db.setTransactionSuccessful()
                        }
                        Toast.makeText(context, "INVENTARIO ALMACENADO CORRECTAMENTE", Toast.LENGTH_SHORT).show()
                        db.endTransaction()
                        db.close()
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