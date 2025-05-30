package com.upv.solidarityHub.ui.gruposDeAyuda

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GruposViewModel : ViewModel() {
    private val _needsRefresh = MutableStateFlow(false)
    val needsRefresh: StateFlow<Boolean> = _needsRefresh.asStateFlow()

    fun setNeedsRefresh(value: Boolean) {
        _needsRefresh.value = value
    }
}