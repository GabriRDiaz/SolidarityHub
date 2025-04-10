package com.upv.solidarityHub.persistence.factory.habilidad

import com.upv.solidarityHub.persistence.model.Habilidad

interface HabilidadFactory {
    fun createHabilidad(habilidad : String, competencia: Int, preferencia: Int): Habilidad
}
