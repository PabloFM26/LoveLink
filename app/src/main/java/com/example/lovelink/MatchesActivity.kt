package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat

class MatchesActivity : Activity() {

    data class Usuario(val nombre: String, val edad: Int, val localidad: String, val imagenResId: Int)

    private lateinit var matchesContainer: LinearLayout
    private var usuarioId: Long = -1L

    private val matches = listOf(
        Usuario("Diego", 36, "Barcelona", R.drawable.sampleuseri),
        Usuario("María", 26, "Zaragoza", R.drawable.sampleuserii)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)

        // Obtener el ID del usuario recibido
        usuarioId = intent.getLongExtra("usuario_id", -1L)

        matchesContainer = findViewById(R.id.matchesContainer)

        if (matches.isEmpty()) {
            val mensaje = TextView(this).apply {
                text = "Esto está demasiado vacío...\n¡Ve a dar likes! 💥"
                textSize = 18f
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            matchesContainer.addView(mensaje)
        } else {
            for (usuario in matches) {
                val item = layoutInflater.inflate(R.layout.item_match, null)

                val nombreEdad = item.findViewById<TextView>(R.id.matchNombreEdad)
                val localidad = item.findViewById<TextView>(R.id.matchLocalidad)
                val imagen = item.findViewById<ImageView>(R.id.matchImagen)

                nombreEdad.text = "${usuario.nombre}, ${usuario.edad}"
                localidad.text = usuario.localidad
                imagen.setImageResource(usuario.imagenResId)

                matchesContainer.addView(item)
            }
        }

        configurarNavegacionInferior()
    }

    private fun configurarNavegacionInferior() {
        findViewById<Button>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, PosiblesMatchesActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_matches).setOnClickListener {
            Toast.makeText(this, "Ya estás en Matches 💘", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.nav_chats).setOnClickListener {
            val intent = Intent(this, ChatsActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }
    }
}
