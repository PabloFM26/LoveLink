package com.example.lovelink

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
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
    private lateinit var heightEditText: EditText

    private var selectedGender: String = ""
    private var selectedOrientation: String = "N/A"
    private var selectedZodiac: String = "N/A"
    private var selectedIntention: String = "N/A"

    private var userAge: Int = 0
    private var cuentaId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_1)

        cuentaId = intent.getLongExtra("cuenta_id", -1L)

        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        birthdayEditText = findViewById(R.id.birthdayEditText)
        cityEditText = findViewById(R.id.cityEditText)
        heightEditText = findViewById(R.id.heightEditText)
        val continueButton = findViewById<Button>(R.id.continueButton)

        configurarSelectores()

        birthdayEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                birthdayEditText.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                val today = Calendar.getInstance()
                val birthDate = Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay) }
                userAge = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) userAge--
            }, year, month, day).show()
        }

        continueButton.setOnClickListener {
            if (cuentaId == -1L) {
                Toast.makeText(this, "ID de cuenta no disponible", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val name = nameEditText.text.toString().trim()
            val surname = surnameEditText.text.toString().trim()
            val birthday = birthdayEditText.text.toString().trim()
            val city = cityEditText.text.toString().trim()
            val height = heightEditText.text.toString().toIntOrNull() ?: -1

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
                orientacionSexual = selectedOrientation,
                signoZodiaco = selectedZodiac,
                intencion = selectedIntention,
                altura = height
            )

            RetrofitClient.usuarioService.crearUsuario(usuario).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful && response.body() != null) {
                        val usuarioCreado = response.body()!!
                        val usuarioId = usuarioCreado.id

                        if (usuarioId != null && usuarioId > 0) {
                            Toast.makeText(this@ProfileSetup1Activity, "Datos guardados", Toast.LENGTH_SHORT).show()

                            Handler(mainLooper).postDelayed({
                                val intent = Intent(this@ProfileSetup1Activity, ProfileSetup2Activity::class.java).apply {
                                    putExtra("usuario_id", usuarioId)
                                }
                                startActivity(intent)
                                finish()
                            }, 500) // Espera 500ms para que MySQL respire
                        } else {
                            Toast.makeText(this@ProfileSetup1Activity, "ID de usuario no válido", Toast.LENGTH_SHORT).show()
                            Log.e("Perfil", "ID de usuario inválido: $usuarioId")
                        }
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

    private fun configurarSelectores() {
        val genderOptions = listOf(
            R.id.genderMale, R.id.genderFemale, R.id.genderOther, R.id.genderNoSay
        )
        val orientationOptions = listOf(
            R.id.orientationHetero, R.id.orientationHomo, R.id.orientationBi,
            R.id.orientationPan, R.id.orientationAsexual, R.id.orientationOt
        )
        val zodiacOptions = listOf(
            R.id.zodiacAries, R.id.zodiacTaurus, R.id.zodiacGemini, R.id.zodiacCancer,
            R.id.zodiacLeo, R.id.zodiacVirgo, R.id.zodiacLibra, R.id.zodiacScorpio,
            R.id.zodiacSagittarius, R.id.zodiacCapricorn, R.id.zodiacAquarius, R.id.zodiacPisces
        )
        val intentionOptions = listOf(
            R.id.intentionRelationship, R.id.intentionCasual,
            R.id.intentionFriendship, R.id.intentionUnknown
        )

        setupTextSelector(genderOptions) { selectedGender = it }
        setupTextSelector(orientationOptions) { selectedOrientation = it }
        setupTextSelector(zodiacOptions) { selectedZodiac = it }
        setupTextSelector(intentionOptions) { selectedIntention = it }
    }

    private fun setupTextSelector(ids: List<Int>, onSelected: (String) -> Unit) {
        val views = ids.map { findViewById<TextView>(it) }
        views.forEach { view ->
            view.setOnClickListener {
                views.forEach {
                    it.setTextColor(ContextCompat.getColor(this, R.color.gender_unselected))
                }
                view.setTextColor(ContextCompat.getColor(this, R.color.black))
                view.setBackgroundResource(R.drawable.gender_selector_blue)
                onSelected(view.text.toString())
            }
        }
    }
}
