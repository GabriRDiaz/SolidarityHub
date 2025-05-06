package com.upv.solidarityHub.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Desaparecido
import kotlinx.coroutines.runBlocking

class LoginViewModel : ViewModel() {

    private val _correo = MutableLiveData<String>()
    private val _contrasena = MutableLiveData<String>()

    private val _correoIsValid = MutableLiveData<Boolean>(true)
    private val _contrasenaIsValid = MutableLiveData<Boolean>(true)
    private val _allIsValid = MutableLiveData<Boolean>(false)

    val correo: LiveData<String> get() = _correo
    val contrasena: LiveData<String> get() = _contrasena

    val correoIsValid: LiveData<Boolean> get() = _correoIsValid
    val contrasenaIsValid: LiveData<Boolean> get() = _contrasenaIsValid
    val allIsValid: LiveData<Boolean> get() = _allIsValid


    fun updateCorreo(newCorreo: String) {
        _correo.value = newCorreo
        checkCorreoIsValid()
        checkAllValid()
    }

    fun updateContrasena(newContrasena: String) {
        _contrasena.value = newContrasena
        checkContrasenaIsValid()
        checkAllValid()
    }

    fun checkCorreoIsValid(): Boolean {
        var res = _correo.value != null && _correo.value != ""
        _correoIsValid.value = res
        return res
    }

    fun checkContrasenaIsValid(): Boolean {
        var res = _contrasena.value != null && _contrasena.value != ""
        _contrasenaIsValid.value = res
        return res
    }

    fun checkAllValid(): Boolean {
        val res = _correoIsValid.value!! && _contrasenaIsValid.value!!
        _allIsValid.value = res
        return res
    }
}
