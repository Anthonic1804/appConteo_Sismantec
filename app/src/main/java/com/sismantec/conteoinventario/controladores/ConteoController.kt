package com.sismantec.conteoinventario.controladores

import android.content.ContentValues
import android.content.Context
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.Conteo
import com.sismantec.conteoinventario.modelos.ResponseBodegas

class ConteoController {

    private var funciones = Funciones()
    fun obtenerConteoSQLite(context: Context): List<String>{
        val db = funciones.getDataBase(context).readableDatabase
        val conteosList = ArrayList<Conteo>()
        val conteo  = arrayListOf<String>()

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

                for(item in conteosList){
                    conteo.add(item.id.toString())
                    conteo.add(item.nombreEmpleado)
                    conteo.add(item.ubicacion)
                    conteo.add(item.idBodega.toString())
                    conteo.add(item.estado)
                    conteo.add(item.fechaInicio)
                    conteo.add(item.fechaFin)
                    conteo.add(item.fechaEnvio)
                    conteo.add(item.id_ajuste_inventario.toString())
                    conteo.add(item.tipoConteo)
                }
                conteos.close()
            }else{
                //MOSTRAR ERROR
            }
        }catch (e:Exception){
            throw Exception(e.message)
        }finally {
            db.close()
        }
        return conteo
    }

    fun registrarNuevoConteo(context: Context, ubicacion:String, tipoConteo:String): Long{
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
            data.put("Estado", "ABIERTO")
            data.put("Fecha_inicio", fechaInicio)
            data.put("Tipo_conteo", tipoConteo)

            idConteo = db.insert("conteoInventario", null, data)

            db.setTransactionSuccessful()

        }catch (e:Exception){
            throw Exception(e.message)
        }finally {
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