package com.sismantec.conteoinventario.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseConteo(context:Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private var tbl: TablasConteo = TablasConteo()

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "AcaeConteoInventario.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(tbl.inventario())
        db?.execSQL(tbl.bodega())
        db?.execSQL(tbl.conteoInventario())
        db?.execSQL(tbl.detalleConteo())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //METODOS QUE SE AGREGARAN AL ACTUALIZAR LA BD
    }

}