package com.sismantec.conteoinventario

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sismantec.conteoinventario.adapter.ConteoAdapter
import com.sismantec.conteoinventario.controladores.ConteoController
import com.sismantec.conteoinventario.databinding.ActivityConteosListBinding
import com.sismantec.conteoinventario.modelos.Conteo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConteosList : AppCompatActivity() {

    private lateinit var binding: ActivityConteosListBinding
    private var conteoController = ConteoController()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConteosListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when(intent.getStringExtra("from").toString()){
            "nuevoConteo"->{
                //SOLO PRUEBA
                val tipoConteo = intent.getStringExtra("tipoConteo")
                val idConteo = intent.getLongExtra("idConteo", 0)

                println("INFORMACION CAPTURADA DESDE NUEVO CONTEO\n" +
                        "ID CONTEO: $idConteo\n" +
                        "TIPO CONTEO: $tipoConteo")
            }
        }
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lista: ArrayList<Conteo> = conteoController.obtenerConteoSQLite(this@ConteosList)
                if(lista.size > 0){
                    runOnUiThread {
                        armarListaConteos(lista)
                    }
                }
            }catch (e: Exception){
                throw Exception(e.message)
            }
        }

        binding.imgRegresar.setOnClickListener {
            val intent = Intent(this@ConteosList, Menu_principal::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnAgregarConteo.setOnClickListener {
            val intent = Intent(this@ConteosList, Nuevo_conteo::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun armarListaConteos(lista: ArrayList<Conteo>){
        val mLayoutManager = LinearLayoutManager(this@ConteosList, LinearLayoutManager.VERTICAL, false)
        binding.rvListadoConteos.layoutManager = mLayoutManager

        val adapter = ConteoAdapter(lista, this@ConteosList){position ->
            val data = lista[position]

        }

        binding.rvListadoConteos.adapter = adapter
    }
}