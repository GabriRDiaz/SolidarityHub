package com.upv.solidarityHub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CrearTareasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrearTareasFragment : Fragment(R.layout.fragment_crear_tareas) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var catSpinner : Spinner
    private lateinit var prioritySpinner : Spinner
    private lateinit var scheduleSpinner : Spinner
    private lateinit var sizeSpinner : Spinner

    lateinit var townSearcher : SearchView
    lateinit var townRecycler : RecyclerView

    lateinit var okButton : Button
    lateinit var cancelButton : Button
    lateinit var dateButton : Button
    lateinit var locationButton : Button

    val categories = arrayOf("Cualquiera","Limpieza", "Recogida de comida", "Reconstrucción", "Primeros auxilios", "Artículos para bebés", "Asistencia a mayores", "Asistencia a discapacitados", "Artículos de primera necesidad", "Otros", "Transporte", "Cocina", "Mascotas")
    val urgenciaList = arrayOf("Cualquiera","Baja", "Media", "Alta")
    val groupSize = arrayOf("Cualquiera","Pequeña (5 voluntarios máx.)", "Media (15 voluntarios máx.)", "Grande (15+ voluntarios)")
    val hours = arrayOf("Cualquiera","Manaña Temprana (6:00 - 9:00)", "Mañana (9:00 - 12:00)", "Mediodía (12:00 - 15:00)", "Tarde (15:00 - 18:00)", "Noche Temprana (18:00 - 21:00)", "Noche (21:00 - 00:00)", "Madrugada (00:00 - 6:00)")




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_crear_tareas, container, false)

        findComponents(rootView)

        setSpinners()
        return rootView

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CrearTareasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CrearTareasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun findComponents(rootView: View) {
        catSpinner = rootView.findViewById(R.id.spinnerCatTask)
        prioritySpinner = rootView.findViewById(R.id.spinnerPriorityTask)
        scheduleSpinner = rootView.findViewById(R.id.spinnerScheduleTask)
        sizeSpinner = rootView.findViewById(R.id.spinnerSizeTask)

        townSearcher = rootView.findViewById(R.id.SearchTownTask)
        townRecycler = rootView.findViewById(R.id.RecyclerViewLocations)

        okButton = rootView.findViewById(R.id.buttonAcceptTask)
        cancelButton = rootView.findViewById(R.id.buttonCancelTask)
        dateButton = rootView.findViewById(R.id.buttonDateTask)
        locationButton = rootView.findViewById(R.id.buttonLocationTask)
    }

    private fun setSpinners(){
        val adapterCat = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, categories
        )
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        catSpinner.adapter = adapterCat


        val adapterPriority = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, urgenciaList
        )
        adapterPriority.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        prioritySpinner.adapter = adapterPriority

        val adapterSize = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, groupSize
        )
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sizeSpinner.adapter = adapterSize

        val adapterSchedule = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, hours
        )
        adapterSchedule.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        scheduleSpinner.adapter = adapterSchedule

    }


}