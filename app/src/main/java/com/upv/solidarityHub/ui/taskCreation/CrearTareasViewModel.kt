package com.upv.solidarityHub.ui.taskCreation

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upv.solidarityHub.assignationAlgorythm
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
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

    private val requiredHabilityList = listOf(
        "Reconstrucción",
        "Primeros auxilios",
        "Asistencia a mayores",
        "Asistencia a discapacitados",
        "Transporte"
    )

    var townInList : Boolean = false

    private lateinit var localCoord : String
    lateinit var localStartCalendar : Calendar
    lateinit var localEndCalendar : Calendar

    private var req: taskReq? = null


    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val db = SupabaseAPI()

    var currentTaskIndex = 0
    var isEditing = false
    lateinit var taskIdList: List<Int>

    private val _failedAssignments = mutableListOf<Int>()
    val failedAssignments: List<Int> get() = _failedAssignments


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

    fun updateStartAndEndCalendar(startCalendar: Calendar, endCalendar: Calendar){
        localStartCalendar = startCalendar
        localEndCalendar = endCalendar
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
            localStartCalendar,
            localEndCalendar,
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

    fun startDateInitialized(): Boolean{
        return ::localStartCalendar.isInitialized
    }

    fun finalDateInitialized(): Boolean{
        return ::localEndCalendar.isInitialized
    }

    fun coordInitialized(): Boolean{
        return ::localCoord.isInitialized
    }

    fun dateInitialized(): Boolean{
        return finalDateInitialized() && startDateInitialized()
    }

    public fun buttonConditions():Boolean{
        return filtersSelected() && dateInitialized() && coordInitialized()
    }

    public fun getCoords() : String?{
        return localCoord
    }

    public fun getStartingCal() : Calendar{
        return localStartCalendar
    }

    public fun getFinishingCal() : Calendar{
        return localStartCalendar
    }

    suspend fun prepareAssignment(taskID: Int): Pair<assignationAlgorythm, List<Usuario>>? {
        val alg = assignationAlgorythm.create(taskID) ?: return null
        val users = alg.getSelectedUsers()

        return Pair(alg, users)
    }

    suspend fun deleteTask(taskID: Int) {
        try {
            SupabaseAPI().eliminarTarea(taskID)
            _eventFlow.emit(UiEvent.ShowToast("Tarea eliminada por falta de voluntarios"))
        } catch (e: Exception) {
            Log.e("TaskDeletion", "Failed to delete task $taskID", e)
            _eventFlow.emit(UiEvent.ShowToast("Error al eliminar la tarea"))
        }
    }

    public suspend fun IDToTaskName(taskID: Int): String?{
        var task = db.getTaskById(taskID)
        var helpReq = task?.og_req?.let { db.getHelpReqById(it) }
        if (helpReq != null) {
            return helpReq.titulo
        }
        return null
    }

    public suspend fun columnNumber(taskID: Int): Int {
        val task = db.getTaskById(taskID) ?: return -1
        val helpReq = task.og_req?.let { db.getHelpReqById(it) } ?: return -1

        return if (helpReq.categoria in requiredHabilityList) {
            3
        } else {
            2
        }
    }

    public suspend fun IDToTaskCat(taskID: Int): String?{
        var task = db.getTaskById(taskID)
        var helpReq = task?.og_req?.let { db.getHelpReqById(it) }
        if (helpReq != null) {
            return helpReq.categoria
        }
        return null
    }

    fun initializeTaskFlow(taskReq: taskReq?){
        taskReq?.taskIDList?.let{
            taskIdList = it
            currentTaskIndex = 0
            isEditing = false
        }
    }

    fun getNextTask(): Int?{
        return if (!haveAllTasksBeenAssigned() && !isEditing){
            taskIdList[currentTaskIndex]
        }
        else{
            null
        }
    }

    fun onTaskCompleted(){
        currentTaskIndex++
    }

    fun onEditStarted(){
        isEditing = true
    }

    fun onEditFinished(){
        isEditing = false
        onTaskCompleted()
    }

    fun haveAllTasksBeenAssigned(): Boolean{
        return currentTaskIndex >= taskIdList.size
    }

    fun clearFailedAssignments(){
        _failedAssignments.clear()
    }

    fun onTaskFailed(taskId: Int) {
        _failedAssignments.add(taskId)
        currentTaskIndex++
    }


    sealed class UiEvent {
        data class ShowToast(val message: String) : UiEvent()
    }
}