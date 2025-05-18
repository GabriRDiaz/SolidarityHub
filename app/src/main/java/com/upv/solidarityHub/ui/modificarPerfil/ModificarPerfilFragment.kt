package com.upv.solidarityHub.ui.modificarPerfil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.ActivityRegistroBinding
import com.upv.solidarityHub.persistence.FileReader
import com.upv.solidarityHub.utils.municipioSpinner.SuggestionAdapter
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ModificarPerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ModificarPerfilFragment : Fragment() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityRegistroBinding

    private lateinit var buscadorMunicipio: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var suggestionAdapter: SuggestionAdapter
    private lateinit var showPassButton: Button
    private lateinit var showRepPassButton: Button
    private lateinit var registrarseButton: Button
    private lateinit var botonIrLogearse: Button

    private lateinit var nombreField: TextInputLayout
    private lateinit var apellidosField: TextInputLayout
    private lateinit var correoField: TextInputLayout
    private lateinit var displayNacimiento: TextView
    private lateinit var contrasenaField: TextInputLayout
    private lateinit var repContrasenaField: TextInputLayout

    private lateinit var viewModel: ModificarPerfilViewModel
    private lateinit var myView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_modificar_perfil, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ModificarPerfilViewModel::class.java)

        initializeFields()
        addItemsToSpinnerComplexion()
        loadDataFromModel()
        initializeButtons()
        initializeListeners()
        initializeObservers()


        return myView
    }

    private fun initializeSearchView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerModMunicipio)

        suggestionAdapter = SuggestionAdapter(emptyList()) { suggestion ->
            buscadorMunicipio.setQuery(suggestion, true)
            suggestionAdapter.updateSuggestions(emptyList())
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = suggestionAdapter


        try {
            val assetManager = requireContext().assets
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

    }


}