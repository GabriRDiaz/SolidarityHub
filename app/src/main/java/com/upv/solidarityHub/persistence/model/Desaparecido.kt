package com.upv.solidarityHub.persistence.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable

@Serializable
class Desaparecido(
    val nombre: String,
    val apellidos: String,
    val edad: Int,
    val altura: Double,
    val complexion: Int,
    val genero: Int,
    val id_baliza_visto_por_ultima_vez: Int?
) : Parcelable {


    constructor(parcel: Parcel) : this(
        nombre = parcel.readString() ?: "",
        apellidos = parcel.readString() ?: "",
        edad = parcel.readInt(),
        altura = parcel.readDouble(),
        complexion = parcel.readInt(),
        genero = parcel.readInt(),
        id_baliza_visto_por_ultima_vez = parcel.readInt()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nombre)
        parcel.writeString(apellidos)
        parcel.writeInt(edad)
        parcel.writeDouble(altura)
        parcel.writeInt(complexion)
        parcel.writeInt(genero)
        if(id_baliza_visto_por_ultima_vez != null) {
            parcel.writeInt(id_baliza_visto_por_ultima_vez)
        }

    }



    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Desaparecido> {
        override fun createFromParcel(parcel: Parcel): Desaparecido {
            return Desaparecido(parcel)
        }

        override fun newArray(size: Int): Array<Desaparecido?> {
            return arrayOfNulls(size)
        }
    }
}