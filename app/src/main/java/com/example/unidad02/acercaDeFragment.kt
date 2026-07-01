package com.example.unidad02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.unidad02.ADO.Alumno // Verifica esta ruta en tu proyecto
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

/**
 * A simple [Fragment] subclass.
 * Use the [acercaDeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class acercaDeFragment : Fragment() {

    private lateinit var txtMatricula : TextView
    private lateinit var txtNombre : TextView
    private lateinit var txtDomicilio : TextView
    private lateinit var txtEspecialidad : TextView
    private lateinit var imgQr : ImageView
    private lateinit var imgFoto : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_acerca_de, container, false)
        iniciarComponentes(view)
        cargarDatos()
        return view
    }

    fun iniciarComponentes(view : View) {
        txtMatricula = view.findViewById<EditText>(R.id.txtMatricula)
        txtNombre = view.findViewById<EditText>(R.id.txtNombreAlumno)
        txtDomicilio = view.findViewById<EditText>(R.id.txtDomicilio)
        txtEspecialidad = view.findViewById<EditText>(R.id.txtEspecialidad)
        imgQr = view.findViewById<ImageView>(R.id.imgQr)
        imgFoto = view.findViewById<ImageView>(R.id.imgFoto)

    }

    private fun cargarDatos() {
        try {
            val yo = Alumno().apply {
                matricula = resources.getString(R.string.strMatricula)
                nombre = resources.getString(R.string.strNombreAlumno)
                domicilio = resources.getString(R.string.strDomicilio)
                especialidad = resources.getString(R.string.strEspecialidad)
                foto = resources.getString(R.string.strImg)
            }

            txtMatricula.text = "Matrícula: ${yo.matricula}"
            txtNombre.text = "Nombre: ${yo.nombre}"
            txtEspecialidad.text = "Especialidad: ${yo.especialidad}"
            txtDomicilio.text = "Domicilio: ${yo.domicilio}"

            // Cargar foto url
            Glide.with(requireContext())
                .load(yo.foto)
                .into(imgFoto)

            // Convertir el objeto a json
            val gson = Gson()
            val jsonString = gson.toJson(yo)

            // Realizar el QR en una imagen
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                jsonString,
                BarcodeFormat.QR_CODE,
                1000,
                1000
            )
            imgQr.setImageBitmap(bitmap)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generando QR", Toast.LENGTH_SHORT).show()
        }
    }
}