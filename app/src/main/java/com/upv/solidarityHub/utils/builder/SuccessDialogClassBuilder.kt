package com.upv.solidarityHub.utils.builder

import android.app.AlertDialog
import android.content.Context

class SuccessDialogBuilder(context: Context) : BaseDialogBuilder(context) {
    override fun setTitle(title: String) {
        builder = AlertDialog.Builder(context)
        builder.setTitle(title)
    }

    override fun setMessage(message: String) {
        builder.setMessage(message)
    }

    override fun setIcon() {
        builder.setIcon(android.R.drawable.checkbox_on_background)
    }

    override fun setButton() {
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
    }
}

