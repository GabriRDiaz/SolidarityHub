package com.upv.solidarityHub.ui.listaRecursos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upv.solidarityHub.persistence.Baliza
import kotlinx.coroutines.launch
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListaRecursosViewModel : ViewModel() {
    private val _balizas = MutableStateFlow<List<Baliza>>(emptyList())
    val balizas: StateFlow<List<Baliza>> = _balizas.asStateFlow()

    private val _selectedBaliza = MutableStateFlow<Baliza?>(null)
    val selectedBaliza: StateFlow<Baliza?> = _selectedBaliza.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val supabaseAPI= SupabaseAPI()

    init {
        cargarBalizas()
    }

    fun cargarBalizas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _balizas.value = supabaseAPI.getAllBalizas() ?: emptyList()
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun seleccionarBaliza(baliza: Baliza?) {
        _selectedBaliza.value = baliza
    }

    fun eliminarBalizaSeleccionada() {
        val selected = _selectedBaliza.value
        if (selected != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    supabaseAPI.eliminarBaliza(selected.id)
                    // Actualizar la lista
                    _balizas.value = _balizas.value.filter { it.id != selected.id }
                    // Limpiar selección
                    _selectedBaliza.value = null
                } catch (e: Exception) {
                    // Manejar error
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}