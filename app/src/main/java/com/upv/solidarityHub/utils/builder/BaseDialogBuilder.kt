package com.upv.solidarityHub.utils.builder

import android.app.AlertDialog
import android.content.Context

abstract class BaseDialogBuilder(protected val context: Context) : DialogBuilder {
    protected lateinit var builder: AlertDialog.Builder

    override fun build(): AlertDialog {
        return builder.create()
    }
}
