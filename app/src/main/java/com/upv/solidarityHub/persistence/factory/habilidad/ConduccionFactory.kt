package com.upv.solidarityHub.persistence.factory.habilidad

import com.upv.solidarityHub.persistence.model.Habilidad

class ConduccionFactory : HabilidadFactory {
    override fun createHabilidad(competencia: Int, preferencia: Int): Habilidad {
        return Habilidad(
            nombre = "Conduccion",
            ponderaciones = mapOf(
                "Logística" to 0.7f,
                "Transporte de Personas" to 0.9f
            ),
            competencia = competencia,
            preferencia = preferencia
        )
    }
}