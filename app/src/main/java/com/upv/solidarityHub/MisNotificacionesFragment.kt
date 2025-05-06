package com.upv.solidarityHub

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.taskReq
import com.upv.solidarityHub.persistence.tieneAsignado
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [MisNotificacionesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MisNotificacionesFragment : Fragment() {
    private lateinit var listaNotis: ListView
    private lateinit var botonVolver: Button
    private lateinit var botonVerNoti: Button

    private var notificaciones: List<Pair<tieneAsignado, SupabaseAPI.taskDB>> = listOf()
    private var selectedPos = -1
    private val supabaseAPI = SupabaseAPI()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mis_notificaciones, container, false)

        listaNotis = view.findViewById(R.id.listaNotis)
        botonVolver = view.findViewById(R.id.botonVolverNotis)
        botonVerNoti = view.findViewById(R.id.botonVerNoti)

        val sharedPref = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("user_email", null)
        if (userId == null) {
            // No hay usuario logeado, manejar este caso
            findNavController().navigate(R.id.nav_gallery)
            return view
        }

        lifecycleScope.launch {
            val asignaciones = supabaseAPI.getAsignacionesUsuario(userId) ?: emptyList()
            val tareas = asignaciones.mapNotNull {
                val task = supabaseAPI.getTaskById(it.id_task)
                val req = task?.og_req?.let { reqId -> supabaseAPI.getHelpReqById(reqId) }
                if (task != null && req != null) Triple(it, task, req) else null
            }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_single_choice,
                tareas.map { "Tarea: ${it.third.titulo} en ${it.third.ubicacion}" } // Usamos ubicacion en lugar de municipio
            )
            listaNotis.choiceMode = ListView.CHOICE_MODE_SINGLE
            listaNotis.adapter = adapter
        }

        listaNotis.setOnItemClickListener { _, _, position, _ ->
            selectedPos = position
        }

        botonVerNoti.setOnClickListener {
            if (selectedPos >= 0) {
                val (asignacion, tarea) = notificaciones[selectedPos]

                // Navegar pasando los parámetros
                findNavController().navigate(
                    R.id.action_misNotisFragment_to_notiFragment,
                    bundleOf(
                        "taskId" to tarea.id,
                        "asignacionId" to asignacion.id
                    )
                )
            } else {
                Toast.makeText(requireContext(), "Selecciona una notificación", Toast.LENGTH_SHORT).show()
            }
        }

        botonVolver.setOnClickListener {
            findNavController().navigateUp()
        }

        return view
    }
}