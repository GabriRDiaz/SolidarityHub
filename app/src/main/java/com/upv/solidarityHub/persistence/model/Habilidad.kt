package com.upv.solidarityHub.persistence.model

data class Habilidad(
    val nombre: String,
    val ponderaciones: Map<String, Float>,
    val competencia: Int,
    val preferencia: Int
)
