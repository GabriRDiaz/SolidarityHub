package com.upv.solidarityHub.utils.builder

import android.app.AlertDialog

interface DialogBuilder {
    fun setTitle(title: String)
    fun setMessage(message: String)
    fun setIcon()
    fun setButton()
    fun build(): AlertDialog
}
