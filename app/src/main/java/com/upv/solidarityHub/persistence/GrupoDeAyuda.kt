package com.upv.solidarityHub.persistence

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class GrupoDeAyuda(
    val id: Int,
    val descripcion: String,
    val ubicacion: String,
    val fecha_creacion: Date,
    val sesion: String,
    val tamanyo: String
):Parcelable