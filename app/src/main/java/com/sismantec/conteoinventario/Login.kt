package com.sismantec.conteoinventario

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sismantec.conteoinventario.controladores.LoginController
import com.sismantec.conteoinventario.databinding.ActivityLoginBinding
import com.sismantec.conteoinventario.funciones.Funciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var controller = LoginController()
    private var funciones = Funciones()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        binding.btnIngresar.setOnClickListener {
            if(controller.validarCampos(binding.txtUsuario.text.toString(), binding.txtPass.text.toString())){
                CoroutineScope(Dispatchers.IO).launch {
                    if(funciones.isInternetReachable(this@Login)){
                        controller.iniciarSesion(this@Login, binding.txtUsuario.text.toString(), binding.txtPass.text.toString()){ conexionExitosa ->
                            if(conexionExitosa){
                                menuPrincipal()
                            }
                        }
                    }else{
                        runOnUiThread {
                            funciones.toastMensaje(this@Login, "NO TIENES INTERNET", 0)
                        }
                    }
                }
            }else{
                funciones.toastMensaje(this@Login, "LOS CAMPOS SON REQUERIDO", 0)
            }
        }
    }

    private fun menuPrincipal(){
        val intent = Intent(this@Login, Menu_principal::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }
}