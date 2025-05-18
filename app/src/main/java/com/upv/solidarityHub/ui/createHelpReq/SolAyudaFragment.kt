package com.upv.solidarityHub.ui.createHelpReq

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.upv.solidarityHub.databinding.FragmentSolAyudaBinding
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.utils.municipioSpinner.SuggestionAdapter
import kotlinx.coroutines.launch
import java.io.IOException

class SolAyudaFragment : Fragment() {

    private var _binding: FragmentSolAyudaBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SolAyudaViewModel

    private lateinit var inputTextTitle: TextInputEditText
    private lateinit var inputTextDesc: TextInputEditText

    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var towns: Array<String?>
    private var searchSuggestions = arrayOfNulls<String>(FileReader.numMunicipios)

    private val categories = arrayOf("Limpieza", "Recogida de comida", "Reconstrucción", "Primeros auxilios", "Artículos para bebés", "Asistencia a mayores", "Asistencia a discapacitados", "Artículos de primera necesidad", "Otros", "Transporte", "Cocina", "Mascotas")
    private val hours = arrayOf("Manaña Temprana (6:00 - 9:00)", "Mañana (9:00 - 12:00)", "Mediodía (12:00 - 15:00)", "Tarde (15:00 - 18:00)", "Noche Temprana (18:00 - 21:00)", "Noche (21:00 - 00:00)", "Madrugada (00:00 - 6:00)")
    private val groupSize = arrayOf("Pequeña (5 voluntarios máx.)", "Media (15 voluntarios máx.)", "Grande (15+ voluntarios)")
    private val urgenciaList = arrayOf("Baja", "Media", "Alta")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSolAyudaBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(requireActivity()).get(SolAyudaViewModel::class.java)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputTextTitle = binding.textInputTitle
        inputTextDesc = binding.textInputDescription

        setSpinners()
        setListeners()
        setLocationUI()
        binding.buttonOK.isEnabled = false
    }


    private fun buttonConditions() {
        binding.buttonOK.isEnabled = viewModel.buttonConditions(towns)
    }



    private fun setListeners() {
        inputTextTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateTitleText(inputTextTitle.text.toString())
                buttonConditions()
                if (viewModel.nullTitle()) inputTextTitle.error = "Por favor, añada un título"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputTextDesc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateDescription(inputTextDesc.text.toString())
                buttonConditions()
                if (viewModel.nullDescription()) inputTextDesc.error = "Por favor, añada una descripcion"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.buttonOK.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.createRequest(binding.CatSpinner.selectedItem.toString(), binding.HourSpinner.selectedItem.toString(),
                    binding.SizeSpinner.selectedItem.toString(), binding.spinnerUrg.selectedItem.toString())
                Toast.makeText(requireContext(), "Se ha registrado la solicitud", Toast.LENGTH_LONG).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.buttonCancelar.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setSpinners() {
        fun setupSpinner(spinnerId: android.widget.Spinner, items: Array<String>) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerId.adapter = adapter
        }

        setupSpinner(binding.CatSpinner, categories)
        setupSpinner(binding.HourSpinner, hours)
        setupSpinner(binding.SizeSpinner, groupSize)
        setupSpinner(binding.spinnerUrg, urgenciaList)
    }

    private fun setLocationUI() {
        binding.SearchViewLocations.setOnKeyListener { _, _, _ ->
            if (viewModel.checkValidTown(towns)) buttonConditions()
            false
        }

        binding.SearchViewLocations.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && !viewModel.checkValidTown(towns)) buttonConditions()
        }

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            binding.SearchViewLocations.setQuery(suggestion, true)
            buttonConditions()
            suggestionAdapter.updateSuggestions(emptyList())
        }

        binding.RecyclerViewLocations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = suggestionAdapter
        }

        try {
            towns = requireContext().assets.open("municipios").use { FileReader.readMunicipiosToArray(it) }
            searchSuggestions = towns
        } catch (e: IOException) {
            Log.e("SolAyudaFragment", "Error loading towns", e)
            towns = emptyArray()
            searchSuggestions = emptyArray()
        }

        binding.SearchViewLocations.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = searchSuggestions.filter {
                    it?.contains(newText.orEmpty(), ignoreCase = true) ?: false
                }
                suggestionAdapter.updateSuggestions(filtered)

                viewModel.updateTown(binding.SearchViewLocations.query.toString())

                buttonConditions()

                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
