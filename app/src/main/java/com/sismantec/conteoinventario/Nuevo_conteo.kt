package com.sismantec.conteoinventario

import android.app.Dialog
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.sismantec.conteoinventario.databinding.ActivityNuevoConteoBinding

class Nuevo_conteo : AppCompatActivity() {

    private lateinit var binding: ActivityNuevoConteoBinding
    private lateinit var imgExit: ImageButton
    private  var tipoConteo: String = "U"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNuevoConteoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.swConteoUnidades.isChecked = true

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
            Toast.makeText(this@Nuevo_conteo, getString(R.string.proceso_cancelado), Toast.LENGTH_LONG).show()

            val intent = Intent(this@Nuevo_conteo, Menu_principal::class.java)
            startActivity(intent)
            finish()
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
}