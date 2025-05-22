package com.upv.solidarityHub.ui.registro

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.DatabaseAPI
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Desaparecido
import com.upv.solidarityHub.ui.components.DatePicker.DatePickerFragment
import com.upv.solidarityHub.ui.components.DatePicker.DatePickerHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import com.upv.solidarityHub.utils.strategy.*

class RegistroViewModel : ViewModel() {

    private val model: RegistroModel = RegistroModel()

    val correo: LiveData<String> get() = model._correo
    val nombre: LiveData<String> get() = model._nombre
    val apellidos: LiveData<String> get() = model._apellidos
    val contrasena: LiveData<String> get() = model._apellidos
    val repContrasena: LiveData<String> get() = model._contrasena
    val fechaNacimiento: LiveData<String> get() = model._fechaNacimiento
    val municipio: LiveData<String> get() = model._municipio
    val municipios: LiveData<Array<String?>> get() = model._municipios
    val toastMessage: LiveData<String> get() = model._toastMessage

    val correoIsValid: LiveData<Boolean> get() = model._correoIsValid
    val nombreIsValid: LiveData<Boolean> get() = model._nombreIsValid
    val apellidosIsValid: LiveData<Boolean> get() = model._apellidosIsValid
    val contrasenaIsValid: LiveData<Boolean> get() = model._contrasenaIsValid
    val repContrasenaIsValid: LiveData<Boolean> get() = model._repContrasenaIsValid
    val municipioIsValid: LiveData<Boolean> get() = model._municipioIsValid
    val fechaNacimientoIsValid: LiveData<Boolean> get() = model._fechaNacimientoIsValid
    val allIsValid: LiveData<Boolean> get() = model._allIsValid
    val registryFinalized: LiveData<Boolean> get() = model._registryFinalized

    fun updateCorreo(correo: String) {
        model.updateCorreo(correo)
    }

    fun updateNombre(nombre: String) {
        model.updateNombre(nombre)
    }

    fun updateApellidos(apellidos: String) {
        model.updateApellidos(apellidos)
    }

    fun updateContrasena(contrasena: String) {
        model.updateContrasena(contrasena)
    }

    fun updateRepContrasena(repContrasena: String) {
        model.updateRepContrasena(repContrasena)
    }

    fun updateFechaNacimiento(fecha: String) {
        model.updateFechaNacimiento(fecha)
    }

    fun updateMunicipio(municipio: String) {
        model.updateMunicipio(municipio)
    }

    fun updateMunicipiosList(municipios: Array<String?>) {
        model.updateMunicipiosList(municipios)
    }

    fun registrarse() {
        model.registrarse()
    }

}
