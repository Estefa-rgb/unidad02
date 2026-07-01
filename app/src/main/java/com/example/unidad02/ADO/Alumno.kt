package com.example.unidad02.ADO

import java.io.Serializable

data class Alumno(
    var id : Int = 0,
    var matricula : String = "",
    var nombre : String = "",
    var domicilio : String = "",
    var especialidad : String = "",
    var foto : String = "",
    var qr : String = "",
    var status : Int = 0
) : Serializable
