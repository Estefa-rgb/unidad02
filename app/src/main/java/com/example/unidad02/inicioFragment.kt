package com.example.unidad02

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.unidad02.ADO.Alumno
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class inicioFragment : Fragment() {

    private lateinit var btnEscanncer: Button

    private val barcode = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            mostrarInformacionQR(result.contents)
        } else {
            Toast.makeText(requireContext(), "El scanner cancelado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)
        iniciarComponentes(view)
        eventosClic()
        return view
    }

    private fun iniciarComponentes(view: View) {
        btnEscanncer = view.findViewById(R.id.btnEscanearQr)
    }

    private fun eventosClic() {
        val opciones = ScanOptions()

        btnEscanncer.setOnClickListener {
            opciones.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            opciones.setPrompt("Escanear QR alumno")
            opciones.setCameraId(0)
            opciones.setBeepEnabled(true)
            opciones.setOrientationLocked(true)
            barcode.launch(opciones)
        }
    }

    private fun mostrarInformacionQR(jsonString: String) {
        try {
            val gson = Gson()
            val informacionAlumno = gson.fromJson(jsonString, Alumno::class.java)

            if (!informacionAlumno.matricula.isNullOrEmpty()) {
                //confirmarInformacion(informacionAlumno)
                irAlumnoFragment(informacionAlumno)
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se encontró información",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error al leer el QR",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun confirmarInformacion(alumno: Alumno) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Validar la información")

        val mensaje = """
            Matrícula: ${alumno.matricula}
            Nombre: ${alumno.nombre}
            Domicilio: ${alumno.domicilio}
            Especialidad: ${alumno.especialidad}
            Foto: ${alumno.foto}

            ¿Deseas cargar los datos?
        """.trimIndent()

        builder.setMessage(mensaje)

        builder.setPositiveButton("Cargar y validar") { _, _ ->
            Toast.makeText(
                requireContext(),
                "Todos los datos son correctos",
                Toast.LENGTH_SHORT
            ).show()
        }

        builder.setNegativeButton("No correctos") { _, _ ->
            Toast.makeText(
                requireContext(),
                "Los datos son incorrectos",
                Toast.LENGTH_SHORT
            ).show()
        }

        builder.create().show()
    }

    private fun irAlumnoFragment(alumno: Alumno) {

        val fragment = alumnoFragment()

        val bundle = Bundle()
        bundle.putSerializable("miAlumno", alumno)

        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.frmContenedor, fragment)
            .addToBackStack(null)
            .commit()
    }
}