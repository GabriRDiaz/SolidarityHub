package com.upv.solidarityHub

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var botonVer: Button
    private lateinit var botonVolver: Button
    private val supabaseAPI = SupabaseAPI()
    //private var notificaciones: List<Triple<Any, Any, Any>> = emptyList() // Triple<Asignación, Tarea, Req>
    private var notificaciones: List<Triple<tieneAsignado, SupabaseAPI.taskDB, SupabaseAPI.reqDB>> = emptyList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_mis_notificaciones, container, false)

        listaNotis = rootView.findViewById(R.id.listaNotis)
        botonVolver = rootView.findViewById(R.id.botonVolverNotis)
        botonVer = rootView.findViewById(R.id.botonVerNoti)

        // Recuperar los datos desde SharedPreferences
        val sharedPref = requireActivity().getSharedPreferences("usuario", AppCompatActivity.MODE_PRIVATE)
        val correo = sharedPref.getString("usuarioCorreo", null)  // Devuelve null si no existe
        val nombre = sharedPref.getString("usuarioNombre", null)

        botonVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            try {
                if(correo!= null){
                    notificaciones = cargarNotificaciones(correo)
                    crearAdaptadorNotificaciones(notificaciones)
                }else{
                    Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error cargando notificaciones", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        botonVer.setOnClickListener {
            val selectedPosition = listaNotis.checkedItemPosition
            if (selectedPosition != ListView.INVALID_POSITION) {
                val (asignacion, tarea, req) = notificaciones[selectedPosition]
                val bundle = bundleOf(
                    "taskId" to tarea.id,
                    "asignacionId" to asignacion.id,
                    "categoria" to req.categoria,
                    "municipio" to req.ubicacion,
                    "horario" to req.horario
                )
                findNavController().navigate(R.id.action_misNotisFragment_to_notiFragment, bundle)
            } else {
                Toast.makeText(requireContext(), "Selecciona una notificación", Toast.LENGTH_SHORT).show()
            }
        }

        return rootView
    }

    private suspend fun cargarNotificaciones(correo: String): List<Triple<tieneAsignado, SupabaseAPI.taskDB, SupabaseAPI.reqDB>> {
        val asignaciones = supabaseAPI.getAsignacionesUsuario(correo) ?: emptyList()
        return asignaciones.mapNotNull { asignacion ->
            val tarea = supabaseAPI.getTaskById(asignacion.id_task)
            val req = tarea?.og_req?.let { reqId -> supabaseAPI.getHelpReqById(reqId) }
            if (tarea != null && req != null) Triple(asignacion, tarea, req) else null
        }
    }

    private fun crearAdaptadorNotificaciones(notificaciones: List<Triple<tieneAsignado, SupabaseAPI.taskDB, SupabaseAPI.reqDB>>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_single_choice,
            notificaciones.map { (asignacion, tarea, req) ->
                "Encajas perfectamente en la tarea ${tarea.id} - ${req.titulo} [${asignacion.estado?.uppercase() ?: "PENDIENTE"}]"
            }
        )
        listaNotis.adapter = adapter
    }
}