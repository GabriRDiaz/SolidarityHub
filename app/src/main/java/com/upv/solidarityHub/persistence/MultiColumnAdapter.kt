package com.upv.solidarityHub.persistence

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultiColumnAdapter(
    private val users: List<Usuario>,
    private val columnsCount: Int,
    private val taskCat: String
) : RecyclerView.Adapter<MultiColumnAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val col1: TextView = view.findViewById(R.id.ColumnsName)
        val col2: TextView = view.findViewById(R.id.ColumnsTown)
        val col3: TextView? = try {
            view.findViewById(R.id.ColumnsAbility)
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = if (columnsCount == 3) R.layout.item_three_columns else R.layout.item_two_columns
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.col1.text = user.nombre
        holder.col2.text = user.municipio

        if (columnsCount == 3 && holder.col3 != null) {
            holder.col3.text = "Comprobando..."

            CoroutineScope(Dispatchers.IO).launch {
                val usersWithAbility: List<String>? = SupabaseAPI().getUsersWithAbility(taskCat)

                withContext(Dispatchers.Main) {
                    if (usersWithAbility != null) {
                        holder.col3.text = if (user.correo in usersWithAbility) taskCat else "N/A"
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = users.size
}