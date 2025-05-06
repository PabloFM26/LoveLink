// SignUpActivity.kt
package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.lovelink.models.Cuenta
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class SignUpActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val password2EditText = findViewById<EditText>(R.id.password2EditText)
        val signupButton = findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            val telefono = phoneEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val password2 = password2EditText.text.toString()

            if (telefono.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Regex("^6\\d{8}$").matches(telefono)) {
                Toast.makeText(this, "El teléfono debe comenzar por 6 y tener 9 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6 || !password.contains(Regex("[^A-Za-z0-9]"))) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres y un carácter especial", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != password2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashedPassword = hashSHA256(password)

            val cuenta = Cuenta(
                telefono = telefono,
                email = email,
                password = hashedPassword
            )

            RetrofitClient.cuentaService.registrarCuenta(cuenta).enqueue(object : Callback<Cuenta> {
                override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                    if (response.isSuccessful) {
                        val cuentaCreada = response.body()
                        if (cuentaCreada != null) {
                            val intent = Intent(this@SignUpActivity, ProfileSetup1Activity::class.java)
                            intent.putExtra("cuenta_id", cuentaCreada.id)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        when {
                            errorBody?.contains("telefono") == true -> {
                                Toast.makeText(this@SignUpActivity, "El teléfono ya está registrado", Toast.LENGTH_SHORT).show()
                            }
                            errorBody?.contains("email") == true -> {
                                Toast.makeText(this@SignUpActivity, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this@SignUpActivity, "Error al registrar cuenta", Toast.LENGTH_SHORT).show()
                            }
                        }
                        Log.e("SignUp", "Error: ${response.code()} - $errorBody")
                    }
                }

                override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "Fallo de red: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("SignUp", "Error de red", t)
                }
            })
        }
    }

    private fun hashSHA256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
