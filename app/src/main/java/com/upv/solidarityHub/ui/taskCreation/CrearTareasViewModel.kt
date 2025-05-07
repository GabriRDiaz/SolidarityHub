package com.upv.solidarityHub.ui.taskCreation

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upv.solidarityHub.persistence.taskReq
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class CrearTareasViewModel : ViewModel(){

    var cat : String = "Cualquiera"
    var priority : String = "Cualquiera"
    var size : String = "Cualquiera"
    var schedule : String = "Cualquiera"
    var localTown : String = ""

    var townInList : Boolean = false

    private lateinit var localCoord : String
    lateinit var localCalendar : Calendar

    private var req: taskReq? = null


    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun updateCat(newCat: String){
        cat = newCat
    }

    fun updatePriority(newPriority: String){
        priority = newPriority
    }

    fun updateSize(newSize: String){
        size = newSize
    }

    fun updateSchedule(newSchedule : String) {
        schedule = newSchedule
    }

    fun updateCalendar(calendar: Calendar){
        localCalendar = calendar
    }

    fun validCoordinates(lat: Double?, long: Double?):Boolean{
        return lat != null && long != null
    }

    fun extractLongitude(input: String): Double? {
        val coordinateRegex = Regex("""(-?\d+(\.\d+)?)[,\s]+(-?\d+(\.\d+)?)""")
        val match = coordinateRegex.find(input)
        return if (match?.groupValues?.getOrNull(3) != null) {
            match.groupValues[3].toDoubleOrNull()
        } else {
            null
        }
    }

    fun extractLatitude(input: String): Double? {
        val coordinateRegex = Regex("""(-?\d+(\.\d+)?)[,\s]+(-?\d+(\.\d+)?)""")
        val match = coordinateRegex.find(input)
        return if (match?.groupValues?.getOrNull(1) != null) {
            match.groupValues[1].toDoubleOrNull()
        } else {
            null
        }
    }

    public suspend fun createTaskReq(): taskReq? {
        var cat : String?
        var priority : String?
        var size : String?
        var schedule : String?
        var town : String?
        var long : Double?
        var lat : Double?


        if(categorySelected()){
            cat = fetchCategory()
        }
        else{
            cat = null
        }

        if(prioritySelected()){
            priority = fetchPriority()
        }
        else{
            priority = null
        }

        if(sizeSelected()){
            size = fetchSize()
        }
        else{
            size = null
        }

        if(scheduleSelected()){
            schedule = fetchSchedule()
        }
        else{
            schedule = null
        }

        if(townSelected()){
            town = fetchLocalTown()
        }
        else{
            town = null
        }

        lat = localCoord?.let { extractLatitude(it) }
        long = localCoord?.let { extractLongitude(it) }

        if (!validCoordinates(lat, long)) {
            viewModelScope.launch {
                _eventFlow.emit(UiEvent.ShowToast("Formato de coordenadas incorrecto"))
            }
            return null
        }

        val unwrappedLat = lat!!
        val unwrappedLong = long!!

        req = taskReq.create(
            cat,
            town,
            priority,
            schedule,
            size,
            unwrappedLat,
            unwrappedLong,
            localCalendar,
            null
        )
        if(req != null){
            return req
        }
        return null

    }

    private fun fetchCategory():String{
        return cat
    }

    private fun fetchSchedule():String{
        return schedule
    }

    private fun fetchSize():String{
        return size
    }

    private fun fetchPriority():String{
        return priority
    }

    private fun prioritySelected():Boolean{
        return fetchPriority() != "Cualquiera"
    }

    private fun categorySelected():Boolean{
        return fetchCategory() != "Cualquiera"
    }

    private fun sizeSelected():Boolean{
        return fetchSize() != "Cualquiera"
    }

    private fun scheduleSelected():Boolean{
        return fetchSchedule() != "Cualquiera"
    }


    fun filtersSelected():Boolean{
        return categorySelected() || prioritySelected() || sizeSelected() || scheduleSelected() || isTownValid()
    }

    fun updateTown(town: String, towns: Array<String?>) {
        localTown = town
        townInList = towns.contains(town)
    }

    fun isTownValid(): Boolean {
        return townInList
    }

    fun townSelected(): Boolean{
        return localTown != ""
    }

    fun fetchLocalTown(): String{
        return localTown
    }

    fun updateCoord(coord: String) {
        localCoord = coord
    }

    fun dateInitialized(): Boolean{
        return ::localCalendar.isInitialized
    }

    fun coordInitialized(): Boolean{
        return ::localCoord.isInitialized
    }

    public fun buttonConditions():Boolean{
        return filtersSelected() && dateInitialized() && coordInitialized()
    }

    public fun getCoords() : String?{
        return localCoord
    }

    public fun getCal() : Calendar{
        return localCalendar
    }

    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }




}