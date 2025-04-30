package com.upv.solidarityHub

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.upv.solidarityHub.databinding.ActivityRegistroBinding
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.DatabaseAPI
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

    private lateinit var buscadorMunicipio: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var showPassButton: Button
    private lateinit var showRepPassButton: Button
    private lateinit var registrarseButton:Button
    private lateinit var botonIrLogearse:Button

    private lateinit var nombreField: TextInputLayout
    private lateinit var apellidosField: TextInputLayout
    private lateinit var correoField: TextInputLayout
    private lateinit var displayNacimiento: TextView
    private lateinit var contrasenaField: TextInputLayout
    private lateinit var repContrasenaField: TextInputLayout



    private val db:SupabaseAPI = SupabaseAPI()

    private lateinit var municipios: Array<String?>
    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        initializeFields()
        initializeButtons()
        initializeSearchView()
        initializeListeners()
        //nombreField.isErrorEnabled = true
        //nombreField.editText!!.error = "Error test"




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






        suggestionAdapter.updateSuggestions(listOf("Municipio"))
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
        contrasenaField = findViewById(R.id.inputContrasena)
        contrasenaField.editText!!.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        repContrasenaField = findViewById(R.id.inputRepContrasena)
        repContrasenaField.editText!!.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        buscadorMunicipio = findViewById(R.id.buscadorMunicipio)



    }

    private fun initializeButtons() {
        showPassButton = findViewById(R.id.showPassButton)
        showRepPassButton = findViewById(R.id.showRepPassButton)
        registrarseButton = findViewById(R.id.registrarseButton)
        botonIrLogearse = findViewById(R.id.botonIrLogearse)

    }

    @SuppressLint("ClickableViewAccessibility")
    //TODO: Refactorizar esto

    private fun initializeListeners() {

        findViewById<Button>(R.id.fechaPickerButton).setOnClickListener {
            val newFragment = DatePickerFragment(this)
            newFragment.show(supportFragmentManager, "datePicker")
        }


        findViewById<Button>(R.id.fechaPickerButton).setOnClickListener {
            val newFragment = DatePickerFragment(this)
            newFragment.show(supportFragmentManager, "datePicker")
        }

        correoField.editText!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if(checkCorreoValidity()){
                    correoField.isErrorEnabled = false
                } else checkAllFields()
            }

        })

        correoField.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(!checkCorreoValidity())  {
                    correoField.isErrorEnabled = true
                    correoField.editText!!.error = "Correo no válido"

                }
            }
        }

        nombreField.editText!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if(!checkNameValidity()){
                    nombreField.editText!!.error = "Por favor, añada su nombre"
                } else checkAllFields()
            }

        })

        apellidosField.editText!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if(!checkApellidosValidity()){
                    apellidosField.editText!!.error = "Por favor, añada sus apellidos"
                } else checkAllFields()
            }

        })

        contrasenaField.editText!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if(checkContrasenaValidity()){
                    contrasenaField.isErrorEnabled = false
                    //contrasenaField.editText!!.error = "Debe contener más de 8 carácteres y almenos un número"
                } else checkAllFields()
            }

        })

        contrasenaField.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
              if(!checkContrasenaValidity())  {
                  contrasenaField.isErrorEnabled = true
                  contrasenaField.editText!!.error = "Debe contener más de 8 carácteres y almenos un número"

              }
            }
        }

        repContrasenaField.editText!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if(checkRepContrasenaValidity()){
                    repContrasenaField.isErrorEnabled = false
                } else checkAllFields()
            }

        })

        repContrasenaField.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(!checkRepContrasenaValidity())  {
                    repContrasenaField.isErrorEnabled = true
                    repContrasenaField.editText!!.error = "Las contraseñas no coinciden"

                }
            }
        }

        buscadorMunicipio.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (checkMunicipioValidity()) {
                checkAllFields()
            }
            false
        })

        buscadorMunicipio.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if (!checkMunicipioValidity()) {
                    checkAllFields()
                }
            }

        }


        showPassButton.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    contrasenaField.editText!!.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    true
                }

                MotionEvent.ACTION_DOWN -> {
                    contrasenaField.editText!!.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    true
                }

                else -> false
            }
        }

        showRepPassButton.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    repContrasenaField.editText!!.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    true
                }

                MotionEvent.ACTION_DOWN -> {
                    repContrasenaField.editText!!.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    true
                }

                else -> false
            }


        }

        registrarseButton.setOnClickListener {

            runBlocking {
                val deferred1 = async {
                registrarse()
                }
                deferred1.await()
            }
        }

        botonIrLogearse.setOnClickListener {
            goToLogin()
        }
    }


    private fun initializeSearchView() {
        recyclerView = findViewById(R.id.recyclerMunicipio)

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            buscadorMunicipio.setQuery(suggestion, true)
            checkAllFields()
            suggestionAdapter.updateSuggestions(emptyList())
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = suggestionAdapter


        try {
            val inputStream = assets.open("municipios")
            municipios = FileReader.readMunicipiosToArray(inputStream)
            searchSuggestions = municipios


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


    suspend fun registrarse() {
        var successfullRegistry = false
        var foundExistingUser = true
        try{
            db.getUsuarioByCorreo(correoField.editText!!.text.toString())
        } catch (e:NoSuchElementException) {
            foundExistingUser = false

            var correo = correoField.editText!!.text.toString()
            var nombre = nombreField.editText!!.text.toString()
            var apellidos = apellidosField.editText!!.text.toString()
            var contrasena = contrasenaField.editText!!.text.toString()
            var fecha_nacimiento = displayNacimiento.text.toString()
            var municipio = buscadorMunicipio.query.toString()

            successfullRegistry = db.registerUsuario(correo, nombre, apellidos, contrasena, fecha_nacimiento,municipio)
            if (successfullRegistry) {
                runBlocking {
                    val deferred1 = async {
                    db.getUsuarioByCorreo(correo)
                        ?.let { it1 -> goToHabilidades(it1) }
                    }
                }


            } else {

            }
        } catch (e:Exception) {
            Toast.makeText(getApplicationContext(),"Hubo un error, porfavor inténtelo más tarde", Toast.LENGTH_SHORT).show()

        }

        if(foundExistingUser) Toast.makeText(getApplicationContext(),"Usuario con ese correo ya existe", Toast.LENGTH_SHORT).show()

    }
    fun goToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun goToHabilidades(usuario:Usuario) {
        val intent = Intent(this, HabilidadesActivity()::class.java)
        intent.putExtra("usuario", usuario)


        startActivity(intent)
    }

    private fun checkCorreoValidity(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(correoField.editText!!.text.toString()).matches()
    }

    private fun checkNameValidity(): Boolean {
        return nombreField.editText!!.text.toString() != ""
    }

    private fun checkApellidosValidity(): Boolean {
        return apellidosField.editText!!.text.toString() != ""
    }

    private fun checkFechaNacimientoValidity(): Boolean {
        return !displayNacimiento.text.equals("")
    }

    private fun checkContrasenaValidity(): Boolean {
        val contrasena = contrasenaField.editText!!.text.toString()
        if (contrasena.length < 8) {
            return false
        }
        return contrasena.any { it.isDigit() }
    }

    private fun checkRepContrasenaValidity(): Boolean {
        var contrasena = contrasenaField.editText!!.text.toString()
        var repetirContrasena = repContrasenaField.editText!!.text.toString()

        return contrasena == repetirContrasena
    }

    private fun checkMunicipioValidity(): Boolean {
        return municipios.contains(buscadorMunicipio.query.toString())
    }

    private fun checkAllFields(): Boolean {
        var allGood: Boolean = checkNameValidity() && checkApellidosValidity() && checkCorreoValidity() && checkContrasenaValidity() && checkRepContrasenaValidity() && checkFechaNacimientoValidity() && checkMunicipioValidity()
        if(allGood) {
            registrarseButton.isEnabled = true; return true
        } else {registrarseButton.isEnabled = false; return false}
    }

}