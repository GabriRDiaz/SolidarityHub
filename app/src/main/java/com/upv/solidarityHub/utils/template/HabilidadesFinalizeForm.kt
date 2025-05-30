package com.upv.solidarityHub.utils.template

import android.content.Context
import android.widget.Toast
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.DatabaseAPI
import com.upv.solidarityHub.persistence.model.Habilidad
import com.upv.solidarityHub.ui.habilidades.HabilidadesActivity
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking


class HabilidadesFinalizeForm(
    context: Context,
    private val db: DatabaseAPI,
    private val usuario: Usuario,
    private val habilidades: List<Habilidad>,
    private val onFinishedCallback: () -> Unit,
    parent: HabilidadesActivity
) : FinalizeFormTemplate(context, parent) {

    override fun saveData() {
        runBlocking {
            try {
                val deferred = async {
                    db.registrarHabilidades(habilidades, usuario)
                }
                deferred.await()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al guardar las habilidades", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun postFinalize() {
        onFinishedCallback()
    }
}
