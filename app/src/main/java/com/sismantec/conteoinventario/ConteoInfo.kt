package com.sismantec.conteoinventario

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

        val tipo = when(intent.getStringExtra("tipoConteo")){
            "U" -> "UNIDADES"
            else -> "UNIDADES Y FRACCIONES"
        }

        //SE OBTIENE TANTO DE NUEVO CONTEO COMO DE LISTCONTEO
        val idConteo = intent.getLongExtra("idConteo", 0)

        val ubicacion = intent.getStringExtra("ubicacion")
        binding.tvUbicacion.text = ubicacion
        //****//

        when(intent.getStringExtra("from").toString()){
            "nuevoConteo"->{
                //DESHABILITANDO LOS BOTON ENVIAR Y HABILITAR
                binding.btnEnviarConteo.visibility = View.GONE
                binding.btnHabilitarConteo.visibility = View.GONE

                //INGRESANDO VALOR A LOS TEXTVIEW
                binding.tvTipoConteo.text = tipo
                binding.tvFechaInicio.text = funciones.getDateTime()
            }
            "conteosList" -> {
                val estado = intent.getStringExtra("estado")
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
                binding.tvFechaInicio.text = intent.getStringExtra("fechaInicio")
                binding.tvFechaEnvio.text = intent.getStringExtra("fechaEnvio")
            }
            "inventarioList" -> {

            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.imgBackConteo.setOnClickListener {
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
}