package com.sismantec.conteoinventario

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.sismantec.conteoinventario.databinding.ActivityMainBinding
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.controladores.ConexionController
import com.sismantec.conteoinventario.modelos.InventarioEnConteo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var from: String
    private var funciones = Funciones()
    private var conexionController = ConexionController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        from = intent.getStringExtra("from").toString()

        when (from) {
            "menu" -> {
                with(binding) {
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
            when (from) {
                "menu" -> {
                    if ((funciones.getPreferences(this).ip.toString() != binding.txtIp.text.toString())
                        || (funciones.getPreferences(this).puerto.toString() != binding.txtPuerto.text.toString())
                    ) {
                        mensajeConexion()
                    } else {
                        conectarServidor()
                    }
                }
                else -> {
                    conectarServidor()
                }
            }
        }

        binding.btnCancelar.setOnClickListener {
            funciones.toastMensaje(this@MainActivity, getString(R.string.proceso_cancelado), 0)
            cancelarProceso()
        }

    }

    private fun cancelarProceso() {
        val intent = Intent(this@MainActivity, Menu_principal::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    //FUNCION CONECTAR SERVIDOR
    private fun conectarServidor() {
        if (conexionController.validarCampos(
                binding.txtIp.text.toString(),
                binding.txtPuerto.text.toString()
            )
        ) {
            CoroutineScope(Dispatchers.IO).launch {
                if (funciones.isInternetReachable(this@MainActivity)) {
                    conexionController.conectarServidor(
                        binding.txtIp.text.toString(),
                        binding.txtPuerto.text.toString(),
                        this@MainActivity
                    ) { conexionExitosa ->
                        if (conexionExitosa) {
                            when (from) {
                                "menu" -> {
                                    menuPrincipal()
                                }
                                else -> {
                                    almacenarServidor(
                                        binding.txtIp.text.toString(),
                                        binding.txtPuerto.text.toString()
                                    )
                                    iniciarSesion()
                                }
                            }
                        }else{
                            splashScreen()
                        }
                    }
                } else {
                    runOnUiThread {
                        funciones.toastMensaje(this@MainActivity, "NO TIENES INTERNET", 0)
                    }
                }
            }
        } else {
            funciones.toastMensaje(this@MainActivity, "LOS CAMPOS SON REQUERIDOS", 0)
        }
    }

    private fun iniciarSesion() {
        val intent = Intent(this@MainActivity, Login::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    private fun splashScreen() {
        val intent = Intent(this@MainActivity, SplashScreen::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    private fun menuPrincipal(){
        val intent = Intent(this@MainActivity, Menu_principal::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    private fun almacenarServidor(ip: String, puerto: String) {
        val prefs = getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("ip", ip)
        editor.putString("puerto", puerto)
        editor.apply()
    }

    //FUNCION DIALOGO PARA ELIMINAR EL PRODUCTO
    private fun mensajeConexion() {
        val dialogo = Dialog(this)
        dialogo.setCancelable(false)
        dialogo.setContentView(R.layout.alert_cerrar_sesion_usuario)
        dialogo.findViewById<TextView>(R.id.txtsubtitulo).text = "INFORMACION"
        dialogo.findViewById<TextView>(R.id.txttitulo2).text = "Al Cambiar de Conexion de Servidor se \n eliminará toda la información de la Aplicación"
        dialogo.findViewById<TextInputLayout>(R.id.lyAjuste).visibility = View.GONE

        dialogo.findViewById<Button>(R.id.btncerrar).setOnClickListener {
            conexionController.eliminarDataApp(this@MainActivity)
            from = ""
            conectarServidor()
        }

        dialogo.findViewById<Button>(R.id.btncancelar).setOnClickListener {
            dialogo.dismiss()
        }

        dialogo.show()
    }

}