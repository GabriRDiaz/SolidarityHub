package com.upv.solidarityHub.ui.modificarPerfil

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.Usuario
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
        model.updateNombre(newNombre)
    }

    fun updateApellidos(newApellidos: String) {
        model.updateApellidos(newApellidos)
    }

    fun updateContrasena(newContrasena: String) {
        model.updateContrasena(newContrasena)
    }

    fun updateMunicipio(newMunicipio: String) {
        model.updateMunicipio(newMunicipio)
    }

    fun updateOldContrasena(newOldContrasena: String) {
        model.updateOldContrasena(newOldContrasena)
    }

    fun updateFechaNacimiento(newFechaNacimiento: String) {
        model.updateFechaNacimiento(newFechaNacimiento)
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

    fun setOriginalUsuario(usuario: Usuario) {
        model.setOriginalUsuario(usuario)
    }


}