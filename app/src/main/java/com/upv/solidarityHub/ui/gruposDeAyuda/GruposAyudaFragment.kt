package com.upv.solidarityHub.ui.gruposDeAyuda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.upv.solidarityHub.databinding.FragmentGruposAyuda2Binding
import com.upv.solidarityHub.databinding.ContentGruposAyuda2Binding
import com.upv.solidarityHub.persistence.GrupoDeAyuda
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upv.solidarityHub.ui.gruposDeAyuda.detallesGrupoVoluntarios.DetallesGrupoVoluntarios
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.ContentDetallesGrupoVoluntariosBinding
import com.upv.solidarityHub.ui.gruposDeAyuda.crearGrupoAyuda.CrearGrupoAyuda


class GruposAyudaFragment : Fragment() {
    private var _binding: FragmentGruposAyuda2Binding? = null
    private val binding get() = _binding!!
    private lateinit var contentBinding: ContentGruposAyuda2Binding

    private val db: SupabaseAPI = SupabaseAPI()
    private lateinit var usuario: Usuario
    private var grupoSeleccionado: GrupoDeAyuda? = null
    private var gruposTotales: List<GrupoDeAyuda> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGruposAyuda2Binding.inflate(inflater, container, false)
        contentBinding = ContentGruposAyuda2Binding.bind(binding.root.findViewById(R.id.contentGrupoAyuda))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuario = db.getLogedUser()
        contentBinding.listaGruposAyuda.layoutManager = LinearLayoutManager(requireContext())

        contentBinding.botonVerDetalles2.setOnClickListener {
            grupoSeleccionado?.let {
                val action = GruposAyudaFragmentDirections.actionGruposAyudaFragmentToDetallesGruposFragment(it.id)
                findNavController().navigate(action)
            } ?: Toast.makeText(requireContext(), "Selecciona un grupo primero", Toast.LENGTH_SHORT).show()
        }

        contentBinding.botonUnirse2.setOnClickListener {
            grupoSeleccionado?.let {
                lifecycleScope.launch {
                    val resultado = db.unirseAGrupo(usuario.correo, it.id)
                    if (resultado) {
                        Toast.makeText(requireContext(), "Te has unido al grupo ${it.id}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al unirte al grupo", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: Toast.makeText(requireContext(), "Selecciona un grupo primero", Toast.LENGTH_SHORT).show()
        }

        contentBinding.botonCrearGrupo.setOnClickListener {
            val intent = Intent(requireContext(), CrearGrupoAyuda::class.java)
            startActivity(intent)
        }

        contentBinding.botonVerGrupos.setOnClickListener {
            findNavController().navigate(R.id.action_gruposAyudaFragment_to_misGruposFragment)
        }

        contentBinding.botonFiltrar.setOnClickListener {
            val municipioSeleccionado = contentBinding.spinnerMunicipio.text.toString()
            filtrarGrupos(municipioSeleccionado)
        }

        obtenerGrupos()
    }

    private fun obtenerGrupos() {
        lifecycleScope.launch {
            gruposTotales = db.getAllGrupos() ?: emptyList()
            if (gruposTotales.isEmpty()) {
                Toast.makeText(requireContext(), "No hay grupos disponibles", Toast.LENGTH_SHORT).show()
            }
            val municipios = gruposTotales.mapNotNull { it.ubicacion }.distinct().sorted()
            val listaMunicipios = listOf("Todos") + municipios
            val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listaMunicipios)
            contentBinding.spinnerMunicipio.setAdapter(spinnerAdapter)

            actualizarRecycler(gruposTotales)
        }
    }

    private fun filtrarGrupos(municipio: String) {
        val filtrados = if (municipio == "Todos" || municipio.isBlank()) {
            gruposTotales
        } else {
            gruposTotales.filter { it.ubicacion == municipio }
        }
        actualizarRecycler(filtrados)
    }

    private fun actualizarRecycler(lista: List<GrupoDeAyuda>) {
        val adapter = GruposAdapter(lista) {
            grupoSeleccionado = it
        }
        contentBinding.listaGruposAyuda.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}