package com.upv.solidarityHub.persistence.model

import kotlinx.serialization.Serializable

@Serializable
data class Habilidad(
    var nombre: String,
    val ponderaciones: Map<String, Float>,
    val competencia: Int,
    val preferencia: Int
)
