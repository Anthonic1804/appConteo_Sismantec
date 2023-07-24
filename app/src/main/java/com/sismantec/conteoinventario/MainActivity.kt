package com.sismantec.conteoinventario

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sismantec.conteoinventario.databinding.ActivityMainBinding
import controladores.ConexionController
import controladores.Funciones
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var from: String
    private lateinit var funciones: Funciones
    private lateinit var conexionController: ConexionController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        from = intent.getStringExtra("from").toString()
        funciones = Funciones()
        conexionController = ConexionController()

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

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStart() {
        super.onStart()

        binding.btnConectar.setOnClickListener {
            if(conexionController.validarCampos(binding.txtIp.text.toString(), binding.txtPuerto.text.toString())){
                GlobalScope.async {
                    if(funciones.isInternetReachable(this@MainActivity)){
                        val intent = Intent(this@MainActivity, Login::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "NO TIENES INTERNET", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                Toast.makeText(this@MainActivity, "LOS CAMPOS SON REQUERIDOS", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnCancelar.setOnClickListener {
            Toast.makeText(this@MainActivity, getString(R.string.proceso_cancelado), Toast.LENGTH_SHORT).show()

            val intent = Intent(this@MainActivity, Menu_principal::class.java)
            startActivity(intent)
            finish()
        }

    }

}