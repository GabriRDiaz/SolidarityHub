package com.upv.solidarityHub.persistence

import java.util.Calendar

class solicitudAyuda(titulo:String,Desc:String, cat: String, location : String, fecha:Calendar,horario:String,tamanyo:String) {
    init {
        System.out.println("$titulo, $Desc, $cat, $fecha, $location, $horario, $tamanyo")
    }

}