package com.upv.solidarityHub.persistence

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date
@Serializable
class Usuario(
    val correo: String,
    val nombre: String,
    val apellidos: String,
    val password: String,
    val nacimiento: String,
    val municipio: String
) : Parcelable {

    // Secondary constructor to create a Usuario object from a Parcel
    constructor(parcel: Parcel) : this(
        correo = parcel.readString() ?: "",
        nombre = parcel.readString() ?: "",
        apellidos = parcel.readString() ?: "",
        password = parcel.readString() ?: "",
        nacimiento = parcel.readString() ?: "",
        municipio = parcel.readString() ?: ""
    )

    // Method to write the object's data to the Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(correo)
        parcel.writeString(nombre)
        parcel.writeString(apellidos)
        parcel.writeString(password)
        parcel.writeString(nacimiento)
        parcel.writeString(municipio)
    }

    // Method to describe the contents of the Parcelable
    override fun describeContents(): Int {
        return 0
    }

    // Companion object that generates instances of the Parcelable class from a Parcel
    companion object CREATOR : Parcelable.Creator<Usuario> {
        override fun createFromParcel(parcel: Parcel): Usuario {
            return Usuario(parcel)
        }

        override fun newArray(size: Int): Array<Usuario?> {
            return arrayOfNulls(size)
        }
    }
}