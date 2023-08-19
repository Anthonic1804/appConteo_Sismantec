package com.sismantec.conteoinventario.funciones

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.AlertDialog
import android.view.LayoutInflater
import android.widget.TextView
import com.sismantec.conteoinventario.R

class AlertaDialogo(act: Activity) {
    var actividad: Activity
    private lateinit var dialogo: Dialog

    init {
        actividad = act
    }

    @SuppressLint("InflateParams")
    fun dialogoCargando(){
        val alerta: AlertDialog.Builder = AlertDialog.Builder(actividad)
        val ly: LayoutInflater = actividad.layoutInflater
        alerta.setView(ly.inflate(R.layout.alerta_carga, null))
        alerta.setCancelable(false)
        dialogo = alerta.create()
        dialogo.show()
    }

    fun dialogoEnviado() {
        val ale: AlertDialog.Builder = AlertDialog.Builder(actividad)
        val ly: LayoutInflater = actividad.layoutInflater
        ale.setView(ly.inflate(R.layout.alerta_enviado, null))
        ale.setCancelable(false)
        dialogo = ale.create()

        dialogo.show()
    }

    fun dialogoCancelar(){
        dialogo.dismiss()
    }

}