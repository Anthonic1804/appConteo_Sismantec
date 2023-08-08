package com.sismantec.conteoinventario.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sismantec.conteoinventario.R
import com.sismantec.conteoinventario.modelos.Conteo

class ConteoAdapter(
    private val lista: ArrayList<Conteo>,
    private val context: Context,
    val itemClic: (Int) -> Unit
) : RecyclerView.Adapter<ConteoAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConteoAdapter.MyViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.conteos_tarjeta, parent, false)
        return MyViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ConteoAdapter.MyViewHolder, position: Int) {
        holder.fecha.text = lista[position].fechaInicio
        holder.ubicacion.text = lista[position].ubicacion
        holder.empleado.text = lista[position].nombreEmpleado
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val fecha: TextView
        val ubicacion: TextView
        val empleado: TextView

        init {
            fecha = itemView.findViewById(R.id.tvFechaCarta)
            ubicacion = itemView.findViewById(R.id.tvUbicacionCarta)
            empleado = itemView.findViewById(R.id.tvEmpleadoCarta)

            itemView.setOnClickListener { itemClic(layoutPosition) }
        }

    }

}