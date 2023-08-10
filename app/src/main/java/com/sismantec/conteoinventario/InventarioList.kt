package com.sismantec.conteoinventario

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sismantec.conteoinventario.adapter.InventarioAdapter
import com.sismantec.conteoinventario.controladores.InventarioController
import com.sismantec.conteoinventario.databinding.ActivityInventarioListBinding
import com.sismantec.conteoinventario.modelos.ResponseInventario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventarioList : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioListBinding
    private var inventario = InventarioController()

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

    }

    private fun armarListaInventario(lista: ArrayList<ResponseInventario>){
        val mLayoutManager = LinearLayoutManager(this@InventarioList, LinearLayoutManager.VERTICAL, false)
        binding.rvInventarioList.layoutManager = mLayoutManager

        val adapter = InventarioAdapter(lista, this@InventarioList){ position ->
            val data = lista[position]

        }

        binding.rvInventarioList.adapter = adapter
    }
}