package com.sismantec.conteoinventario.controladores

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.widget.Toast
import com.sismantec.conteoinventario.apiservices.APIService
import com.sismantec.conteoinventario.funciones.AlertaDialogo
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.ResponseBodegas
import com.sismantec.conteoinventario.modelos.ResponseInventario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
                    }else{
                        //Toast.makeText(context, "ERROR AL OBTENER LAS BODEGAS", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }finally {
                db.close()
                obtenerInventario(context)
            }
        }
    }

    //FUNCION PARA OBTENER EL INVENTARIO
    fun obtenerInventario(context: Context){
        val db = funciones.getDataBase(context).writableDatabase
        val url = funciones.getServidor(funciones.getPreferences(context).ip, funciones.getPreferences(context).puerto)

        val mensaje = AlertaDialogo(context as Activity)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call: Response<List<ResponseInventario>> = funciones.getRetrofit(url).create(APIService::class.java)
                    .obtenerInventario("inventario/")

                withContext(Dispatchers.Main){
                    if(call.isSuccessful){
                        mensaje.dialogoCargando()
                        val respuesta = call.body()
                        db.beginTransaction()
                        db.execSQL("DELETE FROM inventario")

                        respuesta?.let {
                            delay(3000)
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
                        mensaje.dialogoCancelar()
                        funciones.toastMensaje(context, "INVENTARIO ALMACENADO CORRECTAMENTE", 1)
                        db.endTransaction()
                    }else{
                        funciones.toastMensaje(context, "ERROR AL OBTENER EL INVENTARIO", 0)
                    }
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }finally {
                db.close()
            }
        }
    }

    //FUNCION PARA OBTENER TODAS LAS BODEGAS SI SE ENCUENTRAN
    fun seleccionarBodegasSQLite(context: Context) : List<String>{
        val db = funciones.getDataBase(context).readableDatabase
        val bodegasList = ArrayList<ResponseBodegas>()
        val nombreBodega = arrayListOf<String>()
        nombreBodega.add("-- SELECCIONE UNA BODEGA --")

        try{
            val bodegas = db.rawQuery("SELECT * FROM bodegas", null)
            if(bodegas.count > 0){
                bodegas.moveToFirst()
                do {
                    val data = ResponseBodegas(
                        bodegas.getInt(0),
                        bodegas.getString(1)
                    )
                    bodegasList.add(data)
                }while (bodegas.moveToNext())

                for (item in bodegasList){
                    nombreBodega.add(item.nombre)
                }
            }else{
                //RESPUESTA SI NO SE ENCUENTRAN BODEGAS AL MACENADAS
            }
            bodegas.close()
        }catch (e: Exception){
            throw Exception(e.message)
        }finally {
            db.close()
        }
        return nombreBodega
    }

    //FUNCION PARA VERIFICAR SI HAY INVENTARIO CARGADO
    fun seleccionarInventarioSQLite(context: Context, query: String): ArrayList<ResponseInventario>{
        val db = funciones.getDataBase(context).readableDatabase
        val inventarioList = ArrayList<ResponseInventario>()

        val consulta: String = if (query.isEmpty()){
            "SELECT * FROM inventario LIMIT 40"
        }else{
            "SELECT * FROM inventario where codigo=$query"
        }

        try {
            val inventario = db.rawQuery(consulta, null)
            if(inventario.count > 0){
                inventario.moveToFirst()
                do {
                    val data = ResponseInventario(
                        inventario.getInt(0),
                        inventario.getString(1),
                        inventario.getString(2)
                    )
                    inventarioList.add(data)
                }while (inventario.moveToNext())
                inventario.close()
            }else{
             //SI NO HAY INVENTARIO EN DB
            }
        }catch (e:Exception){
            throw Exception(e.message)
        }finally {
            db.close()
        }
        return inventarioList
    }

}