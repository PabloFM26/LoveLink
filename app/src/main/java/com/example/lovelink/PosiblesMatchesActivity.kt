package com.example.lovelink

import android.app.Activity
import android.content.Intent
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
    private var usuarioId: Long = -1L // Propiedad que guarda el ID recibido

    data class Usuario(val nombre: String, val edad: Int, val localidad: String, val imagenResId: Int)

    private val usuarios = listOf(
        Usuario("Jose", 25, "Madrid", R.drawable.sampleuseri),
        Usuario("Ana", 30, "Valencia", R.drawable.sampleuserii),
        Usuario("Carlos", 28, "Sevilla", R.drawable.sampleuseriii)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posibles_matches)

        // Recoge el ID del usuario que se pasó por intent
        usuarioId = intent.getLongExtra("usuario_id", -1L)
        Toast.makeText(this, "ID recibido: $usuarioId", Toast.LENGTH_SHORT).show()

        // Referencias UI
        btnLike = findViewById(R.id.btnLike)
        btnNope = findViewById(R.id.btnNope)
        nombreEdadText = findViewById(R.id.nombreEdadText)
        localidadText = findViewById(R.id.localidadText)
        imagenUsuario = findViewById(R.id.imagenUsuario)

        mostrarUsuarioActual()

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

        configurarNavegacionInferior()
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

    private fun configurarNavegacionInferior() {
        findViewById<Button>(R.id.nav_home).setOnClickListener {
            Toast.makeText(this, "Ya estás en Inicio 🏠", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.nav_matches).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.nav_chats).setOnClickListener {
            startActivity(Intent(this, ChatsActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }
    }
}
