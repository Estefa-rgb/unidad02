package com.example.unidad02.ADO

import android.provider.BaseColumns

class DefinirTabla {

    object Alumno : BaseColumns {
        const val TABLA = "alumnos"
        const val ID = "id"
        const val MATRICULA = "matricula"
        const val NOMBRE = "nombre"
        const val DOMICILIO = "domicilio"
        const val ESPECIALIDAD = "especialidad"
        const val FOTO = "foto"
        const val QR = "qr"
        const val STATUS = "status"
    }

}