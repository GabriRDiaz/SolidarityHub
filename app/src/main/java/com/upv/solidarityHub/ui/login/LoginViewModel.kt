package com.upv.solidarityHub.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Desaparecido
import kotlinx.coroutines.runBlocking

class LoginViewModel : ViewModel() {

    val model:LoginModel = LoginModel()

    val correo: LiveData<String> get() = model._correo
    val contrasena: LiveData<String> get() = model._contrasena

    val correoIsValid: LiveData<Boolean> get() = model._correoIsValid
    val contrasenaIsValid: LiveData<Boolean> get() = model._contrasenaIsValid
    val allIsValid: LiveData<Boolean> get() = model._allIsValid

    val goToMain: LiveData<Boolean> get() = model._goToMain

    fun updateCorreo(correo: String) {
        model.updateCorreo(correo)
    }

    fun updateContrasena(contrasena: String) {
        model.updateContrasena(contrasena)
    }

    fun login() {
        model.login()
    }

    fun setContext(context: Context) {
        model.setContext(context)
    }




}
