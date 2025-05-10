package com.upv.solidarityHub.utils.strategy

//Strategy Interface

interface IValidator {
    fun isValid(value: String):Boolean
}