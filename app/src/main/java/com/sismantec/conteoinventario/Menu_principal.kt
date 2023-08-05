package com.sismantec.conteoinventario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sismantec.conteoinventario.controladores.InventarioController
import com.sismantec.conteoinventario.databinding.ActivityMenuPrincipalBinding
import com.sismantec.conteoinventario.funciones.Funciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Menu_principal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding
    private var funciones = Funciones()
    private var controlador = InventarioController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = funciones.getPreferences(this@Menu_principal)

        with(binding){
            lblServidor.text = funciones.getServidor(prefs.ip, prefs.puerto)
            lblEmpleado.text = "EMPLEADO: ${prefs.empleado}"
        }

    }

    override fun onStart() {
        super.onStart()

        binding.imgServer.setOnClickListener {
            val intent = Intent(this@Menu_principal, MainActivity::class.java)
            intent.putExtra("from", "menu")
            startActivity(intent)
            finish()
        }

        binding.nuevoConteo.setOnClickListener {
            val intent = Intent(this@Menu_principal, Nuevo_conteo::class.java)
            startActivity(intent)
            finish()
        }

        binding.loadInventario.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if(funciones.isInternetReachable(this@Menu_principal)){
                    controlador.obtenerBodegas(this@Menu_principal)
                }else{
                    runOnUiThread {
                        funciones.toastMensaje(this@Menu_principal, "NO TIENES INTERNET", 0)
                    }
                }
            }
        }
    }
}