package com.upv.solidarityHub.persistence.repository

import com.upv.solidarityHub.persistence.model.Habilidad

interface HabilidadRepository {
    suspend fun addHabilidad(habilidad: Habilidad): Result<Habilidad>
    suspend fun updateHabilidad(habilidad: Habilidad): Result<Habilidad>
    suspend fun removeHabilidad(id: String): Result<Unit>
    suspend fun getHabilidad(id: String): Result<Habilidad>
    suspend fun getAllHabilidades(): Result<List<Habilidad>>

    suspend fun updateCompetencia(id: String, competencia: Int): Result<Unit>
    suspend fun updatePreferencia(id: String, preferencia: Int): Result<Unit>
    suspend fun updatePonderacion(id: String, tipoTrabajo: String, valor: Float): Result<Unit>
}