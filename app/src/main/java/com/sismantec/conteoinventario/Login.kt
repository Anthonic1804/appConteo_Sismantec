package com.sismantec.conteoinventario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sismantec.conteoinventario.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            if(binding.txtUsuario.text.toString() == "ADMIN"){
                val intent = Intent(this@Login, Menu_principal::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this@Login, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}