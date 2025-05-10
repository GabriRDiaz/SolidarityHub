package com.upv.solidarityHub.utils

import com.google.android.material.textfield.TextInputLayout

object TextInputLayoutUtils {
    public fun TextInputLayout.setErrorTo(errorMessage: String, isEnabled: Boolean) {
        this.isErrorEnabled = isEnabled
        if (isEnabled) this.editText!!.error = errorMessage else this.editText!!.error = null
    }
}