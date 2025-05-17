package com.upv.solidarityHub.ui.donaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PasarelaPagoViewModel : ViewModel() {
    private val _paymentResult = MutableLiveData<Boolean>()
    val paymentResult: LiveData<Boolean> = _paymentResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun processPayment(amount: Int, cardholder: String, cardNumber: String, expiryDate: String, cvv: String) {
        // Simular lógica de pago
        if (isValidPaymentDetails(cardNumber, expiryDate, cvv)) {
            _paymentResult.value = true
        } else {
            _errorMessage.value = "Error en el procesamiento del pago"
        }
    }

    private fun isValidPaymentDetails(cardNumber: String, expiryDate: String, cvv: String): Boolean {
        // Validación real iría aquí
        return cardNumber.length == 16 && cvv.length in 3..4
    }
}