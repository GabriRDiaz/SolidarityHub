package com.upv.solidarityHub.ui.taskCreation.ListaTask

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.FragmentListaTareasBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI.taskDB
import com.upv.solidarityHub.persistence.database.SupabaseAPI

class ListaTareas :Fragment() {
    private var _binding: FragmentListaTareasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TareasListViewModel by viewModels {
        TareasListViewModelFactory(SupabaseAPI())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaTareasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        viewModel.cargarTareas()
    }

    private fun setupRecyclerView() {
        binding.listaTareas.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar cambios en la lista de tareas
                viewModel.tareas.observe(viewLifecycleOwner) { tareas ->
                    cargarSolicitudesYActualizarAdapter(tareas)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnVerDetalles.setOnClickListener {
            viewModel.tareaSeleccionada.value?.let { tarea ->
                if (tarea.id != null) {
                    viewModel.seleccionarTarea(tarea)
                    navegarADetalles(tarea)
                }
            } ?: mostrarError("Selecciona una tarea primero")
        }

        binding.btnEliminar.setOnClickListener {
            viewModel.tareaSeleccionada.value?.let { tarea ->
                tarea.id?.let { id ->
                    viewModel.eliminarTarea(id)
                }
            } ?: mostrarError("Selecciona una tarea primero")
        }
    }

    private fun cargarSolicitudesYActualizarAdapter(tareas: List<taskDB>) {
        lifecycleScope.launch {
            val solicitudes = SupabaseAPI().getAllSolicitudes()
            val solicitudesMap = solicitudes
                ?.filter { it.id != null }
                ?.associateBy { it.id!! }
                ?: emptyMap()

            binding.listaTareas.adapter = TareasAdapter(
                tareas = tareas,
                solicitudesMap = solicitudesMap,
                onClick = { tarea ->
                    viewModel.seleccionarTarea(tarea)
                }
            )
        }
    }

    private fun navegarADetalles(tarea: taskDB) {
        val bundle = Bundle().apply {
            putInt("idTarea", tarea.id!!)
        }
        findNavController().navigate(
            R.id.action_listaTareasFragment_to_DetallesTareasFragment,
            bundle
        )
    }

    private fun mostrarError(texto: String) {
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}