package com.upv.solidarityHub.ui.taskCreation

import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.tieneAsignado

class TempTaskViewModel: ViewModel() {
    private val db = SupabaseAPI()

    private val requiredHabilityList = listOf(
        "Reconstrucción",
        "Primeros auxilios",
        "Asistencia a mayores",
        "Asistencia a discapacitados",
        "Transporte"
    )

    suspend fun getAllAvailableUsers(currentTask: SupabaseAPI.taskDB?): MutableList<Usuario>? {
        val allUsers = db.getAllUsers()?.toMutableList() ?: return null

        val usersToRemove = mutableListOf<Usuario>()

        for (user in allUsers) {
            val userAssigned = getAllUserAssignations(user.correo) ?: continue
            for (assignation in userAssigned) {
                val task = getTask(assignation.id_task)
                if (currentTask != null && task != null &&
                    task.fecha_inicial == currentTask.fecha_inicial) {
                    usersToRemove.add(user)
                    break
                }
            }
        }

        allUsers.removeAll(usersToRemove)
        return allUsers
    }

    suspend fun getAllUserAssignations(correo: String): List<tieneAsignado>? {
        return db.getAsignacionesUsuario(correo)
    }

    suspend fun getTask(taskID: Int): SupabaseAPI.taskDB? {
        return db.getTaskById(taskID)
    }

    fun taskRequiresAbility(currentReq: SupabaseAPI.reqDB): Boolean {
        return currentReq.categoria in requiredHabilityList
    }

    suspend fun assignSelectedUsers(taskId: Int, selectedUsers: List<Usuario>) {
        selectedUsers.forEach { user ->
            db.createIsAssigned(taskId, user)
        }
    }
}