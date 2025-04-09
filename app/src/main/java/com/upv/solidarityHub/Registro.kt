package com.upv.solidarityHub

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upv.solidarityHub.databinding.ActivityRegistroBinding
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.security.AccessController.getContext
import java.util.Calendar
//TODO: ARREGLO PARA SALIR DEL PASO, CORREGIR!!



class Registro : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRegistroBinding
    private var inputedNombre:String = ""
    private var inputedCorreo:String = ""
    private var inputedApellidos:String = ""
    private var inputedPassword:String = ""
    private var inputedMunicipio:String = ""
    private var inputedNacimiento:String = ""



    private lateinit var buscadorMunicipio: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var showPassButton: Button
    private lateinit var showRepPassButton: Button



    private lateinit var nombreField: EditText
    private lateinit var apellidosField: EditText
    private lateinit var correoField: EditText
    private lateinit var displayNacimiento: TextView
    private lateinit var contrasenaField: EditText
    private lateinit var repContrasenaField: EditText
    private lateinit var errorCorreo: TextView
    private lateinit var errorContrasena: TextView
    private lateinit var errorRepContrasena:TextView
    private lateinit var registrarseButton:Button


    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        initializeFields()
        initializeButtons()
        initializeListeners()
        initializeSearchView()


        findViewById<Button>(R.id.fechaPickerButton).setOnClickListener {
            val newFragment = DatePickerFragment(this)
            newFragment.show(supportFragmentManager, "datePicker")
        }
        //TODO: IMPLEMENTAR LAS LLAMADAS A LA DB EN UN PROXY
        var db = SupabaseAPI()
        Log.d("DEBUG", "Trying to access database" +db.toString())
        var usuario: Usuario? = null
        runBlocking {
            val deferred1 = async {
                usuario = db.getUsuarioByCorreo("xuli@gmail.com") }
            deferred1.await()
            }
        if (usuario != null) {
            Log.d("DEBUG", "Success!!! ")
            Log.d("DEBUG", usuario!!.correo + usuario!!.nombre)
        }




    }

    class DatePickerFragment(parent: Registro) : DialogFragment(), DatePickerDialog.OnDateSetListener {
        var padre:Registro = parent
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            // Use the current date as the default date in the picker.
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // Create a new instance of DatePickerDialog and return it.
            return DatePickerDialog(requireContext(), this, year, month, day)

        }

        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            padre.setDate(year.toString() + "-" + month.toString() + "-" + day.toString())
        }
    }

    public fun setDate(date:String) {
        displayNacimiento.text = date
        checkAllFields()
    }

    private fun initializeFields() {
        correoField = findViewById(R.id.inputCorreo)
        nombreField = findViewById(R.id.inputNombre)
        apellidosField = findViewById(R.id.inputApellidos)
        displayNacimiento = findViewById(R.id.displayNacimiento)
        errorCorreo = findViewById(R.id.errorCorreo)
        errorContrasena = findViewById(R.id.errorContrasena)
        errorRepContrasena = findViewById(R.id.errorRepContrasena)
        contrasenaField = findViewById(R.id.inputContrasena)
        repContrasenaField = findViewById(R.id.inputRepContrasena)
        buscadorMunicipio = findViewById(R.id.buscadorMunicipio)



    }

    private fun initializeButtons() {
        showPassButton = findViewById(R.id.showPassButton)
        showRepPassButton = findViewById(R.id.showRepPassButton)
        registrarseButton = findViewById(R.id.registrarseButton)

    }

    private fun initializeListeners() {
        findViewById<Button>(R.id.fechaPickerButton).setOnClickListener {
            val newFragment = DatePickerFragment(this)
            newFragment.show(supportFragmentManager, "datePicker")
        }

        correoField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(checkCorreoValidity()) {errorCorreo.visibility = View.INVISIBLE}
            checkAllFields()
            true
        })

        correoField.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if(!checkCorreoValidity()) {errorCorreo.visibility = View.VISIBLE}
            }
            checkAllFields()
        }

        contrasenaField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(checkContrasenaValidity()) {errorContrasena.visibility = View.INVISIBLE}
            checkAllFields()
            true
        })

        contrasenaField.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if(!checkContrasenaValidity()) {errorContrasena.visibility = View.VISIBLE}
            }
            checkAllFields()
        }

        repContrasenaField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(checkRepContrasenaValidity()) {errorRepContrasena.visibility = View.INVISIBLE}
            checkAllFields()
            true
        })

        repContrasenaField.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if(!checkRepContrasenaValidity()) {errorRepContrasena.visibility = View.VISIBLE}
            }
            checkAllFields()
        }

        nombreField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(checkNameValidity()) {checkAllFields()}
            false
        })

        nombreField.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if(!checkNameValidity()) {checkAllFields()}
            }

        }

        apellidosField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(checkApellidosValidity()) {checkAllFields()}
            true
        })

        apellidosField.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if(!checkApellidosValidity()) {checkAllFields()}
            }

        }

        buscadorMunicipio.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if(checkMunicipioValidity()) {checkAllFields()}
            true
        })

        buscadorMunicipio.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if(!checkMunicipioValidity()) {checkAllFields()}
            }

        }

    }



    private fun initializeSearchView() {
        recyclerView = findViewById(R.id.recyclerMunicipio)

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            // Set the SearchView text to the clicked suggestion
            buscadorMunicipio.setQuery(suggestion, true)
            // Optionally, you can also clear the suggestions
            suggestionAdapter.updateSuggestions(emptyList())
        }
        suggestionAdapter.updateSuggestions(listOf("Municipio"))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = suggestionAdapter


        try {
            val inputStream = assets.open("municipios")
            searchSuggestions = FileReader.readMunicipiosToArray(inputStream)

            // Read from inputStream
        } catch (e: IOException) {
            Log.d("DEBUG","Failure to read file" + "   " + e.toString())
        }

        buscadorMunicipio.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    private fun checkCorreoValidity(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(correoField.text).matches()
    }

    private fun checkNameValidity(): Boolean {
        return !nombreField.text.equals("")
    }

    private fun checkApellidosValidity(): Boolean {
        return !apellidosField.text.equals("")
    }

    private fun checkFechaNacimientoValidity(): Boolean {
        return !displayNacimiento.text.equals("")
    }

    private fun checkContrasenaValidity(): Boolean {
        val contrasena = contrasenaField.text
        if (contrasena.length < 8) {
            return false
        }
        return contrasena.any { it.isDigit() }
    }

    private fun checkRepContrasenaValidity(): Boolean {
        return contrasenaField.text.equals(repContrasenaField.text)
    }

    private fun checkMunicipioValidity(): Boolean {
        return searchSuggestions.size == 1
    }

    private fun checkAllFields(): Boolean {
        var allGood: Boolean = checkNameValidity() && checkApellidosValidity() && checkCorreoValidity() && checkContrasenaValidity() && checkRepContrasenaValidity() && checkFechaNacimientoValidity() && checkMunicipioValidity()
        if(allGood) {
            registrarseButton.isEnabled = true; return true
        } else {registrarseButton.isEnabled = false; return false}
    }

}