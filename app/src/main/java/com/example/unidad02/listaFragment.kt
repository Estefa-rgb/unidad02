package com.example.unidad02

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unidad02.ADO.Alumno
import com.example.unidad02.ADO.AlumnoDB
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        adapter.setOnClickListener {
            val pos: Int = rcvLista.getChildLayoutPosition(it)
            val alumnoSeleccionado: Alumno = adapter.getAlumno(pos)

            val bundle = Bundle().apply {
                putSerializable("miAlumno", alumnoSeleccionado)
            }

            val fragment = alumnoFragment()
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.frmContenedor, fragment)
                .addToBackStack(null)
                .commit()

            val botonNavegador = requireActivity().findViewById<BottomNavigationView>(R.id.btnNavegador)
            botonNavegador.menu.findItem(R.id.btnAlumno).isChecked = true
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val alumnoSeleccionado = adapter.getAlumno(position)

                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirmar acción")
                builder.setCancelable(false) // Evita que se cierre al tocar fuera de la alerta

                if (direction == ItemTouchHelper.LEFT) {
                    builder.setMessage("¿Deseas eliminar a ${alumnoSeleccionado.nombre} de la base de datos de forma permanente?")

                    builder.setPositiveButton("Sí") { _, _ ->
                        db.openDataBase()
                        db.deleteAlumno(alumnoSeleccionado)
                        db.closeDataBase()

                        listaAlumno.remove(alumnoSeleccionado)
                        adapter.actualizarLista(listaAlumno)

                        Toast.makeText(requireContext(), "Se ha eliminado de la base de datos", Toast.LENGTH_SHORT).show()
                    }

                    builder.setNegativeButton("No") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }

                } else if (direction == ItemTouchHelper.RIGHT) {
                    builder.setMessage("¿Deseas quitar a ${alumnoSeleccionado.nombre} solo de la lista visual?")

                    builder.setPositiveButton("Sí") { _, _ ->
                        listaAlumno.remove(alumnoSeleccionado)
                        adapter.actualizarLista(listaAlumno)

                        Toast.makeText(requireContext(), "Se ha eliminado de la lista", Toast.LENGTH_SHORT).show()
                    }

                    builder.setNegativeButton("No") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                }

                builder.show()
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

                val paint = Paint().apply {
                    color = Color.WHITE
                    textSize = 42f
                    isAntiAlias = true
                    isFakeBoldText = true
                }

                val textY = itemView.top.toFloat() + (itemView.height.toFloat() / 2) + (paint.textSize / 3)
                val horizontalMargin = 50

                if (dX < 0) {
                    val background = ColorDrawable(Color.parseColor("#F44336"))
                    val deleteIcon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)

                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)

                    deleteIcon?.let {
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconRight = itemView.right - horizontalMargin
                        val iconLeft = iconRight - it.intrinsicWidth

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)

                        paint.textAlign = Paint.Align.RIGHT
                        c.drawText("Eliminar de la BD", iconLeft.toFloat() - 20f, textY, paint)
                    }
                } else if (dX > 0) {
                    val background = ColorDrawable(Color.parseColor("#FF9800"))
                    val removeIcon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_close_clear_cancel)

                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    background.draw(c)

                    removeIcon?.let {
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconBottom = iconTop + it.intrinsicHeight
                        val iconLeft = itemView.left + horizontalMargin
                        val iconRight = iconLeft + it.intrinsicWidth

                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)

                        paint.textAlign = Paint.Align.LEFT
                        c.drawText("Eliminar de la lista", iconRight.toFloat() + 20f, textY, paint)
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