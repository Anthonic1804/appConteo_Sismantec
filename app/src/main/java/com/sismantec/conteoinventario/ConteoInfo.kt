package com.sismantec.conteoinventario

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sismantec.conteoinventario.adapter.InventarioEnConteoAdapter
import com.sismantec.conteoinventario.controladores.ConteoController
import com.sismantec.conteoinventario.databinding.ActivityConteoInfoBinding
import com.sismantec.conteoinventario.funciones.AlertaDialogo
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.InventarioEnConteo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConteoInfo : AppCompatActivity() {

    private lateinit var binding: ActivityConteoInfoBinding
    private var funciones = Funciones()
    private lateinit var adapter: InventarioEnConteoAdapter
    private var controlador = ConteoController()
    private var idConteo: Int = 0
    private var estado: String = ""
    private var mensaje = AlertaDialogo(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConteoInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val prefs = funciones.getPreferences(this@ConteoInfo)
        binding.tvEmpleado.text = prefs.empleado

        val tipo = when(prefs.tipoConteo){
            "U" -> "AUTOMÁTICO"
            else -> "MANUAL"
        }

        //SE OBTIENE TANTO DE NUEVO CONTEO COMO DE LISTCONTEO
        idConteo = prefs.idConteo.toString().toInt()

        val ubicacion = prefs.ubicacion
        binding.tvUbicacion.text = ubicacion
        //****//

        when(prefs.from){
            "nuevoConteo"->{
                //DESHABILITANDO LOS BOTON ENVIAR Y HABILITAR
                binding.btnEnviarConteo.visibility = View.GONE
                binding.btnHabilitarConteo.visibility = View.GONE

                //INGRESANDO VALOR A LOS TEXTVIEW
                binding.tvTipoConteo.text = tipo
                binding.tvFechaInicio.text = funciones.getDateTime()
            }
            else -> {
                estado = prefs.estado.toString()
                when(estado){
                    "HABILITADO"->{
                        binding.btnEnviarConteo.visibility = View.GONE
                        binding.btnHabilitarConteo.visibility = View.GONE
                    }
                    "CERRADO"->{
                        binding.btnCerrarConteo.visibility = View.GONE
                        binding.btnAgregarProducto.visibility = View.GONE
                    }
                    "ENVIADO"->{
                        binding.btnCerrarConteo.visibility = View.GONE
                        binding.btnEnviarConteo.visibility = View.GONE
                        binding.btnAgregarProducto.visibility = View.GONE
                    }
                }
                //INGRESANDO VALORES A LOS TEXTVIEW
                binding.tvTipoConteo.text = tipo
                binding.tvEstadoConteo.text = "ESTADO: ${estado}"
                binding.tvFechaInicio.text = prefs.fechaInicio
                binding.tvFechaEnvio.text = prefs.fechaEnvio
            }
        }
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            val lista: ArrayList<InventarioEnConteo> = controlador.obtenerInventarioEnConteo(idConteo,this@ConteoInfo)
            if(lista.isNotEmpty()){
                armarListaInventario(lista)
            }
        }

        binding.imgBackConteo.setOnClickListener {
            eliminarValoresdeConteoShared()
            regresarConteosList()
        }

        binding.btnAgregarProducto.setOnClickListener {
            val intent = Intent(this@ConteoInfo, InventarioList::class.java)
            startActivity(intent)
            finish()

            overridePendingTransition(R.anim.face_in, R.anim.face_out)
        }

        binding.btnEnviarConteo.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if(funciones.isInternetReachable(this@ConteoInfo)){
                    withContext(Dispatchers.Main){
                        mensajeConteo("ENVIAR")
                    }
                }
            }
        }

        binding.btnCerrarConteo.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main){
                    mensajeConteo("CERRAR")
                }
            }
        }

        binding.btnHabilitarConteo.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main){
                    mensajeConteo("HABILITAR")
                }
            }
        }
    }

    private fun armarListaInventario(lista: ArrayList<InventarioEnConteo>){
        val tipo = funciones.getPreferences(this).tipoConteo.toString()
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvListadoProductosConteo.layoutManager = mLayoutManager

        adapter = if(tipo == "F" && estado == "HABILITADO"){
            InventarioEnConteoAdapter(lista, this){ position ->
                val data = lista[position]
                val prefs = this.getSharedPreferences("serverData", MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putString("from", "conteoInfo")
                editor.putInt("idProducto", data.idInventario)
                editor.putString("codigoProducto", data.codigoInventario)
                editor.putString("descripcionProducto", data.descripcion)
                editor.putString("unidades", data.unidades.toString())
                editor.putString("fracciones", data.fracciones.toString())
                editor.apply()

                val intent = Intent(this, ConteoManual::class.java)
                startActivity(intent)
                finish()

                overridePendingTransition(R.anim.face_in, R.anim.face_out)
            }
        }else{
            InventarioEnConteoAdapter(lista, this){
                //NO HACE NADA
            }
        }
        binding.rvListadoProductosConteo.adapter = adapter
    }

    private fun regresarConteosList(){
        val intent = Intent(this@ConteoInfo, ConteosList::class.java)
        startActivity(intent)
        finish()

        overridePendingTransition(R.anim.face_in, R.anim.face_out)
    }

    private fun eliminarValoresdeConteoShared(){
        val prefs = this@ConteoInfo.getSharedPreferences("serverData", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove("from")
        editor.remove("idConteo")
        editor.remove("tipoConteo")
        editor.remove("ubicacion")
        editor.remove("estado")
        editor.remove("fechaInicio")
        editor.remove("fechaEnvio")
        editor.apply()
    }

    //FUNCION DIALOGO PARA ELIMINAR EL PRODUCTO
    private fun mensajeConteo(tipo: String){
        val dialogo = Dialog(this)
        dialogo.setCancelable(false)
        dialogo.setContentView(R.layout.alert_cerrar_sesion_usuario)
        dialogo.findViewById<TextInputLayout>(R.id.lyAjuste).visibility = View.GONE

        val texto = when(tipo){
            "CERRAR" -> {
                "¿Desea Cerrar el Conteo?"
            }
            "HABILITAR" -> {
                "¿Desea Habilitar el Conteo?"
            }
            else->{
                dialogo.findViewById<TextInputLayout>(R.id.lyAjuste).visibility = View.VISIBLE
                dialogo.findViewById<Button>(R.id.btncerrar).text = "ENVIAR"
                dialogo.findViewById<Button>(R.id.btncancelar).text = "SALIR"
                "PARA ENVIAR EL CONTEO INGRESE EL \n ID DEL AJUSTE DEL SISTEMA ACAE"
            }
        }

        dialogo.findViewById<TextView>(R.id.txtsubtitulo).text = "INFORMACION"
        dialogo.findViewById<TextView>(R.id.txttitulo2).text = texto

        dialogo.findViewById<Button>(R.id.btncerrar).setOnClickListener {
            when(tipo){
                "CERRAR" -> {
                    controlador.cerrarConteo(this, idConteo)
                    funciones.toastMensaje(this, "CONTEO CERRADO CORRECTAMENTE", 1)
                    dialogo.dismiss()
                    regresarConteosList()
                }
                "HABILITAR"->{
                    controlador.habilitarConteo(this, idConteo)
                    funciones.toastMensaje(this, "CONTEO HABILITADO CORRECTAMENTE", 1)
                    dialogo.dismiss()
                    regresarConteosList()
                }
                else->{
                    val ajuste = dialogo.findViewById<TextInputEditText>(R.id.txtAjuste).text.toString()
                    if(ajuste.isEmpty() || ajuste.toInt() == 0){
                        funciones.toastMensaje(this,"EL CAMPO ESTÁ VACIO O EL DATO ES INCORRECTO", 0)
                    }else{
                        dialogo.dismiss()
                        verificarEnvio(ajuste.toInt())
                    }
                }
            }
        }

        dialogo.findViewById<Button>(R.id.btncancelar).setOnClickListener {
            dialogo.dismiss()
        }

        dialogo.show()
    }

    private fun verificarEnvio(ajuste: Int){
        controlador.enviarDataConteoServer(this, idConteo, ajuste){ respuesta->
            if(respuesta){
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main){
                        mensaje.dialogoEnviado()
                        delay(2000)
                        //ACTUALIZANDO EL ESTADO DEL CONTEO
                        controlador.conteoEnviado(this@ConteoInfo, idConteo, funciones.getDateTime(), ajuste.toInt())

                        mensaje.dialogoCancelar()
                        regresarConteosList()
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}