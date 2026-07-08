package com.example.unidad02.ADO

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AlumnoDbHelper  (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null,DATABASE_VERSION){
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(SQL_CREATE_ALUMNO)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS ${DefinirTabla.Alumno.TABLA}")
        onCreate(p0)
    }

    companion object {

        private const val DATABASE_NAME = "sistema"
        private const val DATABASE_VERSION = 2
        private const val TEXT_TYPE = "TEXT"
        private const val INTEGER_TYPE = "INTEGER"
        private const val COMA = " ,"

        private const val  SQL_CREATE_ALUMNO = "CREATE TABLE " + "${DefinirTabla.Alumno.TABLA}" +
                "(${DefinirTabla.Alumno.ID} $INTEGER_TYPE PRIMARY KEY $COMA " +
                "${DefinirTabla.Alumno.MATRICULA} $INTEGER_TYPE $COMA" +
                "${DefinirTabla.Alumno.NOMBRE} $TEXT_TYPE $COMA" +
                "${DefinirTabla.Alumno.DOMICILIO} $TEXT_TYPE $COMA"+
                "${DefinirTabla.Alumno.ESPECIALIDAD} $TEXT_TYPE $COMA" +
                "${DefinirTabla.Alumno.FOTO} $TEXT_TYPE $COMA" +
                "${DefinirTabla.Alumno.QR} $TEXT_TYPE $COMA" +
                "${DefinirTabla.Alumno.STATUS} $INTEGER_TYPE"+ ")"

        private const val  SQL_DELETE_ALUMNO = "DELETE FROM " + "${DefinirTabla.Alumno.TABLA}"
    }
}