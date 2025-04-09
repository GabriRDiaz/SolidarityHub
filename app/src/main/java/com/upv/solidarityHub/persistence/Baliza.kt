package com.upv.solidarityHub.persistence
import kotlinx.serialization.Serializable
@Serializable
class Baliza(val nombre: String,val id:Int,val tipo:String,val descripcion: String,val latitud: Double,val longitud: Double){
}