package com.upv.solidarityHub.ui.registrarDesaparecido

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.upv.solidarityHub.MapaDesaparecidos
import com.upv.solidarityHub.MapaGenerico
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.ui.components.selectUbiMap.SelectUbiMapDialogFragment
import com.upv.solidarityHub.utils.TextInputLayoutUtils.setErrorTo
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt

class RegistrarDesaparecidoFragment : Fragment(), SelectUbiMapDialogFragment.DialogListener {

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


    override fun onDialogUbi(baliza: Baliza) {
        viewModel.updateUltimaUbiDesaparecido(baliza)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        myView = inflater.inflate(R.layout.fragment_registrar_desaparecido, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(RegistrarDesaparecidoViewModel::class.java)

        initializeFields()
        addItemsToSpinnerComplexion()
        loadDataFromModel()
        initializeButtons()
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

        buttonMapa.setOnClickListener {
            val dialogFragment: SelectUbiMapDialogFragment = SelectUbiMapDialogFragment()
            dialogFragment.show(childFragmentManager, "Introduzca la ubicación aproximada")
        }

        buttonConfirmar.setOnClickListener {
            if(viewModel.registrarDesaparecido()) {
                Toast.makeText(activity, "Registrado con éxito", Toast.LENGTH_SHORT).show()
                clearAllFields()
                findNavController().popBackStack()
            }
        }

        buttonCancelar. setOnClickListener {
            clearAllFields()
            findNavController().popBackStack()
        }

    }

    private fun initializeObservers() {
        viewModel.nombreIsValid.observe(viewLifecycleOwner, Observer { newNombreIsValid ->
            inputNombre.setErrorTo("Por favor introduzca un Nombre válido", !newNombreIsValid)
        })

        viewModel.apellidosIsValid.observe(viewLifecycleOwner, Observer { newApellidosIsValid ->
            inputApellidos.setErrorTo("Por favor introduzca Apellidos válidos", !newApellidosIsValid)
        })

        viewModel.edadIsValid.observe(viewLifecycleOwner, Observer { newEdadIsValid ->
            inputEdad.setErrorTo("Por favor añada una edad válida", !newEdadIsValid)
        })

        viewModel.alturaIsValid.observe(viewLifecycleOwner, Observer { newAlturaIsValid ->
            inputAltura.setErrorTo("Por favor una altura válida", !newAlturaIsValid)
        })

        viewModel.allIsValid.observe(viewLifecycleOwner, Observer { newAllIsValid ->
            buttonConfirmar.isEnabled = newAllIsValid
        })
    }

    private fun addItemsToSpinnerComplexion() {
        val items = listOf("Pequeña", "Normal", "Grande")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputComplexion.adapter = adapter
    }

    private fun loadDataFromModel() {
            inputNombre.editText!!.setText(viewModel.nombre.value.toString())
            inputApellidos.editText!!.setText(viewModel.apellidos.value.toString())
            if(viewModel.edad.value!! != -1) inputEdad.editText!!.setText(viewModel.edad.value!!.toString())
            if(viewModel.altura.value!! != -1.0) inputAltura.editText!!.setText(viewModel.altura.value!!.toString())
            if(viewModel.complexion.value!! != -1) {
                inputComplexion.setSelection(viewModel.complexion.value!!)
            } else {inputComplexion.setSelection(1)}

            if(viewModel.sexo.value!! != -1) {
                ((inputSexo.getChildAt(viewModel.sexo.value!!)) as RadioButton).isChecked = true
            }

    }

    private fun clearAllFields() {
        inputNombre.editText!!.setText("")
        inputApellidos.editText!!.setText("")
        inputEdad.editText!!.setText("")
        inputAltura.editText!!.setText("")
        inputComplexion.setSelection(1)
        ((inputSexo.getChildAt(0)) as RadioButton).isChecked = false
        ((inputSexo.getChildAt(1)) as RadioButton).isChecked = false
        ((inputSexo.getChildAt(2)) as RadioButton).isChecked = false
        viewModel.clearAllData()
    }
}