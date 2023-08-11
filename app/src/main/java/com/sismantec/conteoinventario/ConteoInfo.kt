package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sismantec.conteoinventario.controladores.ConteoController
import com.sismantec.conteoinventario.databinding.ActivityConteoInfoBinding
import com.sismantec.conteoinventario.funciones.Funciones

class ConteoInfo : AppCompatActivity() {

    private lateinit var binding: ActivityConteoInfoBinding
    private var funciones = Funciones()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConteoInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val prefs = funciones.getPreferences(this@ConteoInfo)
        binding.tvEmpleado.text = prefs.empleado

        val tipo = when(prefs.tipoConteo){
            "U" -> "UNIDADES"
            else -> "UNIDADES Y FRACCIONES"
        }

        //SE OBTIENE TANTO DE NUEVO CONTEO COMO DE LISTCONTEO
        val idConteo = prefs.idConteo

        val ubicacion = prefs.ubicacion
        binding.tvUbicacion.text = ubicacion
        //****//

        when(prefs.from){
            "nuevoConteo"->{
                //DESHABILITANDO LOS BOTON ENVIAR Y HABILITAR
                binding.btnEnviarConteo.visibility = View.GONE
                binding.btnHabilitarConteo.visibility = View.GONE

                //INGRESANDO VALOR A LOS TEXTVIEW
                binding.tvTipoConteo.text = tipo
                binding.tvFechaInicio.text = funciones.getDateTime()
            }
            else -> {
                val estado = prefs.estado
                when(estado){
                    "HABILITADO"->{
                        binding.btnEnviarConteo.visibility = View.GONE
                        binding.btnHabilitarConteo.visibility = View.GONE
                    }
                    "CERRADO"->{
                        binding.btnCerrarConteo.visibility = View.GONE
                        binding.btnAgregarProducto.visibility = View.GONE
                    }
                    "ENVIADO"->{
                        binding.btnCerrarConteo.visibility = View.GONE
                        binding.btnEnviarConteo.visibility = View.GONE
                        binding.btnAgregarProducto.visibility = View.GONE
                    }
                }
                //INGRESANDO VALORES A LOS TEXTVIEW
                binding.tvTipoConteo.text = tipo
                binding.tvEstadoConteo.text = "ESTADO: ${estado}"
                binding.tvFechaInicio.text = prefs.fechaInicio
                binding.tvFechaEnvio.text = prefs.fechaEnvio
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.imgBackConteo.setOnClickListener {
            eliminarValoresdeConteoShared()
            regresarConteosList()
        }

        binding.btnAgregarProducto.setOnClickListener {
            val intent = Intent(this@ConteoInfo, InventarioList::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun regresarConteosList(){
        val intent = Intent(this@ConteoInfo, ConteosList::class.java)
        startActivity(intent)
        finish()
    }

    private fun eliminarValoresdeConteoShared(){
        val prefs = this@ConteoInfo.getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove("from")
        editor.remove("idConteo")
        editor.remove("tipoConteo")
        editor.remove("ubicacion")
        editor.remove("estado")
        editor.remove("fechaInicio")
        editor.remove("fechaEnvio")
        editor.apply()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}