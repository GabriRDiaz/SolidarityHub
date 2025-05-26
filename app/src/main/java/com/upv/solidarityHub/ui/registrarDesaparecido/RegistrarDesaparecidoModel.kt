package com.upv.solidarityHub.ui.registrarDesaparecido

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Desaparecido
import kotlinx.coroutines.runBlocking

class RegistrarDesaparecidoModel {
    private val max_char = 50
    private val max_altura = 300
    private val max_edad = 120

    val _nombre = MutableLiveData<String>("")
    val _apellidos = MutableLiveData<String>("")
    val _edad = MutableLiveData<Int>(-1)
    val _altura = MutableLiveData<Double>(-1.0)
    val _complexion = MutableLiveData<Int>(1)
    val _sexo = MutableLiveData<Int>(-1)
    val _ultimaUbiDesaparecido = MutableLiveData<Baliza?>(null)


    val _nombreIsValid = MutableLiveData<Boolean>(true)
    val _apellidosIsValid = MutableLiveData<Boolean>(true)
    val _edadIsValid = MutableLiveData<Boolean>(true)
    val _alturaIsValid = MutableLiveData<Boolean>(true)
    val _complexionIsValid = MutableLiveData<Boolean>(false)
    val _sexoIsValid = MutableLiveData<Boolean>(false)
    val _allIsValid = MutableLiveData<Boolean>(false)

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

    fun updateUltimaUbiDesaparecido(newUbi: Baliza?) {
        _ultimaUbiDesaparecido.value = newUbi
    }

    fun clearAllData() {
        updateNombre("")
        updateApellidos("")
        updateEdad(-1)
        updateAltura(-1.0)
        updateComplexion(1)
        updateSexo(-1)
        updateUltimaUbiDesaparecido(null)

        _nombreIsValid.value = true
        _apellidosIsValid.value = true
        _edadIsValid.value = true
        _alturaIsValid.value = true
        _complexionIsValid.value = false
        _sexoIsValid.value = false
        _allIsValid.value = false

    }

    fun checkNombreIsValid(): Boolean {
        val nombre = _nombre.value!!
        val res = nombre != "" && nombre.length <= max_char && hasNoSpecialChar(nombre)
        _nombreIsValid.value = res
        return res
    }

    fun hasNoSpecialChar(text:String): Boolean {
        return text.all { it.isLetter() || it in " " }
    }

    fun checkApellidosIsValid(): Boolean {
        val apellidos = _apellidos.value!!
        val res = apellidos != "" && apellidos.length <= max_char && hasNoSpecialChar(apellidos)
        _apellidosIsValid.value = res
        return res
    }

    fun checkEdadIsValid(): Boolean {
        val edad = _edad.value
        val res = edad != null && edad >= 0 && edad <= max_edad
        _edadIsValid.value = res
        return res
    }

    fun checkAlturaIsValid(): Boolean {
        val altura = _altura.value!!
        val res = altura in 0.0..300.0
        _alturaIsValid.value = res
        return res
    }

    fun checkComplexionIsValid(): Boolean {
        val complexion = _complexion.value
        val res = complexion != -1
        _complexionIsValid.value = res
        return res
    }

    fun checkSexoIsValid(): Boolean {
        val sexo = _sexo.value
        val res = sexo != -1
        _sexoIsValid.value = res
        return res
    }

    fun checkAllValid(): Boolean {
        val res = _nombreIsValid.value!! && _alturaIsValid.value!! && _edadIsValid.value!! && _apellidosIsValid.value!! && _complexionIsValid.value!! && _sexoIsValid.value!!
        _allIsValid.value = res
        return res
    }

    fun registrarDesaparecido(): Boolean {
        var succesfulRegistry = false
        if(_allIsValid.value!!) {
            var ultimaUbi = _ultimaUbiDesaparecido.value
            val desaparecido = Desaparecido(_nombre.value!!, _apellidos.value!!, _edad.value!!, _altura.value!!, _complexion.value!!, _sexo.value!!, null)
            if(ultimaUbi != null) {
                ultimaUbi.tipo = "Desaparecido"
                ultimaUbi.nombre = _nombre.value!! + _apellidos.value!!
                ultimaUbi.descripcion = "Última ubicación del desaparecido"
                desaparecido.id_baliza_visto_por_ultima_vez = ultimaUbi.id
            }
            runBlocking {
                try {
                    SupabaseAPI().registerDesaparecido(desaparecido,ultimaUbi)
                    succesfulRegistry = true
                } catch (e: Exception) {
                    Log.d("DEBUG",e.toString())
                    //TODO: ADD ERROR HANDLING
                }
            }
        }
        return succesfulRegistry
    }
}