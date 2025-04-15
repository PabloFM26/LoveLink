package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignUpActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Referencias a los campos del layout
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText) // Este campo es el teléfono
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val password2EditText = findViewById<EditText>(R.id.password2EditText)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener { v: View? ->
            val phone = phoneEditText.text.toString().trim { it <= ' ' }
            val email = emailEditText.text.toString().trim { it <= ' ' }
            val password = passwordEditText.text.toString()
            val password2 = password2EditText.text.toString()
            if (phone.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Rellena todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password != password2) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Guardar los datos temporalmente en SharedPreferences para ser utilizados después
                val editor = getSharedPreferences(
                    "LoveLinkPrefs",
                    MODE_PRIVATE
                ).edit()
                editor.putBoolean("isLoggedIn", true)
                editor.putString("phone", phone)
                editor.putString("email", email)
                editor.putString("password", password)
                editor.apply()

                // Pasar los datos a la primera pantalla (Formulario 1)
                val intent =
                    Intent(
                        this@SignUpActivity,
                        ProfileSetup1Activity::class.java
                    )
                intent.putExtra("phone", phone)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
                finish()
            }
        }
    }
}
