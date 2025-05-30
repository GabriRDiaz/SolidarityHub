package com.upv.solidarityHub.ui.listaDesaparecidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upv.solidarityHub.persistence.model.Desaparecido
import kotlinx.coroutines.launch
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ListaDesaparecidosViewModel : ViewModel() {
    private val _desaparecidos = MutableStateFlow<List<Desaparecido>>(emptyList())
    val desaparecidos: StateFlow<List<Desaparecido>> = _desaparecidos.asStateFlow()

    private val _selectedDesaparecido = MutableStateFlow<Desaparecido?>(null)
    val selectedDesaparecido: StateFlow<Desaparecido?> = _selectedDesaparecido.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val supabaseAPI= SupabaseAPI()

    init {
        cargarDesaparecidos()
    }

    fun cargarDesaparecidos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Siempre asignamos una lista (vacía si hay error)
                _desaparecidos.value = supabaseAPI.getAllDesaparecidos() ?: emptyList()
            } catch (e: Exception) {
                _desaparecidos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun seleccionarDesaparecido(desaparecido: Desaparecido?) {
        _selectedDesaparecido.value = desaparecido
    }

    fun eliminarDesaparecidoSeleccionado() {
        val selected = _selectedDesaparecido.value
        if (selected != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    supabaseAPI.eliminarDesaparecido(selected.nombre, selected.apellidos)

                    // Actualizar la lista y limpiar selección
                    _desaparecidos.value = _desaparecidos.value.filter {
                        it.nombre != selected.nombre || it.apellidos != selected.apellidos
                    }

                    // Limpiar selección después de eliminar
                    _selectedDesaparecido.value = null
                } catch (e: Exception) {
                    // Manejar error
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }
}