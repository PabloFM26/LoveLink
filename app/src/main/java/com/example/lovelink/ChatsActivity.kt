package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast

class ChatsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

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
            Toast.makeText(this, "Ya estás en Chats 💬", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }
    }
}
