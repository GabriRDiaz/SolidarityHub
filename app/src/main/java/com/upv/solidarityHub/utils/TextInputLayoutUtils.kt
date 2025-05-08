package com.upv.solidarityHub.utils

import com.google.android.material.textfield.TextInputLayout

object TextInputLayoutUtils {
    public fun TextInputLayout.setErrorTo(errorMessage: String, isEnabled: Boolean) {
        this.editText!!.error = errorMessage
        this.isErrorEnabled = isEnabled
        if (!isEnabled) this.editText!!.error = null
    }
}