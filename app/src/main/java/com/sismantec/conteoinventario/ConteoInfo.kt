package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sismantec.conteoinventario.adapter.InventarioAdapter
import com.sismantec.conteoinventario.adapter.InventarioEnConteoAdapter
import com.sismantec.conteoinventario.controladores.ConteoController
import com.sismantec.conteoinventario.databinding.ActivityConteoInfoBinding
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.InventarioEnConteo
import com.sismantec.conteoinventario.modelos.ResponseInventario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConteoInfo : AppCompatActivity() {

    private lateinit var binding: ActivityConteoInfoBinding
    private var funciones = Funciones()
    private lateinit var adapter: InventarioEnConteoAdapter
    private var controlador = ConteoController()
    private var idConteo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityConteoInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val prefs = funciones.getPreferences(this@ConteoInfo)
        binding.tvEmpleado.text = prefs.empleado

        val tipo = when(prefs.tipoConteo){
            "U" -> "UNIDADES"
            else -> "UNIDADES Y FRACCIONES"
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
                val estado = prefs.estado
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
        }
    }

    private fun armarListaInventario(lista: ArrayList<InventarioEnConteo>){
        val tipo = funciones.getPreferences(this).tipoConteo.toString()
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvListadoProductosConteo.layoutManager = mLayoutManager

        adapter = if(tipo == "F"){
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

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}