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

    fun dialogoCancelar(){
        dialogo.dismiss()
    }

    fun dialogoCambiarTexto(mensaje: String){
        val texto = dialogo.findViewById<TextView>(R.id.txtCargandoData)
        if(texto != null){
            texto.text = mensaje
        }
    }



}