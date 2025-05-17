package com.upv.solidarityHub.ui.donaciones

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

data class CardData(
    val cardholder: String,
    val cardNumber: String,
    val expiryDate: String,
    val cvv: String
)

class PasarelaPagoViewModel : ViewModel() {

    private val _paymentResult = MutableLiveData<Boolean>()
    val paymentResult: LiveData<Boolean> get() = _paymentResult

    // Mensajes de error
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun processPayment(
        amount: Int,
        paymentMethod: String,
        cardData: CardData? = null,
        phone: String? = null,
        email: String? = null
    ) {
        // Simulación de lógica de pago
        when (paymentMethod) {
            "Tarjeta bancaria" -> {
                if (cardData != null) {
                    when (val result = validateCard(cardData)) {
                        is CardValidationResult.Valid -> {
                            // Procesar pago
                            _paymentResult.value = true
                        }
                        is CardValidationResult.Invalid -> {
                            _errorMessage.value = result.errorMessages.joinToString("\n")
                        }
                    }
                } else {
                    _errorMessage.value = "Datos de tarjeta no proporcionados"
                }
            }
            "Bizum" -> {
                if (phone != null && validatePhone(phone)) {
                    _paymentResult.value = true
                } else {
                    _errorMessage.value = "Teléfono inválido"
                }
            }
            "Paypal" -> {
                if (email != null && validateEmail(email)) {
                    _paymentResult.value = true
                } else {
                    _errorMessage.value = "Correo inválido"
                }
            }
        }
    }

    private fun validatePhone(phone: String): Boolean {
        return phone.length == 9 && phone.all { it.isDigit() }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidExpiryDate(expiry: String): Boolean {
        val parts = expiry.split("/").takeIf { it.size == 2 } ?: return false
        val (month, year) = parts
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        return year.toIntOrNull()?.let { y ->
            month.toIntOrNull()?.let { m ->
                m in 1..12 && (y > currentYear || (y == currentYear && m >= currentMonth))
            }
        } ?: false
    }

    sealed class CardValidationResult {
        data object Valid : CardValidationResult()
        data class Invalid(val errorMessages: List<String>) : CardValidationResult()
    }

    private fun validateCard(data: CardData): CardValidationResult {
        val errors = mutableListOf<String>()

        if (data.cardholder.isBlank()) {
            errors.add("El nombre del titular es requerido")
        }

        if (data.cardNumber.length != 16 || !data.cardNumber.all { it.isDigit() }) {
            errors.add("Número de tarjeta inválido (16 dígitos requeridos)")
        }

        if (data.cvv.length !in 3..4 || !data.cvv.all { it.isDigit() }) {
            errors.add("CVV inválido (3-4 dígitos requeridos)")
        }

        if (!isValidExpiryDate(data.expiryDate)) {
            errors.add("Fecha de expiración inválida (MM/AAAA requerido)")
        }

        return if (errors.isEmpty()) CardValidationResult.Valid
        else CardValidationResult.Invalid(errors)
    }

}