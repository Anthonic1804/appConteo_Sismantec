package com.sismantec.conteoinventario.controladores

import android.content.ContentValues
import android.content.Context
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.Conteo
import com.sismantec.conteoinventario.modelos.ResponseBodegas

class ConteoController {

    private var funciones = Funciones()
    fun obtenerConteoSQLite(context: Context): ArrayList<Conteo>{
        val db = funciones.getDataBase(context).readableDatabase
        val conteosList = ArrayList<Conteo>()

        try{
            val conteos = db.rawQuery("SELECT * FROM conteoInventario", null)
            if(conteos.count > 0){
                conteos.moveToFirst()
                do{
                    val data = Conteo(
                        conteos.getInt(0),
                        conteos.getString(1),
                        conteos.getString(2),
                        conteos.getInt(3),
                        conteos.getString(4),
                        conteos.getString(5),
                        conteos.getString(6),
                        conteos.getString(7),
                        conteos.getInt(8),
                        conteos.getString(9)
                    )
                    conteosList.add(data)
                }while (conteos.moveToNext())
                conteos.close()
            }else{
                //MOSTRAR ERROR
            }
        }catch (e:Exception){
            throw Exception(e.message)
        }finally {
            db.close()
        }
        return conteosList
    }

    fun registrarNuevoConteo(context: Context, ubicacion:String, tipoConteo:String): Long {
        val db = funciones.getDataBase(context).writableDatabase
        val fechaInicio = funciones.getDateTime()
        val prefs = funciones.getPreferences(context)
        val idBodega = obtenerIdBodega(context, ubicacion)
        var idConteo : Long = 0

        try {
            db.beginTransaction()
            val data = ContentValues()
            data.put("Nombre_empleado", prefs.empleado)
            data.put("Ubicacion", ubicacion)
            data.put("Id_Bodega", idBodega)
            data.put("Estado", "HABILITADO")
            data.put("Fecha_inicio", fechaInicio)
            data.put("Tipo_conteo", tipoConteo)

            idConteo = db.insert("conteoInventario", null, data)

            db.setTransactionSuccessful()
        }catch (e:Exception){
            throw Exception(e.message)
        }finally {
            db.endTransaction()
            db.close()
        }
        return idConteo
    }

    fun obtenerIdBodega(context: Context, nombre:String): Int{
        val db = funciones.getDataBase(context).readableDatabase
        val bodegasList = ArrayList<ResponseBodegas>()
        var idBodegas = 0

        try{
            val bodegas = db.rawQuery("SELECT * FROM bodegas WHERE Nombre='${nombre}'", null)
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
                    idBodegas = item.id
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
        return idBodegas
    }
}