package com.upv.solidarityHub.ui.gruposDeAyuda.detallesGrupoVoluntarios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.ContentDetallesGrupoVoluntariosBinding
import com.upv.solidarityHub.databinding.FragmentDetallesGrupoVoluntariosBinding
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.launch

class DetallesGrupoVoluntariosFragment : Fragment() {

    private lateinit var _binding: FragmentDetallesGrupoVoluntariosBinding
    private val binding get() = _binding!!
    private lateinit var contentBinding: ContentDetallesGrupoVoluntariosBinding
    private val db: SupabaseAPI = SupabaseAPI()
    private var grupoId: Int = -1
    private val args: DetallesGrupoVoluntariosFragmentArgs by navArgs()
    val usuario: Usuario = SupabaseAPI().getLogedUser()

    override fun onCreateView(
        inflater : LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetallesGrupoVoluntariosBinding.inflate(inflater, container, false)
        contentBinding = ContentDetallesGrupoVoluntariosBinding.bind(binding.root.findViewById(R.id.contentDetallesGrupo))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        grupoId = args.grupoId

        cargarDatosGrupo(grupoId)
        configurarBotones()
    }

    private fun cargarDatosGrupo(grupoId: Int) {
        lifecycleScope.launch {
            try {
                val grupo = db.getGrupoById(grupoId) ?: run {
                    Toast.makeText(requireContext(), "Grupo no encontrado", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                    return@launch
                }

                with(contentBinding) {
                    textoNombreGrupo.text = "Grupo ${grupo.id}"
                    textViewUbicacion.text = grupo.ubicacion
                    textViewSesion.text = grupo.sesion
                    textViewTamanyo.text = "${grupo.tamanyo} personas"
                    textViewFecha.text = grupo.fecha_creacion
                    textViewDescripcion.text = grupo.descripcion
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar el grupo: ${e.message}", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun configurarBotones() {
        contentBinding.botonVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        contentBinding.botonUnirse.setOnClickListener {
            lifecycleScope.launch {
                if(usuario!= null){
                    val resultado = db.unirseAGrupo(usuario.correo, grupoId)
                    if (resultado) {
                        Toast.makeText(
                            requireContext(), "Te has unido al grupo $grupoId", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al unirte al grupo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

}