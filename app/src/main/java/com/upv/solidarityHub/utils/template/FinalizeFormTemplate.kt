package com.upv.solidarityHub.utils.template

import android.widget.Toast
import com.upv.solidarityHub.ui.habilidades.HabilidadesActivity

abstract class FinalizeFormTemplate(
    protected val context: android.content.Context,
    protected val parent: HabilidadesActivity
) {
    fun finalize() {
        showConfirmation()
        saveData()
        postFinalize()
    }

    protected open fun showConfirmation() {
        Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
    }

    protected abstract fun saveData()

    protected open fun postFinalize() {
        parent.goToMain()
    }
}
