package com.upv.solidarityHub.persistence.factory.habilidad

import com.upv.solidarityHub.persistence.model.Habilidad

class VeterinariaFactory: HabilidadFactory {
    override fun createHabilidad(competencia: Int, preferencia: Int): Habilidad {
        return Habilidad(
            nombre = "Veterinaria",
            ponderaciones = mapOf(
                "Rescate animal" to 0.9f,
                "Primeros auxilios" to 0.6f
            ),
            competencia = competencia,
            preferencia = preferencia
        )
    }
}
