package com.upv.solidarityHub.ui.taskCreation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.taskReq
import com.upv.solidarityHub.ui.registrarDesaparecido.RegistrarDesaparecidoViewModel
import com.upv.solidarityHub.utils.SuggestionAdapter
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [CrearTareasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CrearTareasFragment : Fragment(R.layout.fragment_crear_tareas) {
    private lateinit var viewModel: CrearTareasViewModel

    private lateinit var catSpinner : Spinner
    private lateinit var prioritySpinner : Spinner
    private lateinit var scheduleSpinner : Spinner
    private lateinit var sizeSpinner : Spinner

    lateinit var townSearcher : SearchView
    lateinit var townRecycler : RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter

    lateinit var okButton : Button
    lateinit var cancelButton : Button
    lateinit var dateButton : Button
    lateinit var locationButton : Button

    private var towns: Array<String?> = emptyArray()
    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)

    lateinit var coord: String

    val categories = arrayOf("Cualquiera","Limpieza", "Recogida de comida", "Reconstrucción", "Primeros auxilios", "Artículos para bebés", "Asistencia a mayores", "Asistencia a discapacitados", "Artículos de primera necesidad", "Otros", "Transporte", "Cocina", "Mascotas")
    val urgenciaList = arrayOf("Cualquiera","Baja", "Media", "Alta")
    val groupSize = arrayOf("Cualquiera","Pequeña (5 voluntarios máx.)", "Media (15 voluntarios máx.)", "Grande (15+ voluntarios)")
    val hours = arrayOf("Cualquiera","Manaña Temprana (6:00 - 9:00)", "Mañana (9:00 - 12:00)", "Mediodía (12:00 - 15:00)", "Tarde (15:00 - 18:00)", "Noche Temprana (18:00 - 21:00)", "Noche (21:00 - 00:00)", "Madrugada (00:00 - 6:00)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_crear_tareas, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(CrearTareasViewModel::class.java)

        findComponents(rootView)

        setListeners()

        setSpinners()

        setLocationUI()

        okButton.isEnabled = false

        resetSpinners()

        lifecycleScope.launchWhenStarted {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is CrearTareasViewModel.UiEvent.ShowToast -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return rootView

    }

    companion object {
    }

    private fun findComponents(rootView: View) {
        catSpinner = rootView.findViewById(R.id.spinnerCatTask)
        prioritySpinner = rootView.findViewById(R.id.spinnerPriorityTask)
        scheduleSpinner = rootView.findViewById(R.id.spinnerScheduleTask)
        sizeSpinner = rootView.findViewById(R.id.spinnerSizeTask)

        townSearcher = rootView.findViewById(R.id.SearchTownTask)
        townRecycler = rootView.findViewById(R.id.TownRecyclerTask)

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

    private fun setLocationUI(){
        townSearcher.setOnKeyListener { v, keyCode, event ->
            viewModel.updateTown(townSearcher.query.toString(), towns)
            buttonConditions()
            false
        }

        townSearcher.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.updateTown(townSearcher.query.toString(), towns)
                buttonConditions()
            }
        }


        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            townSearcher.setQuery(suggestion, true)
            viewModel.updateTown(townSearcher.query.toString(), towns)
            buttonConditions()
            suggestionAdapter.updateSuggestions(emptyList())
        }

        townRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = suggestionAdapter
        }

        try {
            val context = requireContext()
            towns = context.assets.open("municipios").use { inputStream ->
                FileReader.readMunicipiosToArray(inputStream)
            }
            searchSuggestions = towns
        } catch (e: IOException) {
            Log.e("SolAyuda", "Error loading towns", e)
            towns = emptyArray()
            searchSuggestions = emptyArray()
        }

        townSearcher.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.updateTown(newText.orEmpty(), towns)
                val filtered = searchSuggestions.filter {
                    it?.contains(newText.orEmpty(), ignoreCase = true) ?: false
                }
                suggestionAdapter.updateSuggestions(filtered)

                buttonConditions()

                return true
            }
        })
    }

    private fun setListeners() {
        dateButton.setOnClickListener {
            openDatePicker()
        }

        locationButton.setOnClickListener {
            openCoordDialog()
        }

        catSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.updateCat(parent.getItemAtPosition(position).toString())
                buttonConditions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }


        }

        sizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.updateSize(parent.getItemAtPosition(position).toString())
                buttonConditions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }


        }

        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.updatePriority(parent.getItemAtPosition(position).toString())
                buttonConditions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }


        }

        scheduleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.updateSchedule(parent.getItemAtPosition(position).toString())
                buttonConditions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }


        }

        okButton.setOnClickListener {
            lifecycleScope.launch {
                val r = viewModel.createTaskReq()
                var lat = viewModel.extractLatitude(coord)
                var long = viewModel.extractLongitude(coord)
                if (r != null) {
                    goToTemp(r)
                } else if (viewModel.validCoordinates(lat,long)) {
                    Toast.makeText(
                        requireContext(),
                        "No se han encontrado solicitudes que cumplan los criterios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        cancelButton.setOnClickListener {
            goBackToMenu()
        }

    }

    private fun goBackToMenu() {
        findNavController().navigate(R.id.action_crearTareasFragment_to_adminMenu)
    }

    private fun goToTemp(r: taskReq) {
        val bundle = Bundle()
        bundle.putSerializable("list", ArrayList(r.taskIDList))
        findNavController().navigate(R.id.action_crearTareasFragment_to_tempTaskFragment, bundle)
    }

    private fun openDatePicker(){

        val year: Int
        val month: Int
        val day: Int

        if(!viewModel.dateInitialized()){
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }
        else{
            var cal = viewModel.getCal()
            year = cal.get(Calendar.YEAR)
            month = cal.get(Calendar.MONTH)
            day = cal.get(Calendar.DAY_OF_MONTH)
        }


        val dialog = DatePickerDialog(
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
            }, year, month, day
        )

        dialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            var cal = Calendar.getInstance().apply {
                set(year,month,dayOfMonth)
            }
            viewModel.updateCalendar(cal)
            buttonConditions()
        }

        dialog.show()


    }

    private fun openCoordDialog(){
        val editText = EditText(requireContext())
        if(viewModel.coordInitialized()){
            editText.text = Editable.Factory.getInstance().newEditable(viewModel.getCoords())
        }
        val dialog = AlertDialog.Builder(requireContext()).setTitle("Coordenadas").setMessage("Inserte las coordenadas del punto de encuentro, separadas por comas")
            .setView(editText).setNegativeButton("Cancelar",null)
            .setPositiveButton("OK"){dialog, which ->
                coord = editText.text.toString()
                viewModel.updateCoord(coord)
                buttonConditions()
            }.create()


        dialog.show()
    }


    private fun buttonConditions(){
        okButton.isEnabled = viewModel.buttonConditions()
    }


    private fun resetSpinners() {
        catSpinner.setSelection(0) // Reset to "Cualquiera"
        prioritySpinner.setSelection(0) // Reset to "Cualquiera"
        sizeSpinner.setSelection(0) // Reset to "Cualquiera"
        scheduleSpinner.setSelection(0) // Reset to "Cualquiera"
    }




}