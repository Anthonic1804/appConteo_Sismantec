package com.sismantec.conteoinventario

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.sismantec.conteoinventario.adapter.InventarioAdapter
import com.sismantec.conteoinventario.controladores.ConteoAutoController
import com.sismantec.conteoinventario.controladores.InventarioController
import com.sismantec.conteoinventario.databinding.ActivityInventarioListBinding
import com.sismantec.conteoinventario.funciones.Funciones
import com.sismantec.conteoinventario.modelos.ResponseInventario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class InventarioList : AppCompatActivity(){

    private lateinit var binding: ActivityInventarioListBinding
    private var inventario = InventarioController()
    private var conteoAuto = ConteoAutoController()
    private var funciones = Funciones()
    private lateinit var adapter: InventarioAdapter

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            funciones.toastMensaje(this, "LECTURA CANCELADA", 0)
        } else {
            if(funciones.getPreferences(this).tipoConteo.toString() == "U"){
                 binding.svProductosList.setQuery(result.contents, true)
            }else{
                binding.svProductosList.setQuery(result.contents, false)
                binding.svProductosList.findFocus()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventarioListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(funciones.getPreferences(this).tipoConteo == "U"){
            binding.svProductosList.isEnabled = false
            binding.svProductosList.onActionViewExpanded()
        }else{
            binding.svProductosList.findFocus()
            binding.svProductosList.onActionViewExpanded()
        }
    }

    override fun onStart() {
        super.onStart()

        CoroutineScope(Dispatchers.IO).launch {
            val lista: ArrayList<ResponseInventario> = inventario.seleccionarInventarioSQLite(this@InventarioList, "")
            if(lista.isNotEmpty()){
                withContext(Dispatchers.Main){
                    armarListaInventario(lista)
                }
            }
        }

        binding.imgRegresar.setOnClickListener {
            val intent = Intent(this@InventarioList, ConteoInfo::class.java)
            startActivity(intent)
            finish()

            overridePendingTransition(R.anim.face_in, R.anim.face_out)
        }

        //IMAGEN DEL LECTOR DE CODIGO DE BARRA
        binding.imgBarra.setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
            options.setPrompt("LECTOR DE CODIGO DE BARRA - SISMANTEC")
            options.setCameraId(0)
            options.setOrientationLocked(true);
            options.setBeepEnabled(true)
            options.setBarcodeImageEnabled(true)
            barcodeLauncher.launch(options)
        }

        binding.svProductosList.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(funciones.getPreferences(this@InventarioList).tipoConteo == "U"){
                    buscarProductoAuto(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(funciones.getPreferences(this@InventarioList).tipoConteo != "U"){
                    buscarProductoManual(newText)
                }
                return false
            }

        })

    }

    //FUNCION DE BUSQUEDA DEL PRODUCTO MANUAL
    private fun buscarProductoManual(query: String?){
        CoroutineScope(Dispatchers.IO).launch {
            val lista: ArrayList<ResponseInventario> = inventario.seleccionarInventarioSQLite(this@InventarioList, "$query")
            if(lista.isNotEmpty()){
                withContext(Dispatchers.Main){
                    armarListaInventario(lista)
                }
            }
        }
    }

    //FUNCION BUSCAR PRODUCTO AUTOMATICO
    private fun buscarProductoAuto(query: String?){
        val idConteo = funciones.getPreferences(this@InventarioList).idConteo.toString().toInt()
        CoroutineScope(Dispatchers.IO).launch {
            val lista: ArrayList<ResponseInventario> = conteoAuto.buscarProductoEnInventario(this@InventarioList, query, idConteo)
            if(lista.isNotEmpty()){
                withContext(Dispatchers.Main){
                    armarListaInventario(lista)
                    delay(1500)
                    binding.svProductosList.setQuery("", false)
                }
            }
        }
    }

    //FUNCION PARA ARMAR LISTA DEL RECYVLERVIEW
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

                overridePendingTransition(R.anim.face_in, R.anim.face_out)
            }
        }else{
            InventarioAdapter(lista, this@InventarioList){

            }
        }
        binding.rvInventarioList.adapter = adapter
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}