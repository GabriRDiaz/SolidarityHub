package com.upv.solidarityHub.ui.gruposDeAyuda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.GrupoDeAyuda

class GruposAdapter(
    private val grupos: List<GrupoDeAyuda>,
    private val onClick: (GrupoDeAyuda) -> Unit
) : RecyclerView.Adapter<GruposAdapter.GrupoViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class GrupoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nombreGrupo = view.findViewById<TextView>(R.id.tvNombreGrupo)
        private val municipioGrupo = view.findViewById<TextView>(R.id.tvMunicipioGrupo)

        fun bind(grupo: GrupoDeAyuda, isSelected: Boolean) {
            nombreGrupo.text = "Grupo ${grupo.id} - ${grupo.sesion}"
            municipioGrupo.text = grupo.ubicacion ?: "Municipio no disponible"
            itemView.setBackgroundResource(if (isSelected) android.R.color.darker_gray else android.R.color.transparent)

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onClick(grupo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GrupoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grupo_ayuda, parent, false)
        return GrupoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GrupoViewHolder, position: Int) {
        holder.bind(grupos[position], position == selectedPosition)
    }

    override fun getItemCount() = grupos.size
}