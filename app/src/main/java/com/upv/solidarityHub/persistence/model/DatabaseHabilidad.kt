package com.upv.solidarityHub.persistence.model
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseHabilidad (
    val nombre_habilidad: String,
    val correo_usuario: String,
    val competencia: Int,
    val preferencia: Int,
    )
