package com.upv.solidarityHub.ui.listaDesaparecidos

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.upv.solidarityHub.databinding.FragmentListaDesaparecidosBinding

class ListaDesaparecidos : Fragment() {
    private var _binding: FragmentListaDesaparecidosBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ListaDesaparecidosViewModel by viewModels()
    private lateinit var adapter: DesaparecidoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaDesaparecidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupButtons()
    }

    private fun setupRecyclerView() {
        adapter = DesaparecidoAdapter(emptyList()) { desaparecido ->
            viewModel.seleccionarDesaparecido(desaparecido)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ListaDesaparecidos.adapter
        }
    }

    private fun setupObservers() {
        // Observar desaparecidos
        viewModel.desaparecidos.onEach { desaparecidos ->
            adapter = DesaparecidoAdapter(desaparecidos) { desaparecido ->
                viewModel.seleccionarDesaparecido(desaparecido)
            }
            binding.recyclerView.adapter = adapter
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        // Observar selección y estado de carga
        combine(
            viewModel.selectedDesaparecido,
            viewModel.isLoading
        ) { selected, isLoading ->
            Pair(selected, isLoading)
        }.onEach { (selected, isLoading) ->
            // Actualizar visibilidad del botón
            binding.btnEliminar.isEnabled = selected != null

            // Actualizar estado de carga
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            // Limpiar selección si se eliminó
            if (selected == null) {
                adapter.clearSelection()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupButtons() {
        binding.btnEliminar.setOnClickListener {
            viewModel.eliminarDesaparecidoSeleccionado()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}