package com.sismantec.conteoinventario

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sismantec.conteoinventario.controladores.ConteoController
import com.sismantec.conteoinventario.controladores.InventarioController
import com.sismantec.conteoinventario.databinding.ActivityNuevoConteoBinding
import com.sismantec.conteoinventario.funciones.Funciones

class Nuevo_conteo : AppCompatActivity() {

    private lateinit var binding: ActivityNuevoConteoBinding
    private lateinit var imgExit: ImageButton
    private  var tipoConteo: String = "U"
    private val funciones = Funciones()
    private val inventarioController = InventarioController()
    private val controlador = ConteoController()
    private var nombreBodega : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoConteoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swConteoUnidades.isChecked = true
        binding.btnIniciar.isEnabled = false

        binding.tvFecha.text = "FECHA INICIO: ${funciones.getDateTime()}"

        cargarBodegas()

        if(inventarioController.seleccionarBodegasSQLite(this@Nuevo_conteo).count() == 1){
            binding.lyBodegas.visibility = View.GONE
            binding.btnIniciar.isEnabled = true
        }else{
            binding.lyUbicacion.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()

        //LOGICA DE TIPO CONTEO
        binding.swConteoUnidades.setOnCheckedChangeListener { _, isChecked ->
            binding.swConteoFracciones.isChecked = !isChecked
            tipoConteo="U"
        }

        binding.swConteoFracciones.setOnCheckedChangeListener { _, isChecked ->
            binding.swConteoUnidades.isChecked = !isChecked
            tipoConteo="F"
        }
        //FIN LOGICA DE TIPO CONTEO

        binding.imgHelp.setOnClickListener {
            mensajeHelp()
        }

        binding.btnCancelar.setOnClickListener {
            val conteos = controlador.obtenerConteoSQLite(this@Nuevo_conteo)
            funciones.toastMensaje(this@Nuevo_conteo, getString(R.string.proceso_cancelado), 0)
            if(conteos.isNotEmpty()){
                regresarConteosList()
            }else{
                regresar()
            }
        }

        //LOGICA DEL SPINNER DE BODEGAS
        binding.spBodegas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                nombreBodega = parent?.getItemAtPosition(position).toString()
                binding.btnIniciar.isEnabled = nombreBodega != "-- SELECCIONE UNA BODEGA --"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //NADA IMPLEMENTADO
            }

        }
        //FIN SPINNER

        binding.btnIniciar.setOnClickListener {

            if(inventarioController.seleccionarBodegasSQLite(this@Nuevo_conteo).count() == 1){
                nombreBodega = binding.txtUbicacion.text.toString()
            }

            val idConteo = controlador.registrarNuevoConteo(this@Nuevo_conteo, nombreBodega, tipoConteo)

            if(idConteo > 0){
                val prefs = this.getSharedPreferences("serverData", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("from", "nuevoConteo")
                editor.putInt("idConteo", idConteo.toInt())
                editor.putString("tipoConteo", tipoConteo)
                editor.putString("ubicacion", nombreBodega)
                editor.putString("estado", "HABILITADO")
                editor.putString("fechaInicio", funciones.getDateTime())
                editor.putString("fechaEnvio", null)
                editor.apply()

                val intent = Intent(this@Nuevo_conteo, ConteoInfo::class.java)
                startActivity(intent)
                finish()

                overridePendingTransition(R.anim.face_in, R.anim.face_out)
            }else{
                Toast.makeText(this,"ERROR EN EL ID DEL CONTEO", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mensajeHelp(){
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)

        updateDialog.setContentView(R.layout.mensaje_ayuda)
        imgExit = updateDialog.findViewById(R.id.imgSalir)

        imgExit.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    //FUNCION PARA CARGAR LAS BODEGAS AL SPINNER
    private fun cargarBodegas() {
        val listBodega = inventarioController.seleccionarBodegasSQLite(this@Nuevo_conteo) .toMutableList()
        val adaptador = ArrayAdapter(this@Nuevo_conteo, android.R.layout.simple_spinner_item, listBodega)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spBodegas.adapter = adaptador
    }

    private fun regresar(){
        val intent = Intent(this@Nuevo_conteo, Menu_principal::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    private fun regresarConteosList(){
        val intent = Intent(this@Nuevo_conteo, ConteosList::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}