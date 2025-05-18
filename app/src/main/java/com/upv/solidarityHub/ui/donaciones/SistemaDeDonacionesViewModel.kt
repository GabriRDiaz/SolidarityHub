package com.upv.solidarityHub.ui.donaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SistemaDeDonacionesViewModel : ViewModel() {
    private val _donationAmount = MutableLiveData(10)
    val donationAmount: LiveData<Int> = _donationAmount

    private val _selectedPaymentMethod = MutableLiveData<String>()
    val selectedPaymentMethod: LiveData<String> = _selectedPaymentMethod

    fun increaseAmount() {
        _donationAmount.value = (_donationAmount.value ?: 10) + 5
    }

    fun decreaseAmount() {
        _donationAmount.value = (_donationAmount.value ?: 10).coerceAtLeast(5) - 5
    }

    fun selectPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }
}