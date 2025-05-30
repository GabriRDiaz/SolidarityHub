package com.upv.solidarityHub.utils.builder

import android.app.AlertDialog
import android.content.Context

class ErrorDialogBuilder(context: Context) : BaseDialogBuilder(context) {
    override fun setTitle(title: String) {
        builder = AlertDialog.Builder(context)
        builder.setTitle(title)
    }

    override fun setMessage(message: String) {
        builder.setMessage(message)
    }

    override fun setIcon() {
        builder.setIcon(android.R.drawable.ic_delete)
    }

    override fun setButton() {
        builder.setPositiveButton("Dismiss") { dialog, _ -> dialog.dismiss() }
    }
}
