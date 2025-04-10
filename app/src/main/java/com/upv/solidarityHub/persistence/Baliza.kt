package com.upv.solidarityHub.persistence
import kotlinx.serialization.Serializable
@Serializable
class Baliza(val id: Int,val latitud: Double,val longitud: Double,val nombre:String,val tipo: String,val descripcion:String){
}