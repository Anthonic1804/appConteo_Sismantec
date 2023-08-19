package com.sismantec.conteoinventario.controladores

import android.content.ContentValues
import android.content.Context
import com.sismantec.conteoinventario.funciones.Funciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConteoManualController {
    private var funciones = Funciones()

    fun registrarProductoConteo(idConteo: Int, idInventario: Int, unidades: Int, fracciones: Int, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val db = funciones.getDataBase(context).writableDatabase
            db.beginTransaction()
            val data = ContentValues()
            data.put("Id_conteo_inventario", idConteo)
            data.put("Id_inventario", idInventario)
            data.put("Unidades", unidades)
            data.put("Fracciones", fracciones)

            db.insert("detalleConteo", null, data)

            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun actualizarProductoConteo(idConteo: Int, idInventario: Int, unidades: Int, fracciones: Int, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val db = funciones.getDataBase(context).writableDatabase
            db.beginTransaction()
            val data = ContentValues()
            data.put("Unidades", unidades)
            data.put("Fracciones", fracciones)

            db.update("detalleConteo", data, "Id_conteo_inventario=${idConteo} AND Id_inventario=${idInventario}", null)

            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }

    fun eliminarProductoConteo(idConteo: Int, idInventario: Int, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val db = funciones.getDataBase(context).writableDatabase
            db.beginTransaction()
            db.delete("detalleConteo", "Id_conteo_inventario=${idConteo} AND Id_inventario=${idInventario}", null)
            db.setTransactionSuccessful()
            db.endTransaction()
            db.close()
        }
    }
}