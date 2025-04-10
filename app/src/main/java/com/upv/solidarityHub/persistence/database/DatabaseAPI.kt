package com.upv.solidarityHub.persistence.database

import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.Usuario

interface DatabaseAPI {

    fun initializeDatabase()

    public suspend fun getUsuarioByCorreo(correo: String): Usuario?
    public suspend fun getBalizaByName(name: String): Baliza?
    public suspend fun getAllBalizas(): List<Baliza>?
    public suspend fun addBaliza(id: Int, latitud: Double,longitud: Double, nombre:String,tipo: String,descripcion:String): Boolean
    public suspend fun registerUsuario(correo: String, nombre: String, apellidos: String, password: String, nacimiento:String, municipio: String):Boolean
}