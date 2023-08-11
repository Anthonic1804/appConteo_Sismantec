package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sismantec.conteoinventario.adapter.InventarioAdapter
import com.sismantec.conteoinventario.controladores.InventarioController
import com.sismantec.conteoinventario.databinding.ActivityInventarioListBinding
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.ResponseInventario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventarioList : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioListBinding
    private var inventario = InventarioController()
    private var funciones = Funciones()
    private lateinit var adapter: InventarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventarioListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            val lista: ArrayList<ResponseInventario> = inventario.seleccionarInventarioSQLite(this@InventarioList, "")
            if(lista.isNotEmpty()){
                armarListaInventario(lista)
            }
        }

        binding.imgRegresar.setOnClickListener {
            val intent = Intent(this@InventarioList, ConteoInfo::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun armarListaInventario(lista: ArrayList<ResponseInventario>){
        val tipo = funciones.getPreferences(this@InventarioList).tipoConteo.toString()
        val mLayoutManager = LinearLayoutManager(this@InventarioList, LinearLayoutManager.VERTICAL, false)
        binding.rvInventarioList.layoutManager = mLayoutManager

        adapter = if(tipo == "F"){
            InventarioAdapter(lista, this@InventarioList){ position ->
                val data = lista[position]
                val prefs = this@InventarioList.getSharedPreferences("serverData", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putInt("idProducto", data.id)
                editor.putString("codigoProducto", data.codigo)
                editor.putString("descripcionProducto", data.descripcion)
                editor.apply()

                val intent = Intent(this@InventarioList, ConteoManual::class.java)
                startActivity(intent)
                finish()
            }
        }else{
            InventarioAdapter(lista, this@InventarioList){
                //NO HACE NADA
            }
        }
        binding.rvInventarioList.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}