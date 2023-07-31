package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sismantec.conteoinventario.databinding.ActivityMenuPrincipalBinding
import com.sismantec.conteoinventario.funciones.Funciones

class Menu_principal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding
    private var funciones = Funciones()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val ip = prefs.getString("ip",null)
        val puerto = prefs.getString("puerto", null)
        val empleado = prefs.getString("empleado", null)

        with(binding){
            lblServidor.text = funciones.getServidor(ip, puerto)
            lblEmpleado.text = "EMPLEADO: $empleado"
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
    }
}