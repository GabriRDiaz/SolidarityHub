package com.upv.solidarityHub.ui.taskCreation.ListaTask

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.database.SupabaseAPI.taskDB
import kotlinx.coroutines.launch

class TareasListViewModel(private val supabaseAPI: SupabaseAPI) : ViewModel() {

    private val _tareas = MutableLiveData<List<taskDB>>()
    val tareas: LiveData<List<taskDB>> get() = _tareas

    private val _tareaSeleccionada = MutableLiveData<taskDB?>()
    val tareaSeleccionada: LiveData<taskDB?> get() = _tareaSeleccionada

    fun cargarTareas() {
        viewModelScope.launch {
            val datos = supabaseAPI.getAllTareas() ?: emptyList()
            _tareas.postValue(datos)
        }
    }

    fun eliminarTarea(id: Int) {
        viewModelScope.launch {
            if (supabaseAPI.eliminarTarea(id)) {
                cargarTareas()
            }
        }
    }

    fun seleccionarTarea(tarea: taskDB) {
        _tareaSeleccionada.postValue(tarea)
    }
}

class TareasListViewModelFactory(
    private val supabaseAPI: SupabaseAPI
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TareasListViewModel(supabaseAPI) as T
    }
}