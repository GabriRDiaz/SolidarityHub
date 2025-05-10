package com.upv.solidarityHub.utils.strategy

//Context Interface

interface IFormField{
    val name: String;
    val value: String;
    fun isValid() : Boolean;
}