package com.upv.solidarityHub.persistence.database

import com.upv.solidarityHub.persistence.Usuario

interface DatabaseAPI {

    fun initializeDatabase()

    public suspend fun getUsuarioByCorreo(correo: String): Usuario?
}