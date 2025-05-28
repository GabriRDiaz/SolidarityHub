package com.upv.solidarityHub.ui.listaDesaparecidos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.model.Desaparecido
import android.graphics.Color

class DesaparecidoAdapter(
    private val desaparecidos: List<Desaparecido>,
    private val onItemClick: (Desaparecido) -> Unit
) : RecyclerView.Adapter<DesaparecidoAdapter.DesaparecidoViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class DesaparecidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombre)
        val apellidos: TextView = itemView.findViewById(R.id.tvApellidos)
        val edad: TextView = itemView.findViewById(R.id.tvEdad)
        val container: View = itemView.findViewById(R.id.container)

        init {
            itemView.setOnClickListener {
                // Actualizar la posición seleccionada
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition

                // Notificar cambios para actualizar ambos elementos
                if (previousSelected != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousSelected)
                }
                notifyItemChanged(selectedPosition)

                // Notificar al fragment sobre el elemento seleccionado
                desaparecidos.getOrNull(adapterPosition)?.let { desaparecido ->
                    onItemClick(desaparecido)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesaparecidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_desaparecido, parent, false)
        return DesaparecidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: DesaparecidoViewHolder, position: Int) {
        val desaparecido = desaparecidos[position]
        holder.nombre.text = desaparecido.nombre
        holder.apellidos.text = desaparecido.apellidos
        holder.edad.text = "${desaparecido.edad} años"

        // Cambiar el fondo si está seleccionado
        if (selectedPosition == position) {
            holder.container.setBackgroundColor(Color.parseColor("#E3F2FD")) // Azul claro
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount() = desaparecidos.size

    fun clearSelection() {
        val previousSelected = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        if (previousSelected != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelected)
        }
    }
}