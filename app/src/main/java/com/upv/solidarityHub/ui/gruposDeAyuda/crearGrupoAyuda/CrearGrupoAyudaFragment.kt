package com.upv.solidarityHub.ui.gruposDeAyuda.crearGrupoAyuda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.ContentCrearGrupoAyudaBinding
import com.upv.solidarityHub.databinding.FragmentCrearGrupoAyudaBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.ui.gruposDeAyuda.GruposViewModel
import kotlinx.coroutines.launch
import java.util.Date

class CrearGrupoAyudaFragment : Fragment() {
    private var _binding: FragmentCrearGrupoAyudaBinding? = null
    private val binding get() = _binding!!
    private lateinit var contentBinding: ContentCrearGrupoAyudaBinding
    private val db: SupabaseAPI = SupabaseAPI()
    private val viewModel: GruposViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearGrupoAyudaBinding.inflate(inflater, container, false)
        contentBinding = ContentCrearGrupoAyudaBinding.bind(binding.root.findViewById(R.id.contentCrearGrupo))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val opcionesHorario = listOf(
            "Mañana Temprana (6:00 - 9:00)",
            "Mañana (9:00 - 12:00)",
            "Mediodía (12:00 - 15:00)",
            "Tarde (15:00 - 18:00)",
            "Noche Temprana (18:00 - 21:00)",
            "Noche (21:00 - 00:00)",
            "Madrugada (00:00 - 6:00)"
        )

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, opcionesHorario)
        contentBinding.dropdownHorario.setAdapter(adapter)

        contentBinding.botonAceptar.setOnClickListener {
            val descripcion = contentBinding.textoDescripcion.text?.toString()?.trim() ?: ""
            val ubicacion = contentBinding.textoMunicipio.text?.toString()?.trim() ?: ""
            val sesion = contentBinding.dropdownHorario.text?.toString()?.trim() ?: ""
            val tamanyo = 1

            if (descripcion.isEmpty() || ubicacion.isEmpty() || sesion.isEmpty() || sesion !in opcionesHorario) {
                Toast.makeText(requireContext(), "Rellena todos los campos con opciones validas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val success = db.registrarGrupo(
                        descripcion = descripcion,
                        ubicacion = ubicacion,
                        fecha_creacion = Date().toString(),
                        sesion = sesion,
                        tamanyo = tamanyo
                    )

                    if (success) {
                        Toast.makeText(requireContext(), "Grupo creado correctamente", Toast.LENGTH_LONG).show()
                        // Notificar al ViewModel que se creó un nuevo grupo
                        viewModel.setNeedsRefresh(true)
                        // Volver al fragmento anterior
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Error al crear el grupo", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        contentBinding.botonCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}