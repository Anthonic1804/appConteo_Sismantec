package com.sismantec.conteoinventario.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sismantec.conteoinventario.InventarioList
import com.sismantec.conteoinventario.R
import com.sismantec.conteoinventario.modelos.Conteo
import com.sismantec.conteoinventario.modelos.ResponseInventario

class InventarioAdapter(
    private val lista: ArrayList<ResponseInventario>,
    private val context: Context,
    //private val tipo: String,
    val itemClic: (Int) -> Unit
) : RecyclerView.Adapter<InventarioAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarioAdapter.MyViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.inventario_tarjeta, parent, false)
        return MyViewHolder(vista)
    }

    override fun onBindViewHolder(holder: InventarioAdapter.MyViewHolder, position: Int) {
        holder.codigo.text = lista[position].codigo
        holder.descripcion.text = lista[position].descripcion
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val codigo: TextView
        val descripcion: TextView

        init {

            codigo = itemView.findViewById(R.id.tvCodigoCarta)
            descripcion = itemView.findViewById(R.id.tvDescripcionCarta)

            itemView.setOnClickListener { itemClic(layoutPosition) }
        }

    }

}