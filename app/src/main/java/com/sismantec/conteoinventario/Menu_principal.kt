package com.sismantec.conteoinventario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sismantec.conteoinventario.databinding.ActivityMenuPrincipalBinding

class Menu_principal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.imgServer.setOnClickListener {
            val intent = Intent(this@Menu_principal, MainActivity::class.java)
            intent.putExtra("from", "menu")
            startActivity(intent)
            finish()
        }

    }
}