package com.upv.solidarityHub.utils.strategy

//Concrete Strategy
class ApellidosValidator: IValidator{
    override fun isValid(value: String): Boolean {
        val apellidosRegex = Regex("^[a-zA-Z]*$")
        return apellidosRegex.matches(value)
    }
}