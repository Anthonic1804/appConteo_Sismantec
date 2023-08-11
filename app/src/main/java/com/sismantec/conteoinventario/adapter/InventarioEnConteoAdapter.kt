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
import com.sismantec.conteoinventario.modelos.InventarioEnConteo
import com.sismantec.conteoinventario.modelos.ResponseInventario

class InventarioEnConteoAdapter(
    private val lista: ArrayList<InventarioEnConteo>,
    private val context: Context,
    val itemClic: (Int) -> Unit
) : RecyclerView.Adapter<InventarioEnConteoAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarioEnConteoAdapter.MyViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.inventario_enconteo_tarjeta, parent, false)
        return MyViewHolder(vista)
    }

    override fun onBindViewHolder(holder: InventarioEnConteoAdapter.MyViewHolder, position: Int) {
        holder.codigo.text = lista[position].codigoInventario
        holder.descripcion.text = lista[position].descripcion
        holder.unidades.text = lista[position].unidades.toString()
        holder.fracciones.text = lista[position].fracciones.toString()
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val codigo: TextView
        val descripcion: TextView
        val unidades: TextView
        val fracciones: TextView

        init {
            codigo = itemView.findViewById(R.id.tvCodigoCarta)
            descripcion = itemView.findViewById(R.id.tvDescripcionCarta)
            unidades = itemView.findViewById(R.id.tvUni)
            fracciones = itemView.findViewById(R.id.tvFra)

            itemView.setOnClickListener { itemClic(layoutPosition) }
        }

    }

}