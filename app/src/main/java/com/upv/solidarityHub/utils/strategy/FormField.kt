package com.upv.solidarityHub.utils.strategy

//Implementation of Context
class FormField(val name: String, val value: String, private val validator:IValidator){
    fun isValid():Boolean{
        return validator.isValid(value);
    }
}