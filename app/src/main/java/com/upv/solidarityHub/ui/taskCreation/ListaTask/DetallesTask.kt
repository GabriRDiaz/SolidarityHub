package com.upv.solidarityHub.ui.taskCreation.ListaTask

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.upv.solidarityHub.R
import androidx.navigation.fragment.findNavController
import com.upv.solidarityHub.databinding.FragmentDetallesTaskBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope


class DetallesTask : Fragment() {

    private var _binding: FragmentDetallesTaskBinding? = null
    private val binding get() = _binding!!
    private val db = SupabaseAPI()
    private var idTarea: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idTarea = it.getInt("idTarea")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallesTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DETALLES", "Argumentos recibidos: ${arguments?.getInt("idTarea")}")

        cargarDetallesTarea()

        binding.btnVolver.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun cargarDetallesTarea() {
        lifecycleScope.launch {
            idTarea?.let { id ->
                val tarea = db.getTaskById(id)
                val solicitud = db.getHelpReqById(tarea?.og_req ?: return@launch)

                tarea?.let {
                    with(binding) {
                        tvTitulo.text = solicitud?.titulo ?: "Sin título"
                        tvDescripcion.text = solicitud?.descripcion ?: "Sin descripción"
                        tvCategoria.text = solicitud?.categoria ?: "Sin categoría"
                        tvUrgencia.text = solicitud?.urgencia ?: "Urgencia no especificada"
                        tvUbicacion.text = solicitud?.ubicacion ?: "Ubicación no especificada"
                        tvFechas.text = "${tarea.fecha_inicial} - ${tarea.fecha_final}"
                        tvHorario.text = "Hora: ${tarea.hora_inicio}"
                    }
                } ?: run {

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(idTarea: Int) = DetallesTask().apply {
            arguments = Bundle().apply {
                putInt("idTarea", idTarea)
            }
        }
    }

}