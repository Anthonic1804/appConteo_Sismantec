package com.sismantec.conteoinventario.controladores

import android.content.Context
import android.media.MediaPlayer
import com.sismantec.conteoinventario.R
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.DetalleConteoJSON
import com.sismantec.conteoinventario.modelos.ResponseInventario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConteoAutoController {

    private val funciones = Funciones()
    private val conteoManual = ConteoManualController()

    fun buscarProductoEnInventario(
        context: Context,
        query: String?,
        idConteo: Int
    ): ArrayList<ResponseInventario> {
        val db = funciones.getDataBase(context).readableDatabase
        val inventarioList = ArrayList<ResponseInventario>()
        var idInventario: Int = 0
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = db.rawQuery(
                    "SELECT * FROM inventario WHERE codigo='${query}'", null
                )
                if (lista.count > 0) {
                    lista.moveToFirst()
                    do {
                        val data = ResponseInventario(
                            lista.getInt(0),
                            lista.getString(1),
                            lista.getString(2)
                        )
                        inventarioList.add(data)
                    } while (lista.moveToNext())
                    lista.close()
                    for (item in inventarioList) {
                        idInventario = item.id
                    }
                    //FUNCION PARA BUSCAR EL PRODUCTO EN EL CONTEO
                    buscarProductoEnConteo(context, idConteo, idInventario)
                } else {
                    val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.alerta)
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                throw Exception(e.message + "BUSQUEDA EN INVENTARIO")
            } finally {
                db.close()
            }
        }
        return inventarioList
    }

    private fun buscarProductoEnConteo(context: Context, idConteo: Int, idInventario: Int) {
        val db = funciones.getDataBase(context).readableDatabase
        val itemDetalle = ArrayList<DetalleConteoJSON>()
        var unidadesProducto: Int = 0

        val consulta =
            "SELECT Id_inventario, Unidades, Fracciones FROM detalleConteo WHERE Id_inventario=${idInventario} " +
                    "AND Id_conteo_inventario=${idConteo}"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista = db.rawQuery(consulta, null)
                if (lista.count > 0) {
                    lista.moveToFirst()
                    do {
                        val data = DetalleConteoJSON(
                            lista.getInt(0),
                            lista.getInt(1),
                            lista.getInt(2)
                        )
                        itemDetalle.add(data)
                    } while (lista.moveToNext())
                    lista.close()

                    for (item in itemDetalle) {
                        unidadesProducto = item.existencias.toInt()
                    }

                    unidadesProducto += 1

                    //FUNCION PARA ACTUALIZAR LAS EXISTENCIAS DEL PRODUCTO
                    conteoManual.actualizarProductoConteo(
                        idConteo,
                        idInventario,
                        unidadesProducto,
                        0,
                        context
                    )

                    withContext(Dispatchers.Main) {
                        funciones.toastMensaje(context, "PRODUCTO CONTADO", 1)
                    }
                } else {
                    //FUNCION PARA REGISTRAR EL PRODUCTO EN EL CONTEO
                    conteoManual.registrarProductoConteo(
                        idConteo,
                        idInventario,
                        1,
                        0,
                        context
                    )
                    withContext(Dispatchers.Main) {
                        funciones.toastMensaje(context, "PRODUCTO AGREGADO AL CONTEO", 1)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    funciones.toastMensaje(
                        context,
                        "ERROR AL ACTUALIZAR O REGISTRAR EL PRODUCTO",
                        0
                    )
                }
            } finally {
                db.close()
            }
        }
    }

}