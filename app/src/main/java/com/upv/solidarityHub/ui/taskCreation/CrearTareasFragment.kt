package com.upv.solidarityHub.ui.taskCreation

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.assignationAlgorythm
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.MultiColumnAdapter
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.taskReq
import com.upv.solidarityHub.utils.municipioSpinner.SuggestionAdapter
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.Calendar

/**
 * A simple [Fragment] subclass.
 * Use the [CrearTareasFragment.newInstance] factory method to
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_crear_tareas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CrearTareasViewModel::class.java)
        findComponents(view)
        setListeners()
        setSpinners()
        setLocationUI()

        parentFragmentManager.setFragmentResultListener("task_edit_complete", viewLifecycleOwner) { _, _ ->
            onEditComplete()
        }
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
            openDateTimeRangePicker()
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
                val lat = viewModel.extractLatitude(coord)
                val long = viewModel.extractLongitude(coord)

                if (r != null) {
                    viewModel.initializeTaskFlow(r)
                    processTasks()
                } else if (viewModel.validCoordinates(lat, long)) {
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

    private fun goToTemp(taskID: Int, userList: List<Usuario>) {
        findNavController().navigate(
            R.id.action_crearTareasFragment_to_tempTaskFragment,
            tempTaskFragment.newInstance(ArrayList(userList), taskID).arguments
        )
    }

    private fun openDateTimeRangePicker() {
        val c = Calendar.getInstance()

        val startYear = c.get(Calendar.YEAR)
        val startMonth = c.get(Calendar.MONTH)
        val startDay = c.get(Calendar.DAY_OF_MONTH)

        val startDatePicker = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val startCal = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }

            val startTimePicker = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                startCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                startCal.set(Calendar.MINUTE, minute)

                val endDatePicker = DatePickerDialog(requireContext(), { _, endYear, endMonth, endDay ->
                    val endCal = Calendar.getInstance().apply {
                        set(endYear, endMonth, endDay) }

                    if (endCal.before(startCal)) {
                        Toast.makeText(requireContext(), "La fecha final debe ser posterior a la fecha de inicio", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateStartAndEndCalendar(startCal, endCal)
                        buttonConditions()
                    }

                }, year, month, dayOfMonth)

                endDatePicker.datePicker.minDate = startCal.timeInMillis
                endDatePicker.setTitle("Selecciona una fecha final")
                endDatePicker.show()

            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true)

            startTimePicker.setTitle("Seleccione una hora de inicio")
            startTimePicker.show()

        }, startYear, startMonth, startDay)

        startDatePicker.setTitle("Seleccione una fecha de inicio")
        startDatePicker.show()
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

    suspend fun Fragment.showMultiColumnPopupSuspend(
        users: List<Usuario>,
        columnsCount: Int,
        taskName: String,
        taskCat: String
    ): String? = suspendCancellableCoroutine { cont ->
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_popup, null)
        val titleText = dialogView.findViewById<TextView>(R.id.taskNamePopup)
        titleText.text = taskName
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewPopup)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        System.out.println("User list size: ${users.size}")
        users.forEach { System.out.println("User: ${it.nombre}, ${it.municipio}") }
        recyclerView.adapter = MultiColumnAdapter(users, columnsCount, taskCat )

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                cont.resume("OK", null)
            }
            .setNeutralButton("Editar") { _, _ ->
                cont.resume("Editar", null)
            }
            .setOnCancelListener {
                cont.resume(null, null)
            }
            .create().show()
    }

    private suspend fun processTasks() {
        var taskId: Int?
        while (true) {
            taskId = viewModel.getNextTask() ?: break

            val prepared = viewModel.prepareAssignment(taskId)

            if (prepared == null) {
                // No users available - show error and skip to next task
                showNoUsersLeftPopUp()
                viewModel.onTaskFailed(taskId)
                continue
            }

            val (alg, users) = prepared

            // Check if we have at least the minimum required users
            if (users.size < alg.getMin()) {
                showNoUsersLeftPopUp()
                viewModel.deleteTask(taskId)
                viewModel.onTaskFailed(taskId)
                continue
            }

            // Only show popup if we have enough users
            when (showTaskPopup(alg, users, taskId)) {
                "OK" -> {
                    assignUsers(alg, users)
                    viewModel.onTaskCompleted()
                }
                "Editar" -> {
                    viewModel.onEditStarted()
                    goToTemp(taskId, users)
                    break
                }
            }
        }

        if (viewModel.haveAllTasksBeenAssigned()) {
            Toast.makeText(
                requireContext(),
                "Se han asignado usuarios a todas las tareas",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private suspend fun assignUsers(alg: assignationAlgorythm, users: List<Usuario>) {
        val taskId = alg.getCurrentTask().id
        users.forEach { user ->
            try {
                if (taskId != null) {
                    SupabaseAPI().createIsAssigned(taskId, user)
                }
            } catch (e: Exception) {
                Log.e("AssignError", "Failed to assign user ${user.correo}", e)
            }
        }
    }

    private suspend fun showTaskPopup(
        alg: assignationAlgorythm,
        users: List<Usuario>,
        taskId: Int
    ): String? {
        return viewModel.IDToTaskName(taskId)?.let { taskName ->
            showMultiColumnPopupSuspend(
                users,
                viewModel.columnNumber(taskId),
                taskName,
                viewModel.IDToTaskCat(taskId)!!
            )
        }
    }

    private fun onEditComplete() {
        viewModel.onEditFinished()
        lifecycleScope.launch { processTasks() }
    }

    private fun showNoUsersLeftPopUp() {
        Toast.makeText(
            requireContext(),
            "No hay suficientes voluntarios. La tarea no se ha creado.",
            Toast.LENGTH_LONG
        ).show()
    }


}