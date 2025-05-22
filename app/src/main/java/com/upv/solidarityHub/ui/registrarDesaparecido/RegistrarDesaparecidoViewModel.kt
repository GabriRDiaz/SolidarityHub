package com.upv.solidarityHub.ui.registrarDesaparecido

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Desaparecido
import com.upv.solidarityHub.ui.login.LoginModel
import kotlinx.coroutines.runBlocking

class RegistrarDesaparecidoViewModel : ViewModel() {

    val model: RegistrarDesaparecidoModel = RegistrarDesaparecidoModel()

    val nombre: LiveData<String> get() = model._nombre
    val apellidos: LiveData<String> get() = model._apellidos
    val edad: LiveData<Int> get() = model._edad
    val altura: LiveData<Double> get() = model._altura
    val complexion: LiveData<Int> get() = model._complexion
    val sexo: LiveData<Int> get() = model._sexo
    val ultimaUbiDesaparecido: LiveData<Baliza?> get() = model._ultimaUbiDesaparecido


    val nombreIsValid: LiveData<Boolean> get() = model._nombreIsValid
    val apellidosIsValid: LiveData<Boolean> get() = model._apellidosIsValid
    val edadIsValid: LiveData<Boolean> get() =  model._edadIsValid
    val alturaIsValid: LiveData<Boolean> get() = model._alturaIsValid
    val complexionIsValid: LiveData<Boolean> get() = model._complexionIsValid
    val sexoIsValid: LiveData<Boolean> get() = model._sexoIsValid
    val allIsValid: LiveData<Boolean> get() = model._allIsValid

    fun clearAllData() {
        model.clearAllData()
    }

    fun registrarDesaparecido(): Boolean {
        return model.registrarDesaparecido()
    }

    fun updateNombre(nombre: String) {
        model.updateNombre(nombre)
    }

    fun updateApellidos(apellidos: String) {
        model.updateApellidos(apellidos)
    }

    fun updateAltura(altura: Double) {
        model.updateAltura(altura)
    }

    fun updateEdad(edad: Int) {
        model.updateEdad(edad)
    }

    fun updateSexo(sexo: Int) {
        model.updateSexo(sexo)
    }

    fun updateComplexion(complexion: Int) {
        model.updateComplexion(complexion)
    }

    fun updateUltimaUbiDesaparecido(baliza: Baliza) {
        model.updateUltimaUbiDesaparecido(baliza)
    }









}
