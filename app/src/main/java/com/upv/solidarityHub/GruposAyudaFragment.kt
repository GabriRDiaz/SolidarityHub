package com.upv.solidarityHub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.upv.solidarityHub.databinding.FragmentGruposAyuda2Binding
import com.upv.solidarityHub.databinding.ContentGruposAyuda2Binding
import com.upv.solidarityHub.persistence.GrupoDeAyuda
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController


class GruposAyudaFragment : Fragment() {
    private var _binding: FragmentGruposAyuda2Binding? = null
    private val binding get() = _binding!!

    private lateinit var contentBinding: ContentGruposAyuda2Binding

    private val db: SupabaseAPI = SupabaseAPI()
    private var grupoSeleccionado: GrupoDeAyuda? = null
    private lateinit var usuario: Usuario

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGruposAyuda2Binding.inflate(inflater, container, false)
        // Aquí se accede al layout incluido
        contentBinding = ContentGruposAyuda2Binding.bind(binding.root.findViewById(R.id.contentGrupoAyuda))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuario = requireActivity().intent.getParcelableExtra("usuario")!!

        obtenerGrupos()

        contentBinding.botonVerDetalles2.setOnClickListener {
            grupoSeleccionado?.let {
                val intent = Intent(requireContext(), DetallesGrupoVoluntarios::class.java)
                intent.putExtra("grupoId", it.id)
                startActivity(intent)
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
            val bundle = Bundle()
            //bundle.putParcelable("usuario", usuario)
            findNavController().navigate(R.id.action_gruposAyudaFragment_to_misGruposFragment, bundle)
        }

        contentBinding.listaGruposAyuda.setOnItemClickListener { _, _, position, _ ->
            val grupo = contentBinding.listaGruposAyuda.adapter.getItem(position) as GrupoDeAyuda
            val id = grupo.id

            if (id != null) {
                lifecycleScope.launch {
                    grupoSeleccionado = db.getGrupoById(id)
                    contentBinding.listaGruposAyuda.setItemChecked(position, true)

                    // Actualiza la vista de los items
                    (contentBinding.listaGruposAyuda.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                }
            }
        }

        contentBinding.botonFiltrar.setOnClickListener {
            val municipioSeleccionado = contentBinding.spinnerMunicipio.selectedItem as String
            obtenerGrupos(municipioSeleccionado)
        }
    }

    private fun obtenerGrupos(municipioFiltro: String? = null) {
        lifecycleScope.launch {
            val grupos = db.getAllGrupos()
            if (grupos.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No hay grupos disponibles", Toast.LENGTH_SHORT).show()
            } else {
                val gruposFiltrados = if (municipioFiltro != null && municipioFiltro != "Todos") {
                    grupos.filter { it.ubicacion == municipioFiltro }
                } else {
                    grupos
                }

                val municipiosUnicos = grupos.mapNotNull { it.ubicacion }.distinct().sorted()
                val listaMunicipios = listOf("Todos") + municipiosUnicos
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaMunicipios)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                contentBinding.spinnerMunicipio.adapter = spinnerAdapter

                val adapter = object : ArrayAdapter<GrupoDeAyuda>(
                    requireContext(),
                    R.layout.item_grupo_ayuda,
                    gruposFiltrados
                ) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val grupo = getItem(position)
                        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_grupo_ayuda, parent, false)

                        val nombreGrupo = view.findViewById<TextView>(R.id.tvNombreGrupo)
                        val municipioGrupo = view.findViewById<TextView>(R.id.tvMunicipioGrupo)

                        nombreGrupo.text = "Grupo ${grupo?.id} - ${grupo?.sesion}"
                        municipioGrupo.text = grupo?.ubicacion ?: "Municipio no disponible"

                        val isSelected = (parent as ListView).isItemChecked(position)
                        if (isSelected) {
                            view.setBackgroundResource(android.R.color.darker_gray)
                        } else {
                            view.setBackgroundResource(android.R.color.transparent)
                        }

                        return view
                    }
                }

                contentBinding.listaGruposAyuda.choiceMode = ListView.CHOICE_MODE_SINGLE
                contentBinding.listaGruposAyuda.adapter = adapter
            }
        }
    }

    private fun mostrarGruposInscritos() {
        lifecycleScope.launch {
            val usuarioId = usuario.correo
            if (usuarioId != null) {
                val gruposInscritos = db.getGruposusuario(usuarioId)
                if (gruposInscritos.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No estás inscrito en ningún grupo", Toast.LENGTH_SHORT).show()
                } else {
                    val nombresGrupos = gruposInscritos.map { "Grupo ${it.id} - ${it.sesion}" }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_activated_1,
                        nombresGrupos
                    )
                    contentBinding.listaGruposAyuda.choiceMode = android.widget.ListView.CHOICE_MODE_SINGLE
                    contentBinding.listaGruposAyuda.adapter = adapter
                }
            } else {
                Toast.makeText(requireContext(), "No hay usuario logueado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}