package com.example.lovelink

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import java.util.*

class ProfileSetup1Activity : Activity() {

    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var cityEditText: EditText
    private var selectedGender: String = ""
    private var userAge: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_1)

        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        birthdayEditText = findViewById(R.id.birthdayEditText)
        cityEditText = findViewById(R.id.cityEditText)
        val continueButton = findViewById<Button>(R.id.continueButton)

        // Género
        val genderMale = findViewById<TextView>(R.id.genderMale)
        val genderFemale = findViewById<TextView>(R.id.genderFemale)
        val genderOther = findViewById<TextView>(R.id.genderOther)
        val genderNoSay = findViewById<TextView>(R.id.genderNoSay)
        val genderOptions = arrayOf(genderMale, genderFemale, genderOther, genderNoSay)

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
                if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                    userAge--
                }

            }, year, month, day).show()
        }

        // Continuar
        continueButton.setOnClickListener {
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

            val intent = Intent(this, ProfileSetup2Activity::class.java).apply {
                putExtra("name", name)
                putExtra("surname", surname)
                putExtra("gender", selectedGender)
                putExtra("age", userAge)
                putExtra("city", city)
            }
            startActivity(intent)

        }
    }
}
