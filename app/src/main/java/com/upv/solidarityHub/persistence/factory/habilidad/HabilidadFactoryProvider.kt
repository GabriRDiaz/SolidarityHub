package com.upv.solidarityHub.persistence.factory.habilidad

object HabilidadFactoryProvider {
    fun getFactory(): HabilidadFactory = HabilidadFactoryImpl()
}