package com.example.lovelink

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.lovelink.models.Cuenta
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class ForgotPasswordActivity : Activity() {

    private lateinit var emailEditText: EditText
    private lateinit var sendEmailButton: Button
    private lateinit var codeTextView: TextView
    private lateinit var codeEditText: EditText
    private lateinit var validateCodeButton: Button
    private lateinit var newPasswordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText
    private lateinit var changePasswordButton: Button

    private lateinit var emailSection: LinearLayout
    private lateinit var codeSection: LinearLayout
    private lateinit var newPasswordLayout: LinearLayout

    private var cuentaId: Long = -1L
    private var generatedCode: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Vistas
        emailEditText = findViewById(R.id.forgotEmailEditText)
        sendEmailButton = findViewById(R.id.sendRecoveryButton)
        codeTextView = findViewById(R.id.generatedCodeTextView)
        codeEditText = findViewById(R.id.codeInputEditText)
        validateCodeButton = findViewById(R.id.validateCodeButton)
        newPasswordEditText = findViewById(R.id.newPasswordEditText)
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        emailSection = findViewById(R.id.emailSection)
        codeSection = findViewById(R.id.codeSection)
        newPasswordLayout = findViewById(R.id.newPasswordLayout)

        // Ocultar secciones al inicio
        codeSection.visibility = View.GONE
        newPasswordLayout.visibility = View.GONE

        sendEmailButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            RetrofitClient.cuentaService.getCuentaPorEmail(email).enqueue(object : Callback<Cuenta> {
                override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                    if (response.isSuccessful && response.body() != null) {
                        cuentaId = response.body()!!.id ?: -1L
                        generatedCode = (100000..999999).random().toString()
                        codeTextView.text = "Código: $generatedCode"

                        // Mostrar sección de código
                        emailSection.visibility = View.GONE
                        codeSection.visibility = View.VISIBLE

                        Toast.makeText(this@ForgotPasswordActivity, "Código generado. Introdúcelo abajo", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Correo no registrado", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                    Toast.makeText(this@ForgotPasswordActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
        }

        validateCodeButton.setOnClickListener {
            val inputCode = codeEditText.text.toString().trim()
            if (inputCode == generatedCode) {
                // Mostrar campos de nueva contraseña
                codeSection.visibility = View.GONE
                newPasswordLayout.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Código incorrecto", Toast.LENGTH_SHORT).show()
            }
        }

        changePasswordButton.setOnClickListener {
            val newPass = newPasswordEditText.text.toString().trim()
            val repeatPass = repeatPasswordEditText.text.toString().trim()

            if (newPass.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != repeatPass) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hashed = hashSHA256(newPass)
            val updateMap = mapOf("password" to hashed)

            RetrofitClient.cuentaService.actualizarContrasena(cuentaId, updateMap).enqueue(object : Callback<Cuenta> {
                override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                    Toast.makeText(this@ForgotPasswordActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun hashSHA256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
