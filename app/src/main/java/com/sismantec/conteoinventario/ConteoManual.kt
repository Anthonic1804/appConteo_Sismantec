package com.sismantec.conteoinventario

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.sismantec.conteoinventario.controladores.ConteoManualController
import com.sismantec.conteoinventario.databinding.ActivityConteoManualBinding
import com.sismantec.conteoinventario.funciones.Funciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConteoManual : AppCompatActivity() {

    private lateinit var binding: ActivityConteoManualBinding
    private val funciones = Funciones()
    private val controlador = ConteoManualController()
    private var from: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConteoManualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCodigoManual.text = funciones.getPreferences(this@ConteoManual).codigoProducto
        binding.tvDescripcionManual.text = funciones.getPreferences(this@ConteoManual).descripcionProducto
        binding.btnEliminarExistencia.visibility = View.GONE

        when(funciones.getPreferences(this).from){
            "conteoInfo" -> {
                binding.txtUnidadesManual.setText(funciones.getPreferences(this).unidades.toString())
                binding.txtFraccionesManual.setText(funciones.getPreferences(this).fracciones.toString())
                binding.btnIngresarExistencia.text = "ACTUALIZAR"
                binding.btnEliminarExistencia.visibility = View.VISIBLE
                from = "conteoInfo"
            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.btnIngresarExistencia.setOnClickListener {
            if(binding.txtUnidadesManual.text!!.isEmpty()){
                funciones.toastMensaje(this@ConteoManual, "EL CAMPO UNIDADES ES REQUERIDO", 0)
            }else{
                val idConteo = funciones.getPreferences(this).idConteo.toString().toInt()
                val idInventario = funciones.getPreferences(this).idProducto.toString().toInt()
                val unidades = binding.txtUnidadesManual.text.toString().toFloat()
                var fracciones : Float = 0.00f
                if(binding.txtFraccionesManual.text.toString().isNotEmpty()){
                    fracciones = binding.txtFraccionesManual.text.toString().toFloat()
                }

                when(from){
                    "conteoInfo"->{
                        controlador.actualizarProductoConteo(idConteo, idInventario, unidades, fracciones, this)
                        funciones.toastMensaje(this@ConteoManual, "PRODUCTO ACTUALIZADO", 1)
                        elimarInforProducto()
                        regresarConteoInfo()
                    }else->{
                        controlador.registrarProductoConteo(idConteo, idInventario, unidades, fracciones, this)
                        funciones.toastMensaje(this@ConteoManual, "PRODUCTO REGISTRADO", 1)
                        elimarInforProducto()
                        regresarInventario()
                    }
                }
            }
        }

        binding.btnEliminarExistencia.setOnClickListener {
            val idConteo = funciones.getPreferences(this).idConteo.toString().toInt()
            val idInventario = funciones.getPreferences(this).idProducto.toString().toInt()
            mensajeEliminar(idConteo, idInventario)
        }

        binding.btnCancelar.setOnClickListener {
            elimarInforProducto()
            when(from){
                "conteoInfo" -> {
                    regresarConteoInfo()
                }else->{
                    regresarInventario()
                }
            }
        }
    }

    private fun elimarInforProducto(){
        val prefs = this@ConteoManual.getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove("idProducto")
        editor.remove("codigoProducto")
        editor.remove("descripcionProducto")
        editor.remove("from")
        editor.remove("unidades")
        editor.remove("fracciones")
        editor.apply()
    }

    private fun regresarInventario(){
        val intent = Intent(this@ConteoManual, InventarioList::class.java)
        startActivity(intent)
        finish()
    }

    private fun regresarConteoInfo(){
        val intent = Intent(this@ConteoManual, ConteoInfo::class.java)
        startActivity(intent)
        finish()
    }

    //FUNCION DIALOGO PARA ELIMINAR EL PRODUCTO
    private fun mensajeEliminar(idConteo: Int, idInventario: Int){
        val dialogo = Dialog(this@ConteoManual)
        dialogo.setCancelable(false)
        dialogo.setContentView(R.layout.alert_cerrar_sesion_usuario)
        dialogo.findViewById<TextView>(R.id.txtsubtitulo).text = "INFORMACION"
        dialogo.findViewById<TextView>(R.id.txttitulo2).text = "¿Desea Eliminar el Producto?"

        dialogo.findViewById<Button>(R.id.btncerrar).setOnClickListener {
            controlador.eliminarProductoConteo(idConteo, idInventario, this)
            funciones.toastMensaje(this@ConteoManual, "PRODUCTO ELIMINADO", 1)
            elimarInforProducto()
            dialogo.dismiss()
            regresarConteoInfo()
        }

        dialogo.findViewById<Button>(R.id.btncancelar).setOnClickListener {
            dialogo.dismiss()
        }

        dialogo.show()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}