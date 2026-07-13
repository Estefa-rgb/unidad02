package com.example.unidad02

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unidad02.ADO.Alumno
import com.example.unidad02.ADO.AlumnoDB
import com.google.android.material.floatingactionbutton.FloatingActionButton

class listaFragment : Fragment() {

    private lateinit var btnInicio: FloatingActionButton
    private lateinit var adapter: DbAdapter
    private lateinit var rcvLista: RecyclerView
    private lateinit var listaAlumno: ArrayList<Alumno>
    private lateinit var srv: SearchView
    private lateinit var db: AlumnoDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista, container, false)
        iniciarComponentes(view)
        return view
    }

    private fun iniciarComponentes(view: View) {
        btnInicio = view.findViewById(R.id.btnAgregarAlumno)
        rcvLista = view.findViewById(R.id.recId)
        srv = view.findViewById(R.id.srvAlumnos)

        listaAlumno = ArrayList()
        db = AlumnoDB(requireContext())

        rcvLista.layoutManager = LinearLayoutManager(requireContext())
        adapter = DbAdapter(requireContext(), listaAlumno)
        rcvLista.adapter = adapter

        cargarAlumnos()

        btnInicio.setOnClickListener {
            val fragment = alumnoFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frmContenedor, fragment)
                .addToBackStack(null)
                .commit()
        }

        srv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun cargarAlumnos() {
        db.openDataBase()
        val alumnosDb = db.getAlumnos()
        val num: Int = alumnosDb.size

        Toast.makeText(
            requireContext(),
            "Número de alumnos: $num",
            Toast.LENGTH_SHORT
        ).show()

        db.closeDataBase()

        listaAlumno.clear()
        listaAlumno.addAll(alumnosDb)
        adapter.actualizarLista(listaAlumno)
    }
}