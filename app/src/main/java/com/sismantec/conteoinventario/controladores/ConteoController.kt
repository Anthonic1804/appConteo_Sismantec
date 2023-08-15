package com.sismantec.conteoinventario.controladores

import android.content.ContentValues
import android.content.Context
import android.widget.Toast
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
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

class ConteoController {

    private var funciones = Funciones()
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

    fun obtenerIdBodega(context: Context, nombre: String): Int {
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
                        lista.getFloat(4),
                        lista.getFloat(5)
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
    fun obtenerDetalleConteo(context: Context, idConteo: Int): ArrayList<DetalleConteoJSON> {
        val db = funciones.getDataBase(context).readableDatabase
        val detalleLista = ArrayList<DetalleConteoJSON>()
        try {
            val consulta = db.rawQuery(
                "SELECT * FROM detalleConteo " +
                        "WHERE Id_conteo_inventario = ${idConteo}", null
            )

            if (consulta.count > 0) {
                consulta.moveToFirst()
                do {
                    val data = DetalleConteoJSON(
                        consulta.getInt(0),
                        consulta.getFloat(1),
                        consulta.getFloat(2)
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

    fun conteoInfoJSON(context: Context, idConteo: Int, idAjuste: Int): JSONObject {
        val db = funciones.getDataBase(context).readableDatabase
        var detalleConteo: Conteo? = null
        val dataConteo = JSONObject()

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
        dataConteo.put("fechaInicio", detalleConteo?.fechaInicio)
        dataConteo.put("fechaFin", detalleConteo?.fechaFin)
        dataConteo.put("fechaEnvio", funciones.getDateTime())
        dataConteo.put("ubicacion", detalleConteo?.ubicacion)
        dataConteo.put("idBodega", detalleConteo?.idBodega)
        dataConteo.put("idAjusteInventario", idAjuste)
        dataConteo.put("idEmpeado", funciones.getPreferences(context).idEmpleado)
        dataConteo.put("empleado", funciones.getPreferences(context).empleado)

        //CONSULTANDO EL DETALLE DEL CONTEO
        val detalleConteoLista = obtenerDetalleConteo(context, idConteo)
        val detalleEnviar = JSONArray()

        detalleConteoLista.forEach { item ->
            val dataDetalle = JSONObject()
            dataDetalle.put("Id_inventario", item.id_Inventario)
            dataDetalle.put("Existencia", item.existencias)
            dataDetalle.put("Existencia_u", item.existencias_u)

            detalleEnviar.put(dataDetalle)
        }
        dataConteo.put("detalle", detalleEnviar)

        //ACTUALIZANDO EL ESTADO DEL CONTEO
        conteoEnviado(context, idConteo, funciones.getDateTime(), idAjuste)

        return dataConteo
    }

    fun enviarDataConteoServer(
        context: Context,
        idConteo: Int,
        idAjuste: Int,
        callback: (Boolean) -> Unit
    ) {
        val prefs = funciones.getPreferences(context)
        val url = funciones.getServidor(prefs.ip, prefs.puerto)

        val data = conteoInfoJSON(context, idConteo, idAjuste)
        println(data)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: Response<ResponseConexion> =
                    funciones.getRetrofit(url).create(APIService::class.java)
                        .enviarConteoServer(data)

                //val respuesta = response.body()
                println("RESPUESTA OBTENIDA DEL SERVIDOR" + response)

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