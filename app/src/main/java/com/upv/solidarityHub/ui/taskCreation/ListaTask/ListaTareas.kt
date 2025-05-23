package com.upv.solidarityHub.ui.taskCreation.ListaTask

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.FragmentListaTareasBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI.taskDB
import com.upv.solidarityHub.persistence.database.SupabaseAPI

class ListaTareas :Fragment() {
    private var _binding: FragmentListaTareasBinding? = null
    private val binding get() = _binding!!
    private val db = SupabaseAPI()
    private var tareaSeleccionada: taskDB? = null

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

        binding.listaTareas.layoutManager = LinearLayoutManager(requireContext())

        cargarTareas()

        binding.btnVerDetalles.setOnClickListener {
            tareaSeleccionada?.let { tarea ->
                Log.d("PRUEBA", "Tarea seleccionada al hacer click boton: ${tarea.id}")
                if(tarea.id!=null){
                    val bundle = Bundle().apply {
                        putInt("idTarea", tarea.id)
                    }
                    findNavController().navigate(R.id.action_listaTareasFragment_to_DetallesTareasFragment, bundle)
                }
            } ?: mostrarError("Selecciona una tarea primero")

        }

        binding.btnEliminar.setOnClickListener {
            tareaSeleccionada?.let { tarea ->
                if(tarea.id!= null){
                    eliminarTarea(tarea.id)
                }
            } ?: mostrarError("Selecciona una tarea primero")
        }
    }

    private fun cargarTareas() {
        lifecycleScope.launch {
            val tareas = db.getAllTareas()
            val solicitudes = db.getAllSolicitudes()

            val solicitudesMap = solicitudes
                ?.filter { it.id != null }
                ?.associateBy { it.id!! }
                ?: emptyMap()

            binding.listaTareas.adapter = TareasAdapter(
                tareas = tareas ?: emptyList(),
                solicitudesMap = solicitudesMap,
                onClick = { tareaSeleccionada = it }
            )
        }
    }

    private fun eliminarTarea(idTarea: Int) {
        lifecycleScope.launch {
            val resultado = db.eliminarTarea(idTarea)
            if (resultado) {
                cargarTareas()
                mostrarMensaje("Tarea eliminada")
            } else {
                mostrarMensaje("Error al eliminar")
            }
        }
    }

    private fun mostrarMensaje(texto: String) {
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarError(texto: String) {
        Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}