package com.upv.solidarityHub

import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.properties.Delegates

class assignationAlgorythm private constructor(
    private val selectedUsers: MutableList<Usuario> = mutableListOf(),
    private val selectedUserEmails: MutableSet<String> = mutableSetOf(),
    private val currentTask: SupabaseAPI.taskDB,
    private val currentHelpReq: SupabaseAPI.reqDB,
    private var taskMin: Int,
    private var taskMax: Int
) {
    companion object {
        private val requiredHabilityList = listOf(
            "Reconstrucción",
            "Primeros auxilios",
            "Asistencia a mayores",
            "Asistencia a discapacitados",
            "Transporte"
        )

        suspend fun create(taskID: Int): assignationAlgorythm? {
            val db = SupabaseAPI()
            val selectedUsers = mutableListOf<Usuario>()
            val selectedUsersEmails = mutableSetOf<String>()

            val currentTask = db.getTaskById(taskID) ?: return null
            val currentHelpReq = currentTask.og_req?.let { db.getHelpReqById(it) } ?: return null

            val (taskMin, taskMax) = when (currentHelpReq.envergadura) {
                "Pequeña (5 voluntarios máx.)" -> 1 to 5
                "Media (15 voluntarios máx.)" -> 6 to 15
                "Grande (15+ voluntarios)" -> 16 to 50
                else -> 1 to 10
            }

            suspend fun userBusy(user: Usuario): Boolean {
                val userAssigned = db.getAsignacionesUsuario(user.correo) ?: return false
                return userAssigned.any {
                    db.getTaskById(it.id_task)?.fecha_inicial == currentTask.fecha_inicial
                }
            }

            suspend fun assignUsersFrom(emails: List<String>) {
                for (email in emails) {
                    val user = db.getUsuarioByCorreo(email)
                    if (user != null && user.correo !in selectedUsersEmails && !userBusy(user)) {
                        selectedUsers.add(user)
                        selectedUsersEmails.add(user.correo)
                    }
                    if (selectedUsers.size >= taskMin) break
                }
            }

            if (currentHelpReq.categoria in requiredHabilityList) {
                assignUsersFrom(
                    db.getUsersWithAbility(currentHelpReq.categoria.orEmpty()) ?: emptyList()
                )
            }

            if (selectedUsers.size < taskMin) {
                assignUsersFrom(db.getUsersTown(currentHelpReq.ubicacion.orEmpty()) ?: emptyList())
            }

            if (selectedUsers.size < taskMin) {
                val allEmails = db.getAllUsers()?.map { it.correo}
                if (allEmails != null) {
                    assignUsersFrom(allEmails)
                }
            }

            return assignationAlgorythm(
                selectedUsers,
                selectedUsersEmails,
                currentTask,
                currentHelpReq,
                taskMin,
                taskMax
            )


        }


    }
    fun getSelectedUsers(): List<Usuario> = selectedUsers
    fun getCurrentTask(): SupabaseAPI.taskDB = currentTask
    fun getMin(): Int = taskMin
    fun getMax(): Int = taskMax
}