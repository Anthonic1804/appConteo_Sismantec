package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sismantec.conteoinventario.databinding.ActivityConteoManualBinding
import com.sismantec.conteoinventario.funciones.Funciones

class ConteoManual : AppCompatActivity() {

    private lateinit var binding: ActivityConteoManualBinding
    private val funciones = Funciones()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConteoManualBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCodigoManual.text = funciones.getPreferences(this@ConteoManual).codigoProducto
        binding.tvDescripcionManual.text = funciones.getPreferences(this@ConteoManual).descripcionProducto
    }

    override fun onStart() {
        super.onStart()

        binding.btnCancelar.setOnClickListener {
            elimarInforProducto()
            val intent = Intent(this@ConteoManual, InventarioList::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun elimarInforProducto(){
        val prefs = this@ConteoManual.getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove("idProducto")
        editor.remove("codigoProducto")
        editor.remove("descripcionProducto")
        editor.apply()

    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}