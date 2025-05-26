package com.upv.solidarityHub.ui.createHelpReq

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.launch

class MyHelpReqsFragment : Fragment() {

    private val viewModel: MyHelpReqsViewModel by viewModels()

    private lateinit var reqRecycler: RecyclerView
    private lateinit var adapter: HelpReqAdapter

    public lateinit var buttonDelete: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_mis_solicitudes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findComponents(view)
        setRecyclerView(view)
        setListeners()

        lifecycleScope.launch {
            val helpRequestsWithStatus = viewModel.loadData()
            adapter.updateData(helpRequestsWithStatus)
        }
    }

    private fun setListeners() {
        buttonDelete.setOnClickListener {
            showDeletePopup()
        }
    }


    private fun findComponents(view: View) {
        buttonDelete = view.findViewById(R.id.buttonEliminarReq)
        buttonDelete.isEnabled = false
    }

    fun showDeletePopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Estás seguro?")
            .setMessage("La solicitud seleccionada será eliminada")
            .setPositiveButton("Si") { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch {
                    viewModel.deleteSelectedReq()
                    val updatedRequests = viewModel.loadData()
                    adapter.updateData(updatedRequests)
                    buttonDelete.isEnabled = false
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun setRecyclerView(view: View) {
        reqRecycler = view.findViewById(R.id.reqsRecycler)
        reqRecycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = HelpReqAdapter(requireContext(), emptyList(), viewModel)
        adapter.onItemClick = { helpRequest, isAlreadyATask ->
            buttonDelete.isEnabled = !isAlreadyATask
        }
        reqRecycler.adapter = adapter
    }

    class HelpReqAdapter(
        private val context: Context,
        private var helpRequests: List<Pair<SupabaseAPI.reqDB, Boolean>>,
        private val viewModel: MyHelpReqsViewModel
    ) : RecyclerView.Adapter<HelpReqAdapter.HelpReqViewHolder>() {

        private var selectedPosition = RecyclerView.NO_POSITION
        var onItemClick: ((SupabaseAPI.reqDB, Boolean) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HelpReqViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.help_reqs_layout, parent, false)
            return HelpReqViewHolder(view)
        }

        override fun getItemCount(): Int = helpRequests.size

        override fun onBindViewHolder(holder: HelpReqViewHolder, position: Int) {
            val (currentItem, isAlreadyATask) = helpRequests[position]
            holder.bind(currentItem, isAlreadyATask)
        }


        fun updateData(newHelpRequests: List<Pair<SupabaseAPI.reqDB, Boolean>>) {
            helpRequests = newHelpRequests
            selectedPosition = RecyclerView.NO_POSITION
            notifyDataSetChanged()
        }

        inner class HelpReqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val title: TextView = itemView.findViewById(R.id.helpReqTitleText)
            private val town: TextView = itemView.findViewById(R.id.helpReqLocationText)
            private val category: TextView = itemView.findViewById(R.id.helpReqCatText)

            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val previousSelected = selectedPosition
                        selectedPosition = position

                        notifyItemChanged(previousSelected)
                        notifyItemChanged(selectedPosition)

                        val (req, isTask) = helpRequests[position]
                        viewModel.setSelectedRequest(req, isTask)
                        onItemClick?.invoke(req, isTask)
                    }
                }
            }

            fun bind(helpReq: SupabaseAPI.reqDB, isAlreadyATask: Boolean) {
                title.text = helpReq.titulo
                town.text = helpReq.ubicacion
                category.text = helpReq.categoria

                val textColor = if (isAlreadyATask) {
                    ContextCompat.getColor(context, android.R.color.darker_gray)
                } else {
                    ContextCompat.getColor(context, android.R.color.black)
                }
                title.setTextColor(textColor)
                town.setTextColor(textColor)
                category.setTextColor(textColor)

                val backgroundColor = when {
                    isAlreadyATask ->  ContextCompat.getColor(context, android.R.color.background_light)
                    adapterPosition == selectedPosition -> ContextCompat.getColor(context, R.color.purple_200)
                    else -> ContextCompat.getColor(context, R.color.white)
                }

                itemView.setBackgroundColor(backgroundColor)
            }
        }
    }
}
