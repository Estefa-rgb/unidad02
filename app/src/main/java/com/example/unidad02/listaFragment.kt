package com.example.unidad02

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
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
            val fragment = inicioFragment()
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

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alumnoAEliminar = adapter.getAlumno(position)

                db.openDataBase()
                db.deleteAlumno(alumnoAEliminar)
                db.closeDataBase()

                listaAlumno.remove(alumnoAEliminar)
                adapter.actualizarLista(listaAlumno)

                Toast.makeText(requireContext(), "Alumno eliminado exitosamente", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val background = ColorDrawable(Color.parseColor("#F44336"))

                val deleteIcon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)

                if (dX < 0) {

                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    background.draw(c)

                    deleteIcon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.right - iconMargin - it.intrinsicWidth
                        val iconRight = itemView.right - iconMargin

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rcvLista)
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