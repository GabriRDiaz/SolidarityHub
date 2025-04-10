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
    val fecha: Calendar,
    val horario: String,
    val tamanyo: String
) {
    companion object {
        suspend fun create(
            titulo: String,
            desc: String,
            categoria: String,
            ubicacion: String,
            fecha: Calendar,
            horario: String,
            tamanyo: String
        ): SolicitudAyuda {
            println("$titulo, $desc, $categoria, $fecha, $ubicacion, $horario, $tamanyo")
            val instance = SolicitudAyuda(id = null,titulo, desc, categoria, ubicacion, fecha, horario, tamanyo)
            db.registrarReq(instance)
            return instance
        }
    }

}