package com.sismantec.conteoinventario

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.sismantec.conteoinventario.controladores.ConteoController
import com.sismantec.conteoinventario.controladores.InventarioController
import com.sismantec.conteoinventario.controladores.LoginController
import com.sismantec.conteoinventario.databinding.ActivityMenuPrincipalBinding
import com.sismantec.conteoinventario.funciones.Funciones
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Menu_principal : AppCompatActivity() {

    private lateinit var binding: ActivityMenuPrincipalBinding
    private var funciones = Funciones()
    private var controlador = InventarioController()
    private var loginController = LoginController()
    private var conteoController = ConteoController()

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

            overridePendingTransition(R.anim.face_in, R.anim.face_out)
        }

        binding.nuevoConteo.setOnClickListener {
            if(controlador.seleccionarInventarioSQLite(this@Menu_principal, "").isNotEmpty()){
                val conteos = conteoController.obtenerConteoSQLite(this@Menu_principal)
                if(conteos.isNotEmpty()){
                    conteosList()
                }else{
                    nuevoConteo()
                }
            }else{
                funciones.toastMensaje(this@Menu_principal, "NO SE HA CARGADO EL INVENTARIO", 0)
            }
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

        binding.imgSalir.setOnClickListener {
            cerrarSesion()
        }
    }

    //FUNCION DIALOGO PARA CERRAR SESION
    private fun cerrarSesion(){
        val idEmpleado = funciones.getPreferences(this@Menu_principal).idEmpleado
        val dialogo = Dialog(this@Menu_principal)
        dialogo.setCancelable(false)
        dialogo.setContentView(R.layout.alert_cerrar_sesion_usuario)
        dialogo.findViewById<TextInputLayout>(R.id.lyAjuste).visibility = View.GONE

        dialogo.findViewById<Button>(R.id.btncerrar).setOnClickListener {
            if (idEmpleado != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    if(funciones.isInternetReachable(this@Menu_principal)){
                        loginController.cerrarSesion(this@Menu_principal, idEmpleado){
                            editPreferences()
                            dialogo.dismiss()
                        }
                    }else{
                        runOnUiThread {
                            funciones.toastMensaje(this@Menu_principal, "NO TIENES INTENET", 0)
                        }
                    }
                }
            }
        }

        dialogo.findViewById<Button>(R.id.btncancelar).setOnClickListener {
            dialogo.dismiss()
        }

        dialogo.show()
    }

    private fun editPreferences(){
        val prefs2 = this@Menu_principal.getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val editor = prefs2.edit()
        editor.putString("empleado", null)
        editor.putInt("idEmpleado", 0)
        editor.putInt("esAdmin", 0)
        editor.apply()

        val intent = Intent(this@Menu_principal, SplashScreen::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    private fun nuevoConteo(){
        val intent = Intent(this@Menu_principal, Nuevo_conteo::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    private fun conteosList(){
        val intent = Intent(this@Menu_principal, ConteosList::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }
}