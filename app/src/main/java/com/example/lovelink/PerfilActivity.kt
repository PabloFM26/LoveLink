package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast

class PerfilActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        configurarNavegacionInferior()
    }

    private fun configurarNavegacionInferior() {
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, PosiblesMatchesActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.nav_matches).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.nav_chats).setOnClickListener {
            startActivity(Intent(this, ChatsActivity::class.java))
            finish()
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            Toast.makeText(this, "Ya estás en Perfil 👤", Toast.LENGTH_SHORT).show()
        }
    }
}
