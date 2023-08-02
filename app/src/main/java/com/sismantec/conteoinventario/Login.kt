package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sismantec.conteoinventario.databinding.ActivityLoginBinding
import com.sismantec.conteoinventario.controladores.LoginController

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var controller = LoginController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            controller.iniciarSesion(this@Login, binding.txtUsuario.text.toString(), binding.txtPass.text.toString()){ conexionExitosa ->
                if(conexionExitosa){
                    menuPrincipal()
                }
            }
        }
    }
    private fun menuPrincipal(){
        val intent = Intent(this@Login, Menu_principal::class.java)
        startActivity(intent)
        finish()
    }
}