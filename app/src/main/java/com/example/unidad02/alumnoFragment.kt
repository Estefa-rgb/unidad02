package com.example.unidad02

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.unidad02.ADO.Alumno
import com.example.unidad02.ADO.AlumnoDB
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class alumnoFragment : Fragment() {

    private lateinit var imgAlumno: ImageView
    private lateinit var imgQR: ImageView
    private lateinit var edtMatricula: EditText
    private lateinit var edtNombre: EditText
    private lateinit var edtDomicilio: EditText
    private lateinit var spEspecialidad: Spinner
    private lateinit var btnBuscar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var btnCerrar: Button
    private var posicion: Int = 0
    private lateinit var db : AlumnoDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_alumno, container, false)
        iniciarComponentes(view)
        eventosClick()
        return view
    }

    private fun iniciarComponentes(root: View) {
        imgAlumno = root.findViewById(R.id.imgAlumno)
        imgQR = root.findViewById(R.id.imgQR)
        edtMatricula = root.findViewById(R.id.edtMatricula)
        edtNombre = root.findViewById(R.id.edtNombre)
        edtDomicilio = root.findViewById(R.id.edtDomicilio)
        spEspecialidad = root.findViewById(R.id.spEspecialidad)
        btnBuscar = root.findViewById(R.id.btnBuscar)
        btnGuardar = root.findViewById(R.id.btnGuardar)
        btnLimpiar = root.findViewById(R.id.btnLimpiar)
        btnCerrar = root.findViewById(R.id.btnCerrar)

        val items = resources.getStringArray(R.array.strEspecialidades)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        spEspecialidad.adapter = adapter

        informacionFromInicio()
    }

    private fun informacionFromInicio() {
        val alumno = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable("miAlumno", Alumno::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getSerializable("miAlumno") as? Alumno
        }

        alumno?.let {
            edtMatricula.setText(it.matricula)
            edtNombre.setText(it.nombre)
            edtDomicilio.setText(it.domicilio)

            val items = resources.getStringArray(R.array.strEspecialidades)
            val posicion = items.indexOf(it.especialidad)

            if (posicion != -1) {
                spEspecialidad.setSelection(posicion)
            }

            Glide.with(requireContext())
                .load(it.foto)
                .error(R.drawable.alumnos)
                .into(imgAlumno)

            imgAlumno.tag = it.foto

            val gson = Gson()
            val jsonString = gson.toJson(it)

            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                jsonString,
                BarcodeFormat.QR_CODE,
                300,
                300
            )

            imgQR.setImageBitmap(bitmap)
        }
    }

    private fun eventosClick() {
        spEspecialidad.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    posicion = position
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        btnBuscar.setOnClickListener {
            if (edtMatricula.text.toString().contentEquals("")) {

                Toast.makeText(requireContext(), "Faltó campturar la matricula", Toast.LENGTH_SHORT)
                    .show()
            } else {
                var resAlumno = Alumno()
                val db = AlumnoDB(requireContext())
                db.openDataBase()

                resAlumno = db.getAlumno(edtMatricula.text.toString())

                if (resAlumno.id > 0) {
                    Glide.with(requireContext())
                        .load(resAlumno.foto)
                        .error(R.drawable.alumnos)
                        .into(imgAlumno)

                    imgAlumno.tag = resAlumno.foto
                    edtMatricula.setText(resAlumno.matricula)
                    edtNombre.setText(resAlumno.nombre)
                    edtDomicilio.setText(resAlumno.domicilio)

                    @Suppress("UNCHECKED_CAST")
                    val adapter = spEspecialidad.adapter as? ArrayAdapter<String>
                    val posicion = adapter?.getPosition(resAlumno.especialidad) ?: -1
                    if (posicion != -1) {
                        spEspecialidad.setSelection(posicion)
                    }

                    val gson = Gson()
                    val jsonString = gson.toJson(resAlumno)

                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.encodeBitmap(
                        jsonString,
                        BarcodeFormat.QR_CODE,
                        1000,
                        1000
                    )
                    imgQR.setImageBitmap(bitmap)

                    Toast.makeText(
                        requireContext(),
                        "Alumno encontrado, modifica los datos y guarda",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Alumno no encontrado, puedes agregarlo",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }

        }
        btnGuardar.setOnClickListener {
            val alumno = Alumno()
            val matricula = edtMatricula.text.toString()
            val nombre = edtNombre.text.toString()
            val domicilio = edtDomicilio.text.toString()
            alumno.status = 0

            if (matricula.isEmpty() || nombre.isEmpty() || domicilio.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Se requieren que todos los campos sean rellenados",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                alumno.matricula = edtMatricula.text.toString()
                alumno.nombre = edtNombre.text.toString()
                alumno.domicilio = edtDomicilio.text.toString()
                alumno.especialidad = spEspecialidad.selectedItem.toString()
                alumno.foto = imgAlumno.tag.toString()
                // alumno.foto = imgAlumno.tag?.toString() ?: ""
                val gson = Gson()
                alumno.qr = gson.toJson(alumno)
                alumno.status = 0
                val db = AlumnoDB(requireContext())
                db.openDataBase()

                val existente = db.getAlumno(alumno.matricula)
                if (existente.id > 0) {
                    alumno.id = existente.id
                    db.updateAlumno(alumno)
                    Toast.makeText(
                        requireContext(),
                        "Datos actualizados correctamente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val id: Long = db.insertAlumno(alumno)
                    Toast.makeText(
                        requireContext(),
                        "Datos guardados correctamente con el ID ${id}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        }

        btnLimpiar.setOnClickListener {
            edtMatricula.setText("")
            edtNombre.setText("")
            edtDomicilio.setText("")
            spEspecialidad.setSelection(0)
            imgAlumno.setImageResource(R.drawable.alumnos)
            imgQR.setImageResource(R.drawable.qr_code)
        }

        btnCerrar.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())

            builder.setTitle("Fragment alumno")
            builder.setMessage("¿Deseas cerrar la aplicación?")

            builder.setPositiveButton("Aceptar") { _, _ ->
                requireActivity().finish()
            }

            builder.setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(
                    requireContext(),
                    "Continuemos en la app",
                    Toast.LENGTH_SHORT
                ).show()
            }

            builder.show()
        }
    }
}