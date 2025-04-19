package com.example.lovelink

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.lovelink.models.Cuenta
import com.example.lovelink.models.Usuario
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProfileSetup1Activity : Activity() {

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var cityEditText: EditText
    private var selectedGender: String = ""
    private var userAge: Int = 0
    private var cuentaId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_1)

        // Llamada para obtener el último ID de cuenta
        obtenerUltimoIdCuenta()

        // Referencias UI
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        birthdayEditText = findViewById(R.id.birthdayEditText)
        cityEditText = findViewById(R.id.cityEditText)
        val continueButton = findViewById<Button>(R.id.continueButton)

        // Género
        val genderOptions = arrayOf(
            findViewById<TextView>(R.id.genderMale),
            findViewById<TextView>(R.id.genderFemale),
            findViewById<TextView>(R.id.genderOther),
            findViewById<TextView>(R.id.genderNoSay)
        )

        genderOptions.forEach { genderView ->
            genderView.setOnClickListener {
                genderOptions.forEach {
                    it.isSelected = false
                    it.setTextColor(ContextCompat.getColor(this, R.color.gender_unselected))
                }
                genderView.isSelected = true
                genderView.setTextColor(ContextCompat.getColor(this, R.color.gender_selected))
                selectedGender = genderView.text.toString()
            }
        }

        // Fecha de nacimiento
        birthdayEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                birthdayEditText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")

                val today = Calendar.getInstance()
                val birthDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }

                userAge = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) userAge--
            }, year, month, day).show()
        }

        // Botón continuar
        continueButton.setOnClickListener {
            if (cuentaId == -1L) {
                Toast.makeText(this, "ID de cuenta no disponible", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = nameEditText.text.toString().trim()
            val surname = surnameEditText.text.toString().trim()
            val birthday = birthdayEditText.text.toString().trim()
            val city = cityEditText.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || birthday.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedGender.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona un género", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = Usuario(
                id_cuenta = cuentaId,
                nombre = name,
                apellidos = surname,
                genero = selectedGender,
                localidad = city,
                edad = userAge,
                orientacionSexual = "N/A",
                signoZodiaco = "N/A",
                intencion = "N/A",
                altura = -1
            )

            RetrofitClient.usuarioService.crearUsuario(usuario).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        val usuarioCreado = response.body()
                        Toast.makeText(this@ProfileSetup1Activity, "Datos guardados", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@ProfileSetup1Activity, ProfileSetup2Activity::class.java).apply {
                            putExtra("usuarioId", usuarioCreado?.id_usuario ?: -1)
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@ProfileSetup1Activity, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                        Log.e("Perfil", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(this@ProfileSetup1Activity, "Fallo de red", Toast.LENGTH_SHORT).show()
                    Log.e("Perfil", "Error de red", t)
                }
            })
        }
    }

    private fun obtenerUltimoIdCuenta() {
        RetrofitClient.cuentaService.obtenerUltimaCuenta().enqueue(object : Callback<Cuenta> {
            override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                if (response.isSuccessful) {
                    val cuenta = response.body()
                    cuentaId = (cuenta?.id ?: -1).toLong()
                    Log.d("ProfileSetup", "Última cuenta ID: $cuentaId")
                } else {
                    Toast.makeText(this@ProfileSetup1Activity, "Error al obtener la cuenta", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileSetup", "Código: ${response.code()} - ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                Toast.makeText(this@ProfileSetup1Activity, "Error de red al obtener cuenta", Toast.LENGTH_SHORT).show()
                Log.e("ProfileSetup", "Error de red", t)
            }
        })
    }
}
