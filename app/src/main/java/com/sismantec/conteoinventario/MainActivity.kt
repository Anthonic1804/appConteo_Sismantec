package com.sismantec.conteoinventario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.sismantec.conteoinventario.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var from: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        from = intent.getStringExtra("from").toString()

        when(from){
            "menu" -> {
                binding.btnCancelar.visibility = View.VISIBLE
                binding.btnConectar.text = getString(R.string.reconectar)
                binding.txtDesigned.visibility = View.GONE
            }
            else -> {
                binding.btnCancelar.visibility = View.GONE
            }
        }

        binding.btnConectar.setOnClickListener {
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnCancelar.setOnClickListener {
            Toast.makeText(this@MainActivity, getString(R.string.proceso_cancelado), Toast.LENGTH_SHORT).show()

            val intent = Intent(this@MainActivity, Menu_principal::class.java)
            startActivity(intent)
            finish()
        }
    }

}