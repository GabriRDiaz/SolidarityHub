package com.upv.solidarityHub.utils.builder

import android.app.AlertDialog

class DialogDirector(private var builder: DialogBuilder) {

    fun setBuilder(builder: DialogBuilder) {
        this.builder = builder
    }

    fun constructDialog(title: String, message: String): AlertDialog {
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setIcon()
        builder.setButton()
        return builder.build()
    }
}

