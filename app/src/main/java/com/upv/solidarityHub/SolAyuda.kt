package com.upv.solidarityHub

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.solicitudAyuda
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar

class SolAyuda : AppCompatActivity() {
    lateinit var okButton : Button

    lateinit var inputTextTitle : TextInputEditText
    lateinit var inputTextDesc : TextInputEditText

    lateinit var selectedDate : Calendar

    lateinit var catSpinner : Spinner
    lateinit var hourSpinner : Spinner
    lateinit var sizeSpinner : Spinner

    lateinit var townSearcher : SearchView
    lateinit var townRecycler : RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter



    val formatter = SimpleDateFormat("dd/MM/yyyy")

    val categories = arrayOf("Limpieza", "Recogida de comida", "Reconstrucción", "Primeros auxilios", "Artículos para bebés", "Asistencia a mayores", "Asistencia a discapacitados", "Artículos de primera necesidad", "Otros", "Transporte", "Cocina", "Mascotas")

    val hours = arrayOf("Manaña Temprana (6:00 - 9:00)", "Mañana (9:00 - 12:00)", "Mediodía (12:00 - 15:00)", "Tarde (15:00 - 18:00)", "Noche Temprana (18:00 - 21:00)", "Noche (21:00 - 00:00)", "Madrugada (00:00 - 6:00)")

    val groupSize = arrayOf("Pequeña (5 voluntarios máx.)", "Media (15 voluntarios máx.)", "Grande (15+ voluntarios)")

    private lateinit var towns: Array<String?>
    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sol_ayuda)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        okButton = findViewById<Button>(R.id.buttonOK)

        inputTextTitle = findViewById<TextInputEditText>(R.id.textInputTitle)
        inputTextDesc = findViewById<TextInputEditText>(R.id.textInputDescription)

        catSpinner = findViewById<Spinner>(R.id.CatSpinner)
        hourSpinner = findViewById<Spinner>(R.id.HourSpinner)
        sizeSpinner = findViewById<Spinner>(R.id.SizeSpinner)

        townSearcher = findViewById<SearchView>(R.id.SearchViewLocations)
        townRecycler = findViewById<RecyclerView>(R.id.RecyclerViewLocations)


        okButton.isEnabled = false

        inputTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                buttonConditions()
                if(nullTitle()){
                    inputTextTitle.error = "Por favor, añada un título"
                }
            }

        })

        inputTextDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                buttonConditions()
                if(nullDesc()){
                    inputTextDesc.error = "Por favor, añada una descripcion"
                }
            }
        })

        val adapterCat = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, categories
        )
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        catSpinner.adapter = adapterCat

        val adapterHour = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, hours
        )
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        hourSpinner.adapter = adapterHour

        val adapterSize = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, groupSize
        )
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sizeSpinner.adapter = adapterSize

        townSearcher.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (checkValidTown()) {
                buttonConditions()
            }
            false
        })

        townSearcher.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if (!checkValidTown()) {
                    buttonConditions()
                }
            }

        }

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            // Set the SearchView text to the clicked suggestion
            townSearcher.setQuery(suggestion, true)
            buttonConditions()
            // Optionally, you can also clear the suggestions
            suggestionAdapter.updateSuggestions(emptyList())
        }
        townRecycler.layoutManager = LinearLayoutManager(this)
        townRecycler.adapter = suggestionAdapter

        try {
            val inputStream = assets.open("municipios")
             towns = FileReader.readMunicipiosToArray(inputStream)
            searchSuggestions = towns

            // Read from inputStream
        } catch (e: IOException) {
            Log.d("DEBUG","Failure to read file" + "   " + e.toString())
        }

        townSearcher.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredSuggestions = searchSuggestions.filter { it!!.contains(newText ?: "", ignoreCase = true) }
                suggestionAdapter.updateSuggestions(filteredSuggestions)
                return true
            }
        })


    }

    fun calendarClicked(view : View){

        val calendar = if (validDate()){
            selectedDate.clone() as Calendar
        }
        else{
            Calendar.getInstance()
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog: DatePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle date selection
                selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR,selectedYear)
                    set(Calendar.MONTH, selectedMonth)
                    set(Calendar.DAY_OF_MONTH, selectedDay)
                }
                val formattedDate = formatter.format(selectedDate.time)
                Toast.makeText(this, "Fecha seleccionada: $formattedDate", Toast.LENGTH_LONG).show()
                buttonConditions()
            },
            year, month, dayOfMonth
        )
        datePickerDialog.show()


    }

    private fun getTitleText(): String{
        return inputTextTitle.text.toString()
    }

    private fun getDesc():String {
        return inputTextDesc.text.toString()
    }

    private fun getDate():Calendar{
        return selectedDate
    }

    private fun nullTitle(): Boolean {
        return getTitleText().equals("")
    }

    private fun nullDesc():Boolean{
        return getDesc().equals("")
    }

    private fun validDate():Boolean{
        return ::selectedDate.isInitialized
    }

    private fun checkValidTown(): Boolean {
        return towns.contains(townSearcher.query.toString())
    }

    private fun buttonConditions(){
        okButton.isEnabled = !nullTitle() && !nullDesc() && validDate() && checkValidTown()
    }

    fun createRequest(view: View){
        val newReq : solicitudAyuda = solicitudAyuda(getTitleText(),getDesc(), catSpinner.selectedItem.toString(),townSearcher.query.toString(),getDate(), hourSpinner.selectedItem.toString(), sizeSpinner.selectedItem.toString())

    }

}