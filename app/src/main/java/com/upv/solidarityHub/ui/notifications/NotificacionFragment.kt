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
    private lateinit var textMunicipioNoti: TextView
    private lateinit var textHorarioNoti: TextView
    private lateinit var textTituloNoti: TextView
    private lateinit var textDescripcionNoti: TextView
    private lateinit var btnAceptar: Button
    private lateinit var btnCancelar: Button
    private var asignacionId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notificacion, container, false)

        // Inicializar vistas
        textCategoriaNoti = view.findViewById(R.id.textCategoriaNoti)
        textMunicipioNoti = view.findViewById(R.id.textMunicipioNoti)
        textHorarioNoti = view.findViewById(R.id.textHorarioNoti)
        textTituloNoti = view.findViewById(R.id.textTituloNoti)
        textDescripcionNoti = view.findViewById(R.id.textDescripcionNoti)
        btnAceptar = view.findViewById(R.id.botAceptarNoti)
        btnCancelar = view.findViewById(R.id.botCancelarNoti)

        // Obtener datos del bundle
        val categoria = arguments?.getString("categoria") ?: ""
        val municipio = arguments?.getString("municipio") ?: ""
        val horario = arguments?.getString("horario") ?: ""
        val titulo = arguments?.getString("titulo")
        val descripcion = arguments?.getString("descripcion")
        asignacionId = arguments?.getInt("asignacionId") ?: -1

        // Mostrar datos
        textCategoriaNoti.text = categoria
        textMunicipioNoti.text = municipio
        textHorarioNoti.text = horario
        textTituloNoti.text = titulo
        textDescripcionNoti.text = descripcion

        // Configurar botones
        btnAceptar.setOnClickListener {
            aceptarTarea()
        }

        btnCancelar.setOnClickListener {
            rechazarTarea()
        }

        return view
    }

    private fun aceptarTarea() {
        lifecycleScope.launch {
            val success = SupabaseAPI().aceptarTarea(asignacionId)
            if (success) {
                Toast.makeText(requireContext(), "¡Tarea aceptada con éxito!", Toast.LENGTH_SHORT).show()
                // Actualizar lista de notificaciones
                findNavController().previousBackStackEntry?.savedStateHandle?.set("tareaAceptada", true)
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al aceptar la tarea", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rechazarTarea() {
        lifecycleScope.launch {
            val success = SupabaseAPI().eliminarAsignacion(asignacionId)
            if (success) {
                Toast.makeText(requireContext(), "Tarea rechazada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } else {
                Toast.makeText(requireContext(), "Error al rechazar la tarea", Toast.LENGTH_SHORT).show()
            }
        }
    }


}