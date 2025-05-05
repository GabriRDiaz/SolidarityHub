package com.upv.solidarityHub.ui.registrarDesaparecido

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.upv.solidarityHub.R
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt


class RegistrarDesaparecidoFragment : Fragment() {

    companion object {
        fun newInstance() = RegistrarDesaparecidoFragment()
    }

    private lateinit var viewModel: RegistrarDesaparecidoViewModel

    private lateinit var inputNombre: TextInputLayout
    private lateinit var inputApellidos: TextInputLayout
    private lateinit var inputEdad: TextInputLayout
    private lateinit var inputAltura: TextInputLayout
    private lateinit var inputComplexion: Spinner
    private lateinit var inputSexo: RadioGroup
    private lateinit var myView: View

    private lateinit var buttonConfirmar: Button
    private lateinit var buttonCancelar: Button
    private lateinit var buttonMapa: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myView = inflater.inflate(R.layout.fragment_registrar_desaparecido, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(RegistrarDesaparecidoViewModel::class.java)

        initializeFields()
        initializeButtons()
        addItemsSpinnerComplexion()
        initializeListeners()
        initializeObservers()

        return myView
    }

    private fun initializeFields() {
        inputNombre = myView.findViewById<TextInputLayout>(R.id.inputNombreDesaparecido)
        inputApellidos = myView.findViewById<TextInputLayout>(R.id.inputApellidosDesaparecido)
        inputEdad = myView.findViewById<TextInputLayout>(R.id.inputEdadDesaparecido)
        inputAltura = myView.findViewById<TextInputLayout>(R.id.inputAlturaDesaparecido)
        inputComplexion = myView.findViewById<Spinner>(R.id.spinnerComplexionDesaparecido)
        inputSexo = myView.findViewById<RadioGroup>(R.id.radioGroupSexo)
    }

    private fun initializeButtons() {
        buttonConfirmar = myView.findViewById<Button>(R.id.botonConfirmarDesaparecido)
        buttonCancelar = myView.findViewById<Button>(R.id.botonCancelarDesaparecido)
        buttonMapa = myView.findViewById<Button>(R.id.botonMapaDesaparecido)
    }

    private fun initializeListeners() {
        inputNombre.editText!!.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.updateNombre(inputNombre.editText!!.text.toString())
            }

        })

        inputApellidos.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateApellidos(inputApellidos.editText!!.text.toString())
            }
        })

        inputApellidos.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateApellidos(inputApellidos.editText!!.text.toString())
            }
        })

        inputEdad.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                var value = inputEdad.editText!!.text.toString()
                var edad = -1
                if(value != "") edad = parseInt(value)
                viewModel.updateEdad(edad)
            }
        })

        inputAltura.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                var value = inputAltura.editText!!.text.toString()
                var altura = -1.0
                if(value != "") altura = parseDouble(value)
                viewModel.updateAltura(altura)
            }
        })

        inputComplexion.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                viewModel.updateComplexion(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })

        inputSexo.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            val id = inputSexo.checkedRadioButtonId
            val selectedItemText = myView.findViewById<RadioButton>(id).text.toString()
            var index = -1
            when (selectedItemText) {
                "Hombre" -> index = 0
                "Mujer"  -> index = 1
                "Otro"   -> index = 2
            }
            if(index != -1) viewModel.updateSexo(index)
        })

        buttonConfirmar.setOnClickListener {
            viewModel.registrarDesaparecido()
        }

    }

    private fun initializeObservers() {
        viewModel.nombreIsValid.observe(viewLifecycleOwner, Observer { newNombreIsValid ->
            inputNombre.editText!!.error = "Por favor introduzca un Nombre"
            inputNombre.isErrorEnabled = !newNombreIsValid
            if (newNombreIsValid) inputNombre.editText!!.error = null
        })

        viewModel.apellidosIsValid.observe(viewLifecycleOwner, Observer { newApellidosIsValid ->
            inputApellidos.editText!!.error = "Por favor introduzca los Apellidos"
            inputApellidos.isErrorEnabled = !newApellidosIsValid
            if (newApellidosIsValid) inputApellidos.editText!!.error = null
        })

        viewModel.apellidosIsValid.observe(viewLifecycleOwner, Observer { newApellidosIsValid ->
            inputApellidos.editText!!.error = "Por favor introduzca los Apellidos"
            inputApellidos.isErrorEnabled = !newApellidosIsValid
            if (newApellidosIsValid) inputApellidos.editText!!.error = null
        })

        viewModel.edadIsValid.observe(viewLifecycleOwner, Observer { newEdadIsValid ->
            inputEdad.editText!!.error = "Por favor introduzca una edad válida"
            inputEdad.isErrorEnabled = !newEdadIsValid
            if (newEdadIsValid) inputEdad.editText!!.error = null
        })

        viewModel.alturaIsValid.observe(viewLifecycleOwner, Observer { newAlturaIsValid ->
            inputAltura.editText!!.error = "Por favor una altura válida"
            inputAltura.isErrorEnabled = !newAlturaIsValid
            if (newAlturaIsValid) inputAltura.editText!!.error = null
        })

        viewModel.allIsValid.observe(viewLifecycleOwner, Observer { newAllIsValid ->
        buttonConfirmar.isEnabled = newAllIsValid
        })

    }

    private fun addItemsSpinnerComplexion() {
        val items = listOf("Pequeña", "Normal", "Grande")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputComplexion.adapter = adapter
        inputComplexion.setSelection(1)
    }
}