package com.upv.solidarityHub.ui.createHelpReq

import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.SolicitudAyuda

class SolAyudaViewModel : ViewModel() {
    private var currentTitle: String = ""
    private var currentDescription: String = ""
    private var currentTown: String = ""


    fun nullTitle(): Boolean{
        return getTitleText().isEmpty()
    }

    fun getTitleText(): String{
        return currentTitle
    }

    fun updateTitleText(newTitle : String){
        currentTitle = newTitle
    }

    fun nullDescription(): Boolean{
        return getDescription().isEmpty()
    }

    fun getDescription(): String{
        return currentDescription
    }

    fun updateDescription(newDescription : String){
        currentDescription = newDescription
    }

    fun checkValidTown(towns: Array<String?>): Boolean{
        return towns.contains(getTown())
    }

    fun getTown(): String{
        return currentTown
    }

    fun updateTown(newTown : String){
        currentTown = newTown
    }

    fun buttonConditions(towns: Array<String?>) : Boolean{
        return !nullTitle() && !nullDescription() && checkValidTown(towns)
    }

    suspend fun createRequest(cat: String, hours: String, size: String, urg: String) {
        SolicitudAyuda.create(
            currentTitle,
            currentDescription,
            cat,
            currentTown,
            hours,
            size,
            urg
        )
    }

}