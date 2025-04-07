package com.upv.solidarityHub.persistence.factory.habilidad

object HabilidadFactoryProvider {
    fun getFactory(nombre: String): HabilidadFactory {
        return when (nombre) {
            "Medicina" -> MedicinaFactory()
            "Veterinaria" -> VeterinariaFactory()
            "Conducción" -> ConduccionFactory()
            else -> throw IllegalArgumentException("No factory found for $nombre")
        }
    }
}