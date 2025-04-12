package com.upv.solidarityHub.persistence

import com.upv.solidarityHub.persistence.database.SupabaseAPI
import java.util.Calendar

private val db: SupabaseAPI = SupabaseAPI()

class SolicitudAyuda private constructor(
    val id: Int?,
    val titulo: String,
    val desc: String,
    val categoria: String,
    val ubicacion: String,
    val horario: String,
    val tamanyo: String,
    val urgencia: String
) {
    companion object {
        suspend fun create(
            titulo: String,
            desc: String,
            categoria: String,
            ubicacion: String,
            horario: String,
            tamanyo: String,
            urgencia: String
        ): SolicitudAyuda {
            println("$titulo, $desc, $categoria,$ubicacion, $horario, $tamanyo, $urgencia")
            val instance = SolicitudAyuda(id = null,titulo, desc, categoria, ubicacion, horario, tamanyo, urgencia)
            db.registrarReq(instance)
            return instance
        }
    }

}