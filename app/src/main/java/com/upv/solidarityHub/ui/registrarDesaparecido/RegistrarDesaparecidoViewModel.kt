package com.upv.solidarityHub.ui.registrarDesaparecido

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Desaparecido
import kotlinx.coroutines.runBlocking

class RegistrarDesaparecidoViewModel : ViewModel() {

    private val _nombre = MutableLiveData<String>()
    private val _apellidos = MutableLiveData<String>()
    private val _edad = MutableLiveData<Int>()
    private val _altura = MutableLiveData<Double>()
    private val _complexion = MutableLiveData<Int>()
    private val _sexo = MutableLiveData<Int>()

    private val _nombreIsValid = MutableLiveData<Boolean>(true)
    private val _apellidosIsValid = MutableLiveData<Boolean>(true)
    private val _edadIsValid = MutableLiveData<Boolean>(true)
    private val _alturaIsValid = MutableLiveData<Boolean>(true)
    private val _complexionIsValid = MutableLiveData<Boolean>(false)
    private val _sexoIsValid = MutableLiveData<Boolean>(false)
    private val _allIsValid = MutableLiveData<Boolean>(false)

    val nombre: LiveData<String> get() = _nombre
    val apellidos: LiveData<String> get() = _apellidos
    val edad: LiveData<Int> get() = _edad
    val altura: LiveData<Double> get() = _altura
    val complexion: LiveData<Int> get() = _complexion
    val sexo: LiveData<Int> get() = _sexo

    val nombreIsValid: LiveData<Boolean> get() = _nombreIsValid
    val apellidosIsValid: LiveData<Boolean> get() = _apellidosIsValid
    val edadIsValid: LiveData<Boolean> get() = _edadIsValid
    val alturaIsValid: LiveData<Boolean> get() = _alturaIsValid
    val complexionIsValid: LiveData<Boolean> get() = _complexionIsValid
    val sexoIsValid: LiveData<Boolean> get() = _sexoIsValid
    val allIsValid: LiveData<Boolean> get() = _allIsValid


    fun updateNombre(newNombre: String) {
        _nombre.value = newNombre
        checkNombreIsValid()
        checkAllValid()
    }

    fun updateApellidos(newApellidos: String) {
        _apellidos.value = newApellidos
        checkApellidosIsValid()
        checkAllValid()
    }

    fun updateEdad(newEdad: Int) {
        _edad.value = newEdad
        checkEdadIsValid()
        checkAllValid()
    }

    fun updateAltura(newAltura: Double) {
        _altura.value = newAltura
        checkAlturaIsValid()
        checkAllValid()
    }

    fun updateComplexion(newComplexion: Int) {
        _complexion.value = newComplexion
        checkComplexionIsValid()
        checkAllValid()
    }

    fun updateSexo(newSexo: Int) {
        _sexo.value = newSexo
        checkSexoIsValid()
        checkAllValid()
    }


    fun checkNombreIsValid(): Boolean {
        var res = _nombre.value != ""
        _nombreIsValid.value = res
        return res
    }

    fun checkApellidosIsValid(): Boolean {
        var res = apellidos.value != ""
        _apellidosIsValid.value = res
        return res
    }

    fun checkEdadIsValid(): Boolean {
        val res = _edad.value != null && _edad.value!! >= 0
        _edadIsValid.value = res
        return res
    }

    fun checkAlturaIsValid(): Boolean {
        val res = _altura.value != null && _altura.value!! >= 0
        _alturaIsValid.value = res
        return res
    }

    fun checkComplexionIsValid(): Boolean {
        val res = _complexion.value != null
        _complexionIsValid.value = res
        return res
    }

    fun checkSexoIsValid(): Boolean {
        val res = _sexo.value != null
        _sexoIsValid.value = res
        return res
    }

    fun checkAllValid(): Boolean {
        val res = _nombreIsValid.value!! && _alturaIsValid.value!! && _edadIsValid.value!! && _apellidosIsValid.value!! && _complexionIsValid.value!! && _sexoIsValid.value!!
        _allIsValid.value = res
        return res
    }

    fun registrarDesaparecido(): Boolean {
        if(allIsValid.value!!) {
            val desaparecido = Desaparecido(_nombre.value!!, _apellidos.value!!, _edad.value!!, _altura.value!!, _complexion.value!!, _sexo.value!!, null)
            runBlocking {
                try {
                    SupabaseAPI().registerDesaparecido(desaparecido)
                    return@runBlocking true
                } catch (e: Exception) {
                    Log.d("DEBUG",e.toString())
                    //TODO: ADD ERROR HANDLING
                    return@runBlocking false
                }
            }
        }
        return false
    }
}
