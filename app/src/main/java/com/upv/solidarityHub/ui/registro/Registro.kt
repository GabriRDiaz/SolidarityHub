package com.upv.solidarityHub.ui.registro

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.upv.solidarityHub.ui.habilidades.HabilidadesActivity
import com.upv.solidarityHub.ui.login.Login
import com.upv.solidarityHub.R
import com.upv.solidarityHub.utils.municipioSpinner.SuggestionAdapter
import com.upv.solidarityHub.databinding.ActivityRegistroBinding
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.ui.components.DatePicker.DatePickerFragment
import com.upv.solidarityHub.ui.components.DatePicker.DatePickerHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.io.IOException


class Registro : AppCompatActivity(), DatePickerHandler {

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

    private lateinit var viewModel: RegistroViewModel


    //TODO LLAMAR A LA BD DEBERÍA NO SER NECESARIO, O LO HACE EL VIEWMODEL O SE OBTIENE EL USUARIO USANDO UN MÉTODO GLOBAL LOCAL
    private val db:SupabaseAPI = SupabaseAPI()

    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this).get(RegistroViewModel::class.java)

        initializeFields()
        initializeButtons()
        initializeSearchView()
        initializeListeners()
        initializeObservers()

        suggestionAdapter.updateSuggestions(listOf("Municipio"))
    }

    public override fun handleDate(date: String) {
        viewModel.updateFechaNacimiento(date)
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
    private fun initializeListeners() {

        findViewById<Button>(R.id.fechaPickerButton).setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(supportFragmentManager, "datePicker")
        }

        nombreField.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateNombre(nombreField.editText!!.text.toString())
            }
        })

        apellidosField.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateApellidos(apellidosField.editText!!.text.toString())
            }
        })

        correoField.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(!viewModel.checkCorreoIsValid())  {
                    correoField.isErrorEnabled = true
                    correoField.editText!!.error = "Correo no válido"

                }
            }
        }

        correoField.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateCorreo(correoField.editText!!.text.toString())
            }
        })

        contrasenaField.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
              if(!viewModel.checkContrasenaIsValid())  {
                  contrasenaField.isErrorEnabled = true
                  contrasenaField.editText!!.error = "Debe contener más de 8 caracteres, una mayúscula, un número y un caracter especial (,.-:;)"

              }
            }
        }

        contrasenaField.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateContrasena(contrasenaField.editText!!.text.toString())
            }
        })

        repContrasenaField.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(!viewModel.checkRepContrasenaIsValid())  {
                    repContrasenaField.isErrorEnabled = true
                    repContrasenaField.editText!!.error = "Las contraseñas no coinciden"

                }
            }
        }

        repContrasenaField.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateRepContrasena(repContrasenaField.editText!!.text.toString())
            }
        })

        buscadorMunicipio.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            viewModel.updateMunicipio(buscadorMunicipio.query.toString())
            false
        })

        recyclerView.setOnTouchListener { v, event ->
            viewModel.updateMunicipio(buscadorMunicipio.query.toString())
            false
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
                    repContrasenaField.editText!!.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    true
                }

                MotionEvent.ACTION_DOWN -> {
                    repContrasenaField.editText!!.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    true
                }

                else -> false
            }
        }

        registrarseButton.setOnClickListener {
            Log.d(TAG, "Click")
            runBlocking {
                val deferred1 = async {
                viewModel.registrarse()
                }
                deferred1.await()
            }
        }

        botonIrLogearse.setOnClickListener {
            goToLogin()
        }
    }

    private fun initializeObservers() {
        viewModel.correoIsValid.observe(this, Observer { newCorreoIsValid ->
            if(newCorreoIsValid) {
                correoField.isErrorEnabled = false
                correoField.editText!!.error = null
            }
        })

        viewModel.nombreIsValid.observe(this, Observer { newNombreIsValid ->
            if(newNombreIsValid) {
                nombreField.editText!!.error = null
            }   else {
                nombreField.editText!!.error = "Introduzca su nombre"
            }
            nombreField.isErrorEnabled = !newNombreIsValid

        })

        viewModel.apellidosIsValid.observe(this, Observer { newApellidosIsValid ->
            apellidosField.isErrorEnabled = !newApellidosIsValid
            if(newApellidosIsValid) apellidosField.editText!!.error = null
            else apellidosField.editText!!.error = "Introduzca sus apellidos"

        })

        viewModel.contrasenaIsValid.observe(this, Observer { newContrasenaIsValid ->
            if(newContrasenaIsValid) {
                contrasenaField.editText!!.error = null
                contrasenaField.isErrorEnabled = false
            }
        })

        viewModel.repContrasenaIsValid.observe(this, Observer { newRepContrasenaIsValid ->
            if(newRepContrasenaIsValid) {
                repContrasenaField.editText!!.error = null
                repContrasenaField.isErrorEnabled = false
            }
        })

        viewModel.registryFinalized.observe(this, Observer { newRegistryFinalized ->
            if(newRegistryFinalized) {
                db.setLogedUserCorreo(viewModel.correo.value.toString())
                goToHabilidades()
            }
        })

        viewModel.allIsValid.observe(this, Observer { newAllIsValid ->
            registrarseButton.isEnabled = newAllIsValid
        })
    }


    private fun initializeSearchView() {
        recyclerView = findViewById(R.id.recyclerMunicipio)

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            buscadorMunicipio.setQuery(suggestion, true)
            suggestionAdapter.updateSuggestions(emptyList())
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = suggestionAdapter


        try {
            val inputStream = assets.open("municipios")
            viewModel.updateMunicipiosList(FileReader.readMunicipiosToArray(inputStream))
            searchSuggestions = viewModel.municipios.value!!
        } catch (e: IOException) {
            Log.d("DEBUG","Failure to read file" + "   " + e.toString())
        }

        buscadorMunicipio.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.updateMunicipio(buscadorMunicipio.query.toString())
                val filteredSuggestions = searchSuggestions.filter { it!!.contains(newText ?: "", ignoreCase = true) }
                suggestionAdapter.updateSuggestions(filteredSuggestions)
                return true
            }
        })

    }

    fun goToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

    fun goToHabilidades() {
        val intent = Intent(this, HabilidadesActivity()::class.java)
        startActivity(intent)
    }
}