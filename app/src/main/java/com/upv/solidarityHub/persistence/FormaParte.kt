package com.upv.solidarityHub.persistence

import kotlinx.serialization.Serializable
import java.util.Date
@Serializable
data class FormaParte(val user: String, val grupo:Int, val fecha: String) {
}