package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.lovelink.models.Cuenta
import com.example.lovelink.network.CuentaApi
import com.example.lovelink.network.RetrofitClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : Activity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cuenta = Cuenta(email = email, password = password, telefono = "") // Teléfono no necesario aquí

            val api = RetrofitClient.retrofit.create(CuentaApi::class.java)

            api.login(cuenta).enqueue(object : Callback<Cuenta> {
                override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@LoginActivity, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()

                        getSharedPreferences("LoveLinkPrefs", MODE_PRIVATE).edit().apply {
                            putBoolean("isLoggedIn", true)
                            putString("email", email)
                            apply()
                        }

                        val intent = Intent(this@LoginActivity, PosiblesMatchesActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error de conexión: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
