package com.upv.solidarityHub.persistence

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date

@Serializable
data class GrupoDeAyuda(
    val id: Int,
    val descripcion: String,
    val ubicacion: String,
    val fecha_creacion: String,
    val sesion: String,
    val tamanyo: Int
)