package com.example.unidad02

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.unidad02.ADO.Alumno

class DbAdapter(
    private val context: Context,
    private var listAlumno: ArrayList<Alumno>
) : RecyclerView.Adapter<DbAdapter.ViewHolder>(), View.OnClickListener, Filterable {

    var listener: View.OnClickListener? = null
    var listaAlumCompleta: ArrayList<Alumno> = ArrayList(listAlumno)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtMatricula: TextView = itemView.findViewById(R.id.txtMatricula)
        val txtDomicilio: TextView = itemView.findViewById(R.id.txtDomicilio)
        val txtEspecialidad: TextView = itemView.findViewById(R.id.txtCarrera)
        val imgAlumno: ImageView = itemView.findViewById(R.id.imgAlumno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.alumno_item, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alumno = listAlumno[position]
        holder.txtNombre.text = alumno.nombre
        holder.txtDomicilio.text = alumno.domicilio
        holder.txtMatricula.text = "Matrícula: ${alumno.matricula}"
        holder.txtEspecialidad.text = alumno.especialidad

        if (alumno.foto.isNullOrEmpty()) {
            holder.imgAlumno.setImageResource(R.drawable.alumnos)
        } else {
            Glide.with(context)
                .load(alumno.foto)
                .placeholder(R.drawable.alumnos)
                .error(R.drawable.alumnos)
                .into(holder.imgAlumno)
        }
    }

    override fun getItemCount(): Int {
        return listAlumno.size
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    override fun onClick(p0: View?) {
        listener?.onClick(p0)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val resultados = FilterResults()
                if (constraint.isNullOrEmpty()) {
                    resultados.values = listaAlumCompleta
                } else {
                    val filtro = constraint.toString().lowercase()
                    val filtrados = ArrayList<Alumno>()
                    for (alumno in listaAlumCompleta) {
                        if (alumno.nombre.lowercase().contains(filtro) ||
                            alumno.matricula.lowercase().contains(filtro)
                        ) {
                            filtrados.add(alumno)
                        }
                    }
                    resultados.values = filtrados
                }
                return resultados
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listAlumno = results?.values as ArrayList<Alumno>
                notifyDataSetChanged()
            }
        }
    }

    fun getAlumno(pos: Int): Alumno {
        return listAlumno[pos]
    }

    fun actualizarLista(lista: ArrayList<Alumno>) {
        listAlumno = lista
        listaAlumCompleta = ArrayList(lista)
        notifyDataSetChanged()
    }
}