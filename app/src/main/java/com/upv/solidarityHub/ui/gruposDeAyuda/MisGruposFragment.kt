package com.upv.solidarityHub.ui.gruposDeAyuda

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upv.solidarityHub.databinding.FragmentMisGruposBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.upv.solidarityHub.persistence.Usuario
import androidx.navigation.fragment.findNavController
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.GrupoDeAyuda


class MisGruposFragment : Fragment() {
    private var _binding: FragmentMisGruposBinding? = null
    private val binding get() = _binding!!
    private val db = SupabaseAPI()
    private var gruposInscritos: List<GrupoDeAyuda> = emptyList()
    private var selectedPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisGruposBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usuario = db.getLogedUser()

        // Configuración del ListView
        binding.listaMisGrupos.choiceMode = ListView.CHOICE_MODE_SINGLE

        binding.listaMisGrupos.setOnItemClickListener { _, _, position, _ ->
            selectedPosition = position
            binding.botonSalirGrupo.isEnabled = true
            binding.listaMisGrupos.setItemChecked(position, true)
            (binding.listaMisGrupos.adapter as? ArrayAdapter<*>)?.notifyDataSetChanged()
        }

        binding.botonSalirGrupo.setOnClickListener {
            if (selectedPosition != -1) {
                val grupoSeleccionado = gruposInscritos[selectedPosition]
                lifecycleScope.launch {
                    try {
                        val success = db.salirDelGrupo(usuario.correo, grupoSeleccionado.id)
                        if (success) {
                            Toast.makeText(requireContext(), "Has salido del grupo", Toast.LENGTH_SHORT).show()
                            // Limpiar selección visual
                            binding.listaMisGrupos.clearChoices()
                            cargarGrupos(usuario.correo)
                        } else {
                            Toast.makeText(requireContext(), "Error al salir del grupo", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.botonVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        if (usuario != null) {
            lifecycleScope.launch {
                cargarGrupos(usuario.correo)
            }
        } else {
            Toast.makeText(requireContext(), "Error al buscar el usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun cargarGrupos(usuario: String) {
        gruposInscritos = db.getGruposusuario(usuario) ?: emptyList()
        if (gruposInscritos.isEmpty()) {
            Toast.makeText(requireContext(), "No estás inscrito en ningún grupo", Toast.LENGTH_SHORT).show()
            binding.listaMisGrupos.adapter = null
        } else {
            val adapter = object : ArrayAdapter<GrupoDeAyuda>(
                requireContext(),
                R.layout.item_grupos_mios,
                R.id.tvNombreGrupo,
                gruposInscritos
            ) {
                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val grupo = getItem(position)
                    val textView = view.findViewById<TextView>(R.id.tvNombreGrupo)
                    textView.text = "Grupo ${grupo?.id} - ${grupo?.sesion}"

                    // Resaltar si está seleccionado
                    val isSelected = (parent as ListView).isItemChecked(position)
                    if (isSelected) {
                        view.setBackgroundResource(android.R.color.darker_gray)
                    } else {
                        view.setBackgroundResource(android.R.color.transparent)
                    }

                    return view
                }
            }
            binding.listaMisGrupos.adapter = adapter
            binding.listaMisGrupos.choiceMode = ListView.CHOICE_MODE_SINGLE
        }
        selectedPosition = -1
        binding.botonSalirGrupo.isEnabled = false
        binding.listaMisGrupos.clearChoices()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}