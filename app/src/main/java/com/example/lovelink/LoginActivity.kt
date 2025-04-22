package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.lovelink.models.Cuenta
import com.example.lovelink.models.Usuario
import com.example.lovelink.network.CuentaApi
import com.example.lovelink.network.UsuarioApi
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : Activity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicialización de vistas
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signupButton)

        // Botón de inicio de sesión
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cuenta = Cuenta(email = email, password = password, telefono = "")

            val cuentaApi = RetrofitClient.retrofit.create(CuentaApi::class.java)
            cuentaApi.login(cuenta).enqueue(object : Callback<Cuenta> {
                override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                    if (response.isSuccessful && response.body() != null) {
                        val cuentaRespuesta = response.body()
                        val idCuenta = cuentaRespuesta?.id ?: -1L

                        if (idCuenta == -1L) {
                            Toast.makeText(this@LoginActivity, "Error al obtener ID de cuenta", Toast.LENGTH_SHORT).show()
                            return
                        }

                        // Guardar login en SharedPreferences
                        getSharedPreferences("LoveLinkPrefs", MODE_PRIVATE).edit().apply {
                            putBoolean("isLoggedIn", true)
                            putString("email", email)
                            putLong("idCuenta", idCuenta)
                            apply()
                        }

                        // Obtener el usuario asociado a esa cuenta
                        val usuarioApi = RetrofitClient.retrofit.create(UsuarioApi::class.java)
                        usuarioApi.obtenerUsuarioPorCuenta(idCuenta).enqueue(object : Callback<Usuario> {
                            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                                if (response.isSuccessful && response.body() != null) {
                                    val usuario = response.body()!!
                                    val usuarioId = usuario.id ?: -1L

                                    // Guardar ID del usuario por si se necesita en otras Activities
                                    getSharedPreferences("LoveLinkPrefs", MODE_PRIVATE).edit().apply {
                                        putLong("usuarioId", usuarioId)
                                        apply()
                                    }

                                    // Ir a pantalla principal
                                    val intent = Intent(this@LoginActivity, PosiblesMatchesActivity::class.java)
                                    intent.putExtra("usuario_id", usuarioId)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@LoginActivity, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "Error obteniendo usuario: ${t.message}", Toast.LENGTH_LONG).show()
                            }
                        })

                    } else {
                        Toast.makeText(this@LoginActivity, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error de conexión: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }

        // Botón de registro
        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
