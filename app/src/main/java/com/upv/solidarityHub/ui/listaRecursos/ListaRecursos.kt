package com.upv.solidarityHub.ui.listaRecursos

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.upv.solidarityHub.databinding.FragmentListaRecursosBinding
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ListaRecursos : Fragment() {
    private var _binding: FragmentListaRecursosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListaRecursosViewModel by viewModels()
    private lateinit var adapter: RecursosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaRecursosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupButtons()
    }

    private fun setupRecyclerView() {
        adapter = RecursosAdapter(emptyList()) { baliza ->
            viewModel.seleccionarBaliza(baliza)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ListaRecursos.adapter
        }
    }

    private fun setupObservers() {
        // Observar balizas
        viewModel.balizas.onEach { balizas ->
            adapter = RecursosAdapter(balizas) { baliza ->
                viewModel.seleccionarBaliza(baliza)
            }
            binding.recyclerView.adapter = adapter
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        // Observar selección y estado de carga
        combine(
            viewModel.selectedBaliza,
            viewModel.isLoading
        ) { selected, isLoading ->
            Pair(selected, isLoading)
        }.onEach { (selected, isLoading) ->
            binding.btnEliminar.isEnabled = selected != null
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            if (selected == null) {
                adapter.clearSelection()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupButtons() {
        binding.btnEliminar.setOnClickListener {
            viewModel.eliminarBalizaSeleccionada()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}