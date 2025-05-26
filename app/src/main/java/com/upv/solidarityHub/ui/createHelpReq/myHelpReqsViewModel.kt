package com.upv.solidarityHub.ui.createHelpReq

import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class MyHelpReqsViewModel : ViewModel() {
    private val db = SupabaseAPI()
    private var ogReqs: List<Int> = emptyList()

    private val _selectedRequest = MutableStateFlow<Pair<SupabaseAPI.reqDB, Boolean>?>(null)

    suspend fun loadData(): List<Pair<SupabaseAPI.reqDB, Boolean>> {
        ogReqs = db.getTaskOGReqs() ?: emptyList()
        val user = db.getLogedUser()
        val helpRequests = db.getReqsUser(user.correo) ?: return emptyList()
        return helpRequests.map { req ->
            req to (req.id?.let { isAlreadyATask(it) } ?: false)
        }
    }

    fun isAlreadyATask(reqID: Int): Boolean = reqID in ogReqs

    fun setSelectedRequest(req: SupabaseAPI.reqDB, isAlreadyATask: Boolean) {
        _selectedRequest.value = Pair(req, isAlreadyATask)
    }

    suspend fun deleteSelectedReq(){
        _selectedRequest.first()?.first?.id?.let { db.deleteReq(it) }
    }
}