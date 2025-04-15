package com.example.lovelink

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat

class PosiblesMatchesActivity : Activity() {

    private lateinit var btnLike: Button
    private lateinit var btnNope: Button
    private lateinit var nombreEdadText: TextView
    private lateinit var localidadText: TextView
    private lateinit var imagenUsuario: ImageView

    private var indiceActual = 0

    data class Usuario(val nombre: String, val edad: Int, val localidad: String, val imagenResId: Int)

    private val usuarios = listOf(
        Usuario("Jose", 25, "Madrid", R.drawable.sampleuseri),
        Usuario("Ana", 30, "Valencia", R.drawable.sampleuserii),
        Usuario("Carlos", 28, "Sevilla", R.drawable.sampleuseriii)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posibles_matches)

        // Referencias UI
        btnLike = findViewById(R.id.btnLike)
        btnNope = findViewById(R.id.btnNope)
        nombreEdadText = findViewById(R.id.nombreEdadText)
        localidadText = findViewById(R.id.localidadText)
        imagenUsuario = findViewById(R.id.imagenUsuario)

        mostrarUsuarioActual()

        // Botones de acción
        btnLike.setOnClickListener {
            if (indiceActual < usuarios.size) {
                Toast.makeText(this, "¡Le diste Like a ${usuarios[indiceActual].nombre}! 💖", Toast.LENGTH_SHORT).show()
                avanzarUsuario()
            } else {
                Toast.makeText(this, "No hay más usuarios disponibles", Toast.LENGTH_SHORT).show()
            }
        }

        btnNope.setOnClickListener {
            if (indiceActual < usuarios.size) {
                Toast.makeText(this, "${usuarios[indiceActual].nombre} descartado 💔", Toast.LENGTH_SHORT).show()
                avanzarUsuario()
            } else {
                Toast.makeText(this, "No hay más usuarios disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarUsuarioActual() {
        if (indiceActual < usuarios.size) {
            val usuario = usuarios[indiceActual]
            nombreEdadText.text = "${usuario.nombre}, ${usuario.edad}"
            localidadText.text = usuario.localidad
            imagenUsuario.setImageDrawable(ContextCompat.getDrawable(this, usuario.imagenResId))
            imagenUsuario.scaleType = ImageView.ScaleType.CENTER_CROP

            btnLike.isEnabled = true
            btnNope.isEnabled = true
        } else {
            nombreEdadText.text = "No hay más usuarios"
            localidadText.text = ""
            imagenUsuario.setImageResource(android.R.color.transparent)

            btnLike.isEnabled = false
            btnNope.isEnabled = false
        }
    }

    private fun avanzarUsuario() {
        indiceActual++
        mostrarUsuarioActual()
    }
}
