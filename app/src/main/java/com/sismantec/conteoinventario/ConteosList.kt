package com.sismantec.conteoinventario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sismantec.conteoinventario.databinding.ActivityConteosListBinding

class ConteosList : AppCompatActivity() {

    private lateinit var binding: ActivityConteosListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConteosListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.imgRegresar.setOnClickListener {
            val intent = Intent(this@ConteosList, Menu_principal::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnAgregarConteo.setOnClickListener {
            val intent = Intent(this@ConteosList, Nuevo_conteo::class.java)
            startActivity(intent)
            finish()
        }
    }
}