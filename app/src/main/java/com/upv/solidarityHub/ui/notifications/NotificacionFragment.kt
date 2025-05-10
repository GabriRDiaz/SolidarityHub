package com.upv.solidarityHub.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.upv.solidarityHub.R

class NotificacionFragment : Fragment() {


    private lateinit var textCategoriaNoti: TextView
    private lateinit var municipio: TextView
    private lateinit var textHorarioNoti: TextView
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button

    private val supabaseAPI = SupabaseAPI()

    private val args by lazy {
        val taskId = arguments?.getInt("idTask", -1) ?: -1
        val asignacionId = arguments?.getInt("idAsignacion", -1) ?: -1
        Pair(taskId, asignacionId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notificacion, container, false)

        initViews(view)
        setupButtons()
        loadTaskData()

        return view
    }

    private fun initViews(view: View) {
        textCategoriaNoti = view.findViewById(R.id.textCategoriaNoti)
        municipio = view.findViewById(R.id.textMunicipioNoti)
        textHorarioNoti = view.findViewById(R.id.textHorarioNoti)
        btnAceptar = view.findViewById(R.id.botAceptarNoti)
        btnCancelar = view.findViewById(R.id.botCancelarNoti)
    }

    private fun setupButtons() {
        btnAceptar.setOnClickListener {
            Toast.makeText(requireContext(), "¡Tarea aceptada!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        btnCancelar.setOnClickListener {
            lifecycleScope.launch {
                supabaseAPI.eliminarAsignacion(args.second)
                Toast.makeText(requireContext(), "Tarea rechazada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun loadTaskData() {
        lifecycleScope.launch {
            try {
                // Obtener la tarea y su solicitud asociada
                val task = supabaseAPI.getTaskById(args.first)
                if (task == null) {
                    showError("Tarea no encontrada")
                    return@launch
                }

                val req = task.og_req?.let { supabaseAPI.getHelpReqById(it) }
                if (req == null) {
                    showError("Información de solicitud no disponible")
                    return@launch
                }

                // Actualizar UI con los datos
                with(req) {
                    textCategoriaNoti.text = req.categoria
                    municipio.text = req.ubicacion // O usa req.municipio si existe ese campo
                    textHorarioNoti.text = req.horario
                }

            } catch (e: Exception) {
                showError("Error al cargar datos: ${e.localizedMessage}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }


}