package com.upv.solidarityHub.ui.modificarPerfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.model.Habilidad

class ModificarPerfilViewModel : ViewModel() {
    private val model = ModificarPerfilModel()

    val nombre: LiveData<String> get() = model._nombre
    val apellidos: LiveData<String> get() = model._apellidos
    val contrasena: LiveData<String> get() = model._contrasena
    val municipio: LiveData<String> get() = model._municipio
    val oldContrasena: LiveData<String> get() = model._oldContrasena
    val fechaNacimiento: LiveData<String> get() = model._fechaNacimiento
    val habilidades: LiveData<List<Habilidad>?> get() = model._habilidades

    val municipios: LiveData<Array<String?>> get() = model._municipios


    val nombreIsValid: LiveData<Boolean> get() = model._nombreIsValid
    val apellidosIsValid: LiveData<Boolean> get() = model._apellidosIsValid
    val contrasenaIsValid: LiveData<Boolean> get() = model._contrasenaIsValid
    val municipioIsValid: LiveData<Boolean> get() = model._municipioIsValid
    val oldContrasenaIsValid: LiveData<Boolean> get() = model._oldContrasenaIsValid
    val fechaNacimientoIsValid: LiveData<Boolean> get() = model._fechaNacimientoIsValid
    val allIsValid: LiveData<Boolean> get() = model._allIsValid

    fun updateMunicipiosList(newMunicipio: Array<String?>) {
        model._municipios.value = newMunicipio
    }

    fun updateNombre(newNombre: String) {
        model._nombre.value = newNombre
    }

    fun updateApellidos(newApellidos: String) {
        model._apellidos.value = newApellidos
    }

    fun updateContrasena(newContrasena: String) {
        model._contrasena.value = newContrasena
    }

    fun updateMunicipio(newMunicipio: String) {
        model._municipio.value = newMunicipio
    }

    fun updateOldContrasena(newOldContrasena: String) {
        model._oldContrasena.value = newOldContrasena
    }

    fun updateFechaNacimiento(newFechaNacimiento: String) {
        model._fechaNacimiento.value = newFechaNacimiento
    }

    fun updateHabilidades(newHabilidades: List<Habilidad>) {
        model._habilidades.value = newHabilidades
    }

    fun confirmar(): Boolean {
        return model.confirmarModificacion()
    }

    fun setOriginalUserValues() {
        model.setOriginalUsuarioValues()
    }


}