package com.upv.solidarityHub.ui.modificarPerfil

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.ActivityModPerfilBinding
import com.upv.solidarityHub.databinding.ActivityRegistroBinding
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Habilidad
import com.upv.solidarityHub.ui.components.DatePicker.DatePickerHandler
import com.upv.solidarityHub.ui.habilidades.HabilidadesFragment
import com.upv.solidarityHub.utils.TextInputLayoutUtils
import com.upv.solidarityHub.utils.municipioSpinner.SuggestionAdapter
import java.io.IOException
import com.upv.solidarityHub.utils.TextInputLayoutUtils.setErrorTo

class ModificarPerfilActivity : AppCompatActivity(), HabilidadesFragment.HabilidadesListener, DatePickerHandler {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityModPerfilBinding

    private lateinit var buscadorMunicipio: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var showPassButton: Button
    private lateinit var cancelarButton: Button
    private lateinit var confirmarButton: Button
    private lateinit var habilidadesButton: Button

    private lateinit var nombreField: TextInputLayout
    private lateinit var apellidosField: TextInputLayout
    private lateinit var displayNacimiento: TextView
    private lateinit var contrasenaField: TextInputLayout
    private lateinit var oldContrasenaField: TextInputLayout


    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)

    private lateinit var viewModel: ModificarPerfilViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityModPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ModificarPerfilViewModel::class.java)
        initializeFields()
        initializeButtons()
        viewModel.setOriginalUserValues()
        initializeSearchView()
        setFieldsToViewmodelValues()
        initializeListeners()
        initializeObservers()
    }

    private fun initializeFields() {
        nombreField = findViewById(R.id.mod_nombreInput)
        apellidosField = findViewById(R.id.mod_apellidosInput)
        contrasenaField = findViewById(R.id.mod_ContrasenaInput)
        oldContrasenaField = findViewById(R.id.mod_oldContrasenaInput)
        displayNacimiento = findViewById(R.id.displayNacimiento)
        buscadorMunicipio = findViewById(R.id.buscadorModMunicipio)
    }

    private fun initializeButtons() {
        showPassButton = findViewById(R.id.mod_mostrarContrasenaButton)
        confirmarButton = findViewById(R.id.mod_confirmarButton)
        cancelarButton = findViewById(R.id.mod_cancelarButton)
        habilidadesButton = findViewById(R.id.mod_HabilidadesButton)
    }

    private fun initializeListeners() {
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

        contrasenaField.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateApellidos(contrasenaField.editText!!.text.toString())
            }
        })

        oldContrasenaField.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateOldContrasena(oldContrasenaField.editText!!.text.toString())
            }
        })

        habilidadesButton.setOnClickListener {
            val dialogFragment: HabilidadesFragment = HabilidadesFragment(viewModel.habilidades.value!!)
            dialogFragment.show(supportFragmentManager, "Introduzca sus habilidades")
        }

        confirmarButton.setOnClickListener {
            var contrasenaAcertada = true
            try {
                contrasenaAcertada = viewModel.confirmar()
            } catch (e: Exception) {
                Toast.makeText(this, "Hubo un error, por favor vuelva a intenarlo más tarde", Toast.LENGTH_LONG).show()
            }

            if(contrasenaAcertada) {
                Toast.makeText(this, "Modificado con éxito", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Contraseña Errónea", Toast.LENGTH_LONG).show()
            }

        }

        cancelarButton. setOnClickListener {
            //viewmodel.clearAllFields()
            finish()
        }

    }

    private fun initializeObservers() {
        viewModel.nombreIsValid.observe(this, Observer { newNombreIsValid ->
            nombreField.setErrorTo("Nombre no válido", !newNombreIsValid)
        })

        viewModel.apellidosIsValid.observe(this, Observer { newApellidosIsValid ->
            apellidosField.setErrorTo("Apellidos no válidos", !newApellidosIsValid)
        })

        viewModel.contrasenaIsValid.observe(this, Observer { newContrasenaIsValid ->
            contrasenaField.setErrorTo("Contraseña no válida", !newContrasenaIsValid)
        })

        viewModel.allIsValid.observe(this, Observer { newAllIsValid ->
            confirmarButton.isEnabled = newAllIsValid
        })
    }

    private fun initializeSearchView() {
        recyclerView = findViewById(R.id.recyclerModMunicipio)

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            buscadorMunicipio.setQuery(suggestion, true)
            suggestionAdapter.updateSuggestions(emptyList())
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = suggestionAdapter

        try {
            val assetManager = this.assets
            val inputStream = assetManager.open("municipios")
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

        suggestionAdapter.updateSuggestions(listOf(SupabaseAPI().getLogedUser().municipio))

    }

    private fun setFieldsToViewmodelValues() {
        nombreField.editText!!.setText(viewModel.nombre.value)
        apellidosField.editText!!.setText(viewModel.apellidos.value)
        contrasenaField.editText!!.setText(viewModel.contrasena.value)
        displayNacimiento.setText(viewModel.fechaNacimiento.value)
        suggestionAdapter.updateSuggestions(listOf(viewModel.municipio.value))
    }

    override fun onHabilidadesInput(habilidades: List<Habilidad>) {
        viewModel.updateHabilidades(habilidades)
    }

    override fun handleDate(date: String) {
        viewModel.updateFechaNacimiento(date)
    }

}