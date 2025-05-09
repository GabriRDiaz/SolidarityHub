package com.upv.solidarityHub.persistence
import kotlinx.serialization.Serializable
@Serializable
class Baliza(val id: Int, val latitud: Double, val longitud: Double, var nombre:String,
             var tipo: String, var descripcion:String){
}