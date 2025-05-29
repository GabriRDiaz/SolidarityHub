package com.upv.solidarityHub.ui.listaRecursos

import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.persistence.Baliza
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.upv.solidarityHub.R
import android.graphics.Color

class RecursosAdapter(
    private val balizas: List<Baliza>,
    private val onItemClick: (Baliza) -> Unit
) : RecyclerView.Adapter<RecursosAdapter.BalizaViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class BalizaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tipo: TextView = itemView.findViewById(R.id.tvTipo)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val container: View = itemView.findViewById(R.id.container)

        init {
            itemView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition

                if (previousSelected != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousSelected)
                }
                notifyItemChanged(selectedPosition)

                balizas.getOrNull(adapterPosition)?.let { baliza ->
                    onItemClick(baliza)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalizaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_baliza, parent, false)
        return BalizaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalizaViewHolder, position: Int) {
        val baliza = balizas[position]
        holder.nombre.text = baliza.nombre
        holder.tipo.text = baliza.tipo_recurso
        holder.descripcion.text = baliza.descripcion

        // Cambiar el fondo si está seleccionado
        if (selectedPosition == position) {
            holder.container.setBackgroundColor(Color.parseColor("#E3F2FD"))
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount() = balizas.size

    fun clearSelection() {
        val previousSelected = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        if (previousSelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelected)
        }
    }
}