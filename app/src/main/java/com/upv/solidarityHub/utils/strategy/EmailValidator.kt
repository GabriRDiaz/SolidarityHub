package com.upv.solidarityHub.utils.strategy

//Concrete Strategy
class EmailValidator: IValidator{
    override fun isValid(value: String): Boolean {
        return value.contains("@") && value.contains(".");
    }
}