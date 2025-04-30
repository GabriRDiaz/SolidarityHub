package com.upv.solidarityHub.persistence

import com.google.android.gms.tasks.Task
import com.upv.solidarityHub.persistence.SolicitudAyuda
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

lateinit var helpReq: List<SolicitudAyuda>
private val db: SupabaseAPI = SupabaseAPI()

class taskReq private constructor(
    val id: Int?,
    val cat : String? = null,
    val town : String? = null,
    val priority : String? = null,
    val schedule : String? = null,
    val size : String? = null,
    val lat : Double,
    val long : Double,
    val date : Calendar,
    var taskIDList: List<Int>?
){
    companion object{
        suspend fun create(
            cat : String?,
            town : String?,
            priority : String?,
            schedule : String?,
            size : String?,
            lat : Double,
            long : Double,
            date : Calendar,
            taskIDList: List<Int>?
        ): taskReq? {
            val instance = taskReq(id = null, cat, town, priority,schedule,size,lat,long,date, null)
            System.out.println("$cat, $town, $priority, $schedule, $size, $lat, $long, $date")
            val list = db.helpReqsToTasks(instance)
            if(list != null){
                instance.taskIDList = getIDsFromTaskList(list)
                return instance
            }
            return null

        }

        private fun getIDsFromTaskList(list: List<SupabaseAPI.taskDB>): List<Int>? {
            return list.map {it.id!!}
        }
    }

    fun calendarToDateString(calendar: Calendar, format: String = "dd/MM/yyyy"): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(calendar.time)
    }





}