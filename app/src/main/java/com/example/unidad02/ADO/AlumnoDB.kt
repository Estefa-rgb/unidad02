package com.example.unidad02.ADO

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class AlumnoDB(private val context: Context)
{
    private val dbHelper = AlumnoDbHelper(context)
    private lateinit var db : SQLiteDatabase

    private val leerRegistros = arrayOf(
        DefinirTabla.Alumno.ID,
        DefinirTabla.Alumno.MATRICULA,
        DefinirTabla.Alumno.NOMBRE,
        DefinirTabla.Alumno.DOMICILIO,
        DefinirTabla.Alumno.ESPECIALIDAD,
        DefinirTabla.Alumno.FOTO,
        DefinirTabla.Alumno.QR,
        DefinirTabla.Alumno.STATUS,
    )

    // Abrir la base de datos

    public fun openDataBase(){
        db = dbHelper.writableDatabase
    }

    public fun insertAlumno(alumno: Alumno): Long {
        val value = ContentValues().apply {
            put(DefinirTabla.Alumno.MATRICULA, alumno.matricula)
            put(DefinirTabla.Alumno.NOMBRE, alumno.nombre)
            put(DefinirTabla.Alumno.DOMICILIO, alumno.domicilio)
            put(DefinirTabla.Alumno.ESPECIALIDAD, alumno.especialidad)
            put(DefinirTabla.Alumno.FOTO, alumno.foto)
            put(DefinirTabla.Alumno.QR, alumno.qr)
            put(DefinirTabla.Alumno.STATUS, alumno.status)
        }
        return db.insert(DefinirTabla.Alumno.TABLA, null, value)
    }
    public fun updateAlumno(alumno: Alumno) : Int {
        val value = ContentValues().apply {
            put(DefinirTabla.Alumno.MATRICULA, alumno.matricula)
            put(DefinirTabla.Alumno.NOMBRE, alumno.nombre)
            put(DefinirTabla.Alumno.DOMICILIO, alumno.domicilio)
            put(DefinirTabla.Alumno.ESPECIALIDAD, alumno.especialidad)
            put(DefinirTabla.Alumno.FOTO, alumno.foto)
            put(DefinirTabla.Alumno.QR, alumno.qr)
            put(DefinirTabla.Alumno.STATUS, alumno.status)
        }

        return db.update(DefinirTabla.Alumno.TABLA, value,
            "${DefinirTabla.Alumno.ID} = ?", arrayOf(alumno.id.toString())
        )
    }

    public fun deleteAlumno(alumno: Alumno) : Int {
        return db.delete(DefinirTabla.Alumno.TABLA, "${DefinirTabla.Alumno.ID} = ?", arrayOf(alumno.id.toString()))
    }

    // Consultas

    // Query que busque por matricula o nombre

    public fun buscarAlumno(str: String): ArrayList<Alumno> {
        val where = "${DefinirTabla.Alumno.MATRICULA} LIKE ? OR ${DefinirTabla.Alumno.NOMBRE} LIKE ?"
        val args = arrayOf("%$str%", "%$str%")
        val cursor = db.query(DefinirTabla.Alumno.TABLA, leerRegistros, where, args, null, null, null)
        val listaAlumnos = ArrayList<Alumno>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val alumno = mostrarAlumnos(cursor)
            listaAlumnos.add(alumno)
            cursor.moveToNext()
        }
        cursor.close()
        return listaAlumnos
    }

    public fun getAlumnoById(id: Int): Alumno {
        val cursor = db.query(DefinirTabla.Alumno.TABLA, leerRegistros, "${DefinirTabla.Alumno.ID} = ?", arrayOf(id.toString()), null, null, null)
        cursor.moveToFirst()
        val alumno = mostrarAlumnos(cursor)
        cursor.close()
        return alumno
    }


    public fun mostrarAlumnos(cursor: Cursor) : Alumno {
        if (!cursor.isAfterLast) {
            return Alumno().apply {
                id = cursor.getInt(0)
                matricula = cursor.getString(1)
                nombre = cursor.getString(2)
                domicilio = cursor.getString(3)
                especialidad = cursor.getString(4)
                foto = cursor.getString(5)
                qr = cursor.getString(6)
                status = cursor.getInt(7)
            }
        } else return Alumno()
    }

    public fun getAlumno(matricula: String) : Alumno{
        val cursor = db.query(DefinirTabla.Alumno.TABLA, leerRegistros, "${DefinirTabla.Alumno.MATRICULA} = ?", arrayOf(matricula), null, null, null)
        cursor.moveToFirst()
        val alumno = mostrarAlumnos(cursor)
        cursor.close()
        return alumno
    }

    public fun getAlumnos(): ArrayList<Alumno> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DefinirTabla.Alumno.TABLA, leerRegistros, null, null, null, null, null, null)
        val listaAlumnos = ArrayList<Alumno>()
        cursor.moveToFirst()

        while (!cursor.isAfterLast) {
            val alumno = mostrarAlumnos(cursor)
            listaAlumnos.add(alumno)
            cursor.moveToNext()
        }
        cursor.close()
        return listaAlumnos
    }

    public fun closeDataBase(){
        dbHelper.close()
    }
}


