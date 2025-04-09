package com.upv.solidarityHub.persistence

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date
@Serializable
class Usuario(val correo: String, val nombre: String, val apellidos: String, val password: String, val nacimiento:String, val municipio: String) {

}