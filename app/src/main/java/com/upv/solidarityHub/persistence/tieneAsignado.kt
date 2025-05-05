package com.upv.solidarityHub.persistence

import kotlinx.serialization.Serializable

@Serializable
class tieneAsignado(
    val id:Int,
    val created_at: String,
    val id_user: Int,
    val id_task: Int
)