package com.upv.solidarityHub.ui.taskCreation.ListaTask

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.database.SupabaseAPI.taskDB
import com.upv.solidarityHub.persistence.database.SupabaseAPI.reqDB

import java.text.SimpleDateFormat
import java.util.Locale

class TareasAdapter(
    private val tareas: List<taskDB>,
    private val solicitudesMap: Map<Int, reqDB>,
    private val onClick: (taskDB) -> Unit
) : RecyclerView.Adapter<TareasAdapter.TareaViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitulo: TextView = view.findViewById(R.id.tvTituloSolicitud)
        private val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        private val tvFechas: TextView = view.findViewById(R.id.tvFechas)
        private val tvHoras: TextView = view.findViewById(R.id.tvHoras)

        fun bind(tarea: taskDB, isSelected: Boolean) {
            val solicitud = solicitudesMap[tarea.og_req]
            tvTitulo.text = solicitud?.titulo ?: "Sin título"
            tvCategoria.text = solicitud?.categoria ?: "Sin categoría"

            tvFechas.text = "${tarea.fecha_inicial} - ${tarea.fecha_final}"
            tvHoras.text = tarea.hora_inicio

            itemView.isSelected = isSelected

            itemView.setOnClickListener {
                Log.d("PRUEBA", "Tarea seleccionada: ${tarea.id}")
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onClick(tarea)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tareas, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        holder.bind(tareas[position], position == selectedPosition)
        holder.itemView.isActivated = (position == selectedPosition)
    }

    override fun getItemCount() = tareas.size
}