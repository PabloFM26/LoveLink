package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class ChatsActivity : Activity() {

    private var usuarioId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        // Obtener el ID del usuario desde el Intent
        usuarioId = intent.getLongExtra("usuario_id", -1L)

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
            val intent = Intent(this, MatchesActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_chats).setOnClickListener {
            Toast.makeText(this, "Ya estás en Chats 💬", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }
    }
}
