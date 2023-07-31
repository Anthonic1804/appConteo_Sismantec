package com.sismantec.conteoinventario

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.WindowManager

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val prefs = getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val ip = prefs.getString("ip",null)
        val puerto = prefs.getString("puerto", null)
        val empleado = prefs.getString("empleado", null)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            if(ip.isNullOrEmpty() && puerto.isNullOrEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else if(empleado.isNullOrEmpty()){
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, Menu_principal::class.java)
                startActivity(intent)
                finish()
            }
        }, 5000)

    }
}