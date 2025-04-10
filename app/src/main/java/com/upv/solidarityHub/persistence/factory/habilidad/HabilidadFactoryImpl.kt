package com.upv.solidarityHub.persistence.factory.habilidad

import com.upv.solidarityHub.persistence.model.Habilidad

class HabilidadFactoryImpl : HabilidadFactory {
    override fun createHabilidad(nombre: String, competencia: Int, preferencia: Int): Habilidad {
        val ponderaciones = when (nombre) {
            "Medicina" -> mapOf(
                "Diagnostico" to 0.9f,
                "Primeros auxilios" to 0.9f
            )
            "Veterinaria" -> mapOf(
                "Rescate animal" to 0.9f,
                "Primeros auxilios" to 0.6f
            )
            "Conducción" -> mapOf(
                "Vehículos pesados" to 0.8f,
                "Logística" to 0.7f
            )
            else -> throw IllegalArgumentException("No Habilidad defined for $nombre")
        }

        return Habilidad(
            nombre = nombre,
            ponderaciones = ponderaciones,
            competencia = competencia,
            preferencia = preferencia
        )
    }
}
