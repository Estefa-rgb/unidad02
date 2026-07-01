package com.example.unidad02

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        iniciarComponentes()
        if (savedInstanceState == null) {
            cambiarFrame(inicioFragment())
        }
        eventosClic()
    }

    public fun cambiarFrame(fragment : Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frmContenedor, fragment).commit()
    }

    fun iniciarComponentes() {
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.btnNavegador)
    }

    fun eventosClic() {
        bottomNavigationView.setOnItemSelectedListener { MenuItem ->
            when (MenuItem.itemId) {
                R.id.btnInicio -> {
                    cambiarFrame(inicioFragment())
                    true
                }

                R.id.btnAcerca -> {
                    cambiarFrame(acercaDeFragment())
                    true
                }

                R.id.btnAlumno -> {
                    cambiarFrame(alumnoFragment())
                    true
                }

                R.id.btnLista -> {
                    cambiarFrame(listaFragment())
                    true
                }

                R.id.btnSalir -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("App Unidad02")
                    builder.setMessage("¿Deseas cerrar la aplicacion?")
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                        finish()
                    }
                    builder.setNegativeButton("Cancelar") { dialog, which ->
                        Toast.makeText(applicationContext, "Continuemos en la app", Toast.LENGTH_SHORT)
                            .show()
                    }
                    builder.show()
                    true
                }

                else -> false
            }
        }
    }
}