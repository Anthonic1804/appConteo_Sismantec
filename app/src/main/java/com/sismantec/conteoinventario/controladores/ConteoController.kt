package com.sismantec.conteoinventario.controladores

import android.content.ContentValues
import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.sismantec.conteoinventario.apiservices.APIService
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.Conteo
import com.sismantec.conteoinventario.modelos.DetalleConteoJSON
import com.sismantec.conteoinventario.modelos.InventarioEnConteo
import com.sismantec.conteoinventario.modelos.ResponseBodegas
import com.sismantec.conteoinventario.modelos.ResponseConexion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ConteoController {

    private var funciones = Funciones()
    //FUNCION PARA OBTENER TODOS LOS CONTEOS DE SQLITE
    fun obtenerConteoSQLite(context: Context): ArrayList<Conteo> {
        val db = funciones.getDataBase(context).readableDatabase
        val conteosList = ArrayList<Conteo>()

        try {
            val conteos = db.rawQuery("SELECT * FROM conteoInventario", null)
            if (conteos.count > 0) {
                conteos.moveToFirst()
                do {
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
                } while (conteos.moveToNext())
                conteos.close()
            } else {
                //MOSTRAR ERROR
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        } finally {
            db.close()
        }
        return conteosList
    }

    //FUNCION PARA REGISTRAR UN NUEVO CONTEO
    fun registrarNuevoConteo(context: Context, ubicacion: String, tipoConteo: String): Long {
        val db = funciones.getDataBase(context).writableDatabase
        val fechaInicio = funciones.getDateTime()
        val prefs = funciones.getPreferences(context)
        val idBodega = obtenerIdBodega(context, ubicacion)
        var idConteo: Long = 0

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
        } catch (e: Exception) {
            throw Exception(e.message)
        } finally {
            db.endTransaction()
            db.close()
        }
        return idConteo
    }

    //FUNCION PARA OBTENER EL ID DE LA BODEGA SELECCIONADA
    private fun obtenerIdBodega(context: Context, nombre: String): Int {
        val db = funciones.getDataBase(context).readableDatabase
        val bodegasList = ArrayList<ResponseBodegas>()
        var idBodegas = 0

        try {
            val bodegas = db.rawQuery("SELECT * FROM bodegas WHERE Nombre='${nombre}'", null)
            if (bodegas.count > 0) {
                bodegas.moveToFirst()
                do {
                    val data = ResponseBodegas(
                        bodegas.getInt(0),
                        bodegas.getString(1)
                    )
                    bodegasList.add(data)
                } while (bodegas.moveToNext())

                for (item in bodegasList) {
                    idBodegas = item.id
                }
            } else {
                //RESPUESTA SI NO SE ENCUENTRAN BODEGAS AL MACENADAS
            }
            bodegas.close()
        } catch (e: Exception) {
            throw Exception(e.message)
        } finally {
            db.close()
        }
        return idBodegas
    }

    //FUNCION PARA OBTENER EL INVENTARIO DEL CONTEO EN CURSO
    fun obtenerInventarioEnConteo(idConteo: Int, context: Context): ArrayList<InventarioEnConteo> {
        val db = funciones.getDataBase(context).readableDatabase
        val inventarioList = ArrayList<InventarioEnConteo>()
        try {
            val lista = db.rawQuery(
                "SELECT dt.Id_conteo_inventario, " +
                        "dt.Id_inventario, " +
                        "i.Codigo, " +
                        "i.Descripcion, " +
                        "dt.Unidades," +
                        "dt.Fracciones FROM detalleConteo AS dt " +
                        "INNER JOIN inventario AS i ON dt.Id_inventario = i.id " +
                        "WHERE dt.Id_conteo_inventario = ${idConteo}", null
            )

            if (lista.count > 0) {
                lista.moveToFirst()
                do {
                    val data = InventarioEnConteo(
                        lista.getInt(0),
                        lista.getInt(1),
                        lista.getString(2),
                        lista.getString(3),
                        lista.getInt(4),
                        lista.getInt(5)
                    )
                    inventarioList.add(data)
                } while (lista.moveToNext())
            } else {
                //NADA IMPLEMENTADO
            }
            lista.close()
        } catch (e: Exception) {
            throw Exception(e.message)
        } finally {
            db.close()
        }

        return inventarioList
    }

    //FUNCION PARA CERRAR EL CONTEO
    fun cerrarConteo(context: Context, idConteo: Int) {
        val db = funciones.getDataBase(context).writableDatabase
        db.beginTransaction()
        val data = ContentValues()
        data.put("estado", "CERRADO")
        data.put("Fecha_fin", funciones.getDateTime())

        db.update("conteoInventario", data, "Id = ${idConteo}", null)

        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()
    }

    //FUNCION PARA HABILITAR EL CONTEO
    fun habilitarConteo(context: Context, idConteo: Int) {
        val db = funciones.getDataBase(context).writableDatabase
        db.beginTransaction()
        val data = ContentValues()
        data.put("estado", "HABILITADO")
        data.put("Fecha_fin", "")
        data.put("Fecha_envio", "")
        data.put("Id_ajuste_inventario", 0)

        db.update("conteoInventario", data, "Id = ${idConteo}", null)
        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()
    }

    //FUNCION CONTEO ENVIADO
    fun conteoEnviado(context: Context, idConteo: Int, fechaEnvio: String, idAjuste: Int) {
        val db = funciones.getDataBase(context).writableDatabase
        db.beginTransaction()
        val data = ContentValues()
        data.put("estado", "ENVIADO")
        data.put("Fecha_envio", fechaEnvio)
        data.put("Id_ajuste_inventario", idAjuste)

        db.update("conteoInventario", data, "Id = ${idConteo}", null)
        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()
    }

    //FUNCION OBTENER CONTEOINFO y DETALLE PARA ENVIAR A SERVIDOR
    private fun obtenerDetalleConteo(context: Context, idConteo: Int): ArrayList<DetalleConteoJSON> {
        val db = funciones.getDataBase(context).readableDatabase
        val detalleLista = ArrayList<DetalleConteoJSON>()
        try {
            val consulta = db.rawQuery(
                "SELECT Id_inventario, Unidades, Fracciones FROM detalleConteo " +
                        "WHERE Id_conteo_inventario = ${idConteo}", null
            )

            if (consulta.count > 0) {
                consulta.moveToFirst()
                do {
                    val data = DetalleConteoJSON(
                        consulta.getInt(0),
                        consulta.getInt(1),
                        consulta.getInt(2)
                    )
                    detalleLista.add(data)
                } while (consulta.moveToNext())
                consulta.close()
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        } finally {
            db.close()
        }
        return detalleLista
    }

    //FUNCION PARA CREAR EL JSON QUE SE ENVIARA AL SERVIDOR
    private fun conteoInfoJSON(context: Context, idConteo: Int, idAjuste: Int): JsonObject {
        val db = funciones.getDataBase(context).readableDatabase
        var detalleConteo: Conteo? = null
        val dataConteo = JsonObject()
        try {
            val consulta = db.rawQuery(
                "SELECT * FROM conteoInventario " +
                        "WHERE Id=${idConteo}", null
            )

            if (consulta.count > 0) {
                consulta.moveToFirst()
                detalleConteo = Conteo(
                    consulta.getInt(0),
                    consulta.getString(1),
                    consulta.getString(2),
                    consulta.getInt(3),
                    consulta.getString(4),
                    consulta.getString(5),
                    consulta.getString(6),
                    consulta.getString(7),
                    consulta.getInt(8),
                    consulta.getString(9)
                )
                consulta.close()
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        } finally {
            db.close()
        }

        //CONSULTADO LA IFNORMACION DEL CONTEO
        dataConteo.addProperty("fechaInicio", detalleConteo?.fechaInicio)
        dataConteo.addProperty("fechaFin", detalleConteo?.fechaFin)
        dataConteo.addProperty("fechaEnvio", funciones.getDateTime())
        dataConteo.addProperty("ubicacion", detalleConteo?.ubicacion)
        dataConteo.addProperty("idBodega", detalleConteo?.idBodega)
        dataConteo.addProperty("idAjusteInventario", idAjuste)
        dataConteo.addProperty("idEmpleado", funciones.getPreferences(context).idEmpleado)
        dataConteo.addProperty("empleado", funciones.getPreferences(context).empleado)

        //CONSULTANDO EL DETALLE DEL CONTEO
        val detalleConteoLista = obtenerDetalleConteo(context, idConteo)
        val detalleEnviar = JsonArray()

        detalleConteoLista.forEach { item ->
            val dataDetalle = JsonObject()
            dataDetalle.addProperty("id_Inventario", item.id_Inventario)
            dataDetalle.addProperty("existencias", item.existencias)
            dataDetalle.addProperty("existencias_u", item.existencias_u)
            detalleEnviar.add(dataDetalle)
        }

        dataConteo.add("detalle", detalleEnviar)
        return dataConteo
    }

    //FUNCION PARA ENVIAR EL CONTEO AL SERVIDOR
    fun enviarDataConteoServer(
        context: Context,
        idConteo: Int,
        idAjuste: Int,
        callback: (Boolean) -> Unit
    ) {
        val prefs = funciones.getPreferences(context)
        val url = funciones.getServidor(prefs.ip, prefs.puerto)

        val data = conteoInfoJSON(context, idConteo, idAjuste)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<ResponseConexion> =
                    funciones.getRetrofit(url).create(APIService::class.java)
                        .enviarConteoServer(data)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        callback(true)
                    } else {
                        withContext(Dispatchers.Main) {
                            funciones.toastMensaje(
                                context,
                                "ERROR AL ENVIAR EL CONTEO VERIFIQUE SU ID DE AJUSTE",
                                0
                            )
                        }
                        callback(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    funciones.toastMensaje(context, "ERROR DE CONEXION CON EL SERVIDOR", 0)
                }
                callback(false)
            }
        }
    }
}