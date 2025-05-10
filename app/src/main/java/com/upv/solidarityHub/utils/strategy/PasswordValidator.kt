package com.upv.solidarityHub.utils.strategy

//Concrete Strategy
class PasswordValidator: IValidator{
    override fun isValid(value: String): Boolean {
        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[:;,.-]).{8,}$")
        return passwordRegex.matches(value)
    }
}