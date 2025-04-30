package com.upv.solidarityHub

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.taskReq
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Calendar


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
    private lateinit var suggestionAdapter: SuggestionAdapter

    lateinit var okButton : Button
    lateinit var cancelButton : Button
    lateinit var dateButton : Button
    lateinit var locationButton : Button

    private var towns: Array<String?> = emptyArray()
    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)

    lateinit var cal : Calendar
    lateinit var coord: String

    lateinit var req: taskReq

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

        setListeners()

        setSpinners()

        setLocationUI()

        okButton.isEnabled = false

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
            if(checkValidTown()){
                buttonConditions()
            }
            false
        }

        townSearcher.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus && !checkValidTown()){
                buttonConditions()
            }
        }


        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            townSearcher.setQuery(suggestion, true)
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
                buttonConditions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }


        }

        okButton.setOnClickListener{
            lifecycleScope.launch{
                val r = createTaskReq()
                if (r != null) {
                    goToTemp()
                }
                else if(extractLatitude(coord) != null && extractLongitude(coord) != null){
                    Toast.makeText(requireContext(), "No se han encontrado solicitudes que cumplan los criterios", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToTemp() {

        val bundle = Bundle()
        bundle.putSerializable("list", ArrayList(req.taskIDList))

        findNavController().navigate(R.id.action_crearTareasFragment_to_tempTaskFragment, bundle)
    }

    private fun openDatePicker(){

        val year: Int
        val month: Int
        val day: Int

        if(!dateInitialized()){
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }
        else{
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
            cal = Calendar.getInstance().apply {
                set(year,month,dayOfMonth)

            }
            buttonConditions()
        }

        dialog.show()


    }

    private fun openCoordDialog(){
        val editText = EditText(requireContext())
        if(coordInitialized()){
            editText.text = Editable.Factory.getInstance().newEditable(coord)
        }
        val dialog = AlertDialog.Builder(requireContext()).setTitle("Coordenadas").setMessage("Inserte las coordenadas del punto de encuentro, separadas por comas")
            .setView(editText).setNegativeButton("Cancelar",null)
            .setPositiveButton("OK"){dialog, which ->
                coord = editText.text.toString()
                buttonConditions()
            }.create()


        dialog.show()
    }

    private fun categorySelected():Boolean{
        return getCategory() != "Cualquiera"
    }

    private fun prioritySelected():Boolean{
        return getPriority() != "Cualquiera"
    }

    private fun sizeSelected():Boolean{
        return getSize() != "Cualquiera"
    }

    private fun scheduleSelected():Boolean{
        return getSchedule() != "Cualquiera"
    }

    private fun filtersSelected():Boolean{
        return categorySelected() || prioritySelected() || sizeSelected() || scheduleSelected() || checkValidTown()
    }

    private fun buttonConditions(){
        okButton.isEnabled = filtersSelected() && dateInitialized() && coordInitialized()
    }

    private fun checkValidTown(): Boolean {
        return towns.contains(getTown())
    }

    private fun dateInitialized(): Boolean{
        return ::cal.isInitialized
    }

    private fun coordInitialized(): Boolean{
        return ::coord.isInitialized
    }

    private fun getCategory():String{
        return catSpinner.selectedItem.toString()
    }

    private fun getPriority():String{
        return prioritySpinner.selectedItem.toString()
    }

    private fun getSize():String{
        return sizeSpinner.selectedItem.toString()
    }

    private fun getSchedule():String{
        return scheduleSpinner.selectedItem.toString()
    }

    private fun getTown():String{
        return townSearcher.query.toString()
    }

    fun extractLongitude(input: String): Double? {
        val coordinateRegex = Regex("""(-?\d+(\.\d+)?)[,\s]+(-?\d+(\.\d+)?)""")
        val match = coordinateRegex.find(input)
        return if (match?.groupValues?.getOrNull(3) != null) {
            match.groupValues[3].toDoubleOrNull()
        } else {
            null
        }
    }

    fun extractLatitude(input: String): Double? {
        val coordinateRegex = Regex("""(-?\d+(\.\d+)?)[,\s]+(-?\d+(\.\d+)?)""")
        val match = coordinateRegex.find(input)
        return if (match?.groupValues?.getOrNull(1) != null) {
            match.groupValues[1].toDoubleOrNull()
        } else {
            null
        }
    }

    private suspend fun createTaskReq(): taskReq? {
        var cat : String?
        var priority : String?
        var size : String?
        var schedule : String?
        var town : String?
        var long : Double?
        var lat : Double?

        if(categorySelected()){
            cat = getCategory()
        }
        else{
            cat = null
        }

        if(prioritySelected()){
            priority = getPriority()
        }
        else{
            priority = null
        }

        if(sizeSelected()){
            size = getSize()
        }
        else{
            size = null
        }

        if(scheduleSelected()){
            schedule = getSchedule()
        }
        else{
            schedule = null
        }

        if(checkValidTown()){
            town = getTown()
        }
        else{
            town = null
        }

        lat = extractLatitude(coord)

        long = extractLongitude(coord)

        if(lat != null && long != null){
            req = taskReq.create(cat,town,priority,schedule,size,lat,long,cal, null)!!
            return req
        }
        else{
            Toast.makeText(requireContext(), "Formato de coordenadas incorrecto", Toast.LENGTH_SHORT).show()
            return null
        }

    }




}