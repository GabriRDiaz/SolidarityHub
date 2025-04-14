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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.SolicitudAyuda
import java.io.IOException
import com.upv.solidarityHub.databinding.ActivitySolAyudaBinding
import kotlinx.coroutines.launch


class SolAyuda : AppCompatActivity() {
    lateinit var okButton : Button

    lateinit var inputTextTitle : TextInputEditText
    lateinit var inputTextDesc : TextInputEditText

    lateinit var catSpinner : Spinner
    lateinit var hourSpinner : Spinner
    lateinit var sizeSpinner : Spinner
    lateinit var urgSpinner: Spinner

    lateinit var townSearcher : SearchView
    lateinit var townRecycler : RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter

    private lateinit var binding: ActivitySolAyudaBinding

    val categories = arrayOf("Limpieza", "Recogida de comida", "Reconstrucción", "Primeros auxilios", "Artículos para bebés", "Asistencia a mayores", "Asistencia a discapacitados", "Artículos de primera necesidad", "Otros", "Transporte", "Cocina", "Mascotas")

    val hours = arrayOf("Manaña Temprana (6:00 - 9:00)", "Mañana (9:00 - 12:00)", "Mediodía (12:00 - 15:00)", "Tarde (15:00 - 18:00)", "Noche Temprana (18:00 - 21:00)", "Noche (21:00 - 00:00)", "Madrugada (00:00 - 6:00)")

    val groupSize = arrayOf("Pequeña (5 voluntarios máx.)", "Media (15 voluntarios máx.)", "Grande (15+ voluntarios)")

    val urgenciaList = arrayOf("Baja", "Media", "Alta")

    private lateinit var towns: Array<String?>
    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySolAyudaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findComponents()

        setListeners()

        setSpinners()

        setLocationUI()

        binding.buttonOK.isEnabled = false

    }

    private fun getTitleText(): String{
        return inputTextTitle.text.toString()
    }

    private fun getDesc():String {
        return inputTextDesc.text.toString()
    }

    private fun nullTitle(): Boolean {
        return getTitleText().equals("")
    }

    private fun nullDesc():Boolean{
        return getDesc().equals("")
    }

    private fun checkValidTown(): Boolean {
        return towns.contains(townSearcher.query.toString())
    }

    private fun buttonConditions(){
        binding.buttonOK.isEnabled = !nullTitle() && !nullDesc() && checkValidTown()
    }

    suspend fun createRequest(){
        val req = SolicitudAyuda.create(getTitleText(), getDesc(), catSpinner.selectedItem.toString(),townSearcher.query.toString(), hourSpinner.selectedItem.toString(), sizeSpinner.selectedItem.toString(), urgSpinner.selectedItem.toString())
    }

    private fun findComponents(){
        okButton = binding.buttonOK

        inputTextTitle = binding.textInputTitle
        inputTextDesc = binding.textInputDescription

        catSpinner = binding.CatSpinner
        hourSpinner = binding.HourSpinner
        sizeSpinner = binding.SizeSpinner
        urgSpinner = binding.spinnerUrg

        townSearcher = binding.SearchViewLocations
        townRecycler = binding.RecyclerViewLocations

    }

    private fun setListeners(){
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

        binding.buttonOK.setOnClickListener(){
            lifecycleScope.launch {
                createRequest()
            }
        }

    }

    private fun setSpinners(){
        val adapterCat = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, categories
        )
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.CatSpinner.adapter = adapterCat

        val adapterHour = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, hours
        )
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.HourSpinner.adapter = adapterHour

        val adapterSize = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, groupSize
        )
        adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.SizeSpinner.adapter = adapterSize

        val adapterUrg = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, urgenciaList
        )
        adapterUrg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerUrg.adapter = adapterUrg


    }

    private fun setLocationUI(){
        binding.SearchViewLocations.setOnKeyListener { v, keyCode, event ->
            if (checkValidTown()) {
                buttonConditions()
            }
            false
        }

        binding.SearchViewLocations.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus && !checkValidTown()) {
                buttonConditions()
            }
        }

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            binding.SearchViewLocations.setQuery(suggestion, true)
            buttonConditions()
            suggestionAdapter.updateSuggestions(emptyList())
        }

        binding.RecyclerViewLocations.apply {
            layoutManager = LinearLayoutManager(this@SolAyuda)
            adapter = suggestionAdapter
        }

        try {
            towns = assets.open("municipios").use { FileReader.readMunicipiosToArray(it) }
            searchSuggestions = towns
        } catch (e: IOException) {
            Log.e("SolAyuda", "Error loading towns", e)
            towns = emptyArray()
            searchSuggestions = emptyArray()
        }

        binding.SearchViewLocations.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = searchSuggestions.filter {
                    it?.contains(newText.orEmpty(), ignoreCase = true) ?: false
                }
                suggestionAdapter.updateSuggestions(filtered)
                return true
            }
        })


    }
}