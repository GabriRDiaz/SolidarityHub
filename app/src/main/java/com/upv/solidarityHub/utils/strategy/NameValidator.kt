package com.upv.solidarityHub.utils.strategy

//Concrete Strategy
class NameValidator: IValidator{
    override fun isValid(value: String): Boolean {
        val nameRegex = Regex("^[A-Za-zÀ-ÖØ-öø-ÿ'’\\- ]{2,50}\$")
        return nameRegex.matches(value)
    }
}