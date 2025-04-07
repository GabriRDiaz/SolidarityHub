package com.upv.solidarityHub.persistence.factory.habilidad

import com.upv.solidarityHub.persistence.model.Habilidad

class MedicinaFactory : HabilidadFactory {
    override fun createHabilidad(competencia: Int, preferencia: Int): Habilidad {
        return Habilidad(
            nombre = "Medicina",
            ponderaciones = mapOf(
                "Diagnostico" to 0.9f,
                "Primeros auxilios" to 0.9f
            ),
            competencia = competencia,
            preferencia = preferencia
        )
    }
}