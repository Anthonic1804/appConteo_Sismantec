package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sismantec.conteoinventario.databinding.ActivityMainBinding
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.controladores.ConexionController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                with(binding){
                    txtIp.setText(funciones.getPreferences(this@MainActivity).ip.toString())
                    txtPuerto.setText(funciones.getPreferences(this@MainActivity).puerto.toString())
                    btnCancelar.visibility = View.VISIBLE
                    btnConectar.text = getString(R.string.reconectar)
                    txtDesigned.visibility = View.GONE
                }
            }
            else -> {
                binding.btnCancelar.visibility = View.GONE
            }
        }

    }

    override fun onStart() {
        super.onStart()
        binding.btnConectar.setOnClickListener {
            if(conexionController.validarCampos(binding.txtIp.text.toString(), binding.txtPuerto.text.toString())){
                CoroutineScope(Dispatchers.IO).launch {
                    if(funciones.isInternetReachable(this@MainActivity)){
                        conexionController.conectarServidor(binding.txtIp.text.toString(), binding.txtPuerto.text.toString(), this@MainActivity){ conexionExitosa ->
                            if(conexionExitosa){
                                almacenarServidor(binding.txtIp.text.toString(), binding.txtPuerto.text.toString())
                                iniciarSesion()
                            }
                        }
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
            cancelarProceso()
        }

    }
    private fun cancelarProceso(){
        val intent = Intent(this@MainActivity, Menu_principal::class.java)
        startActivity(intent)
        finish()
    }
    private fun iniciarSesion(){
        val intent = Intent(this@MainActivity, Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun almacenarServidor(ip: String, puerto:String){
        val prefs = getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("ip", ip)
        editor.putString("puerto", puerto)
        editor.apply()
    }

}