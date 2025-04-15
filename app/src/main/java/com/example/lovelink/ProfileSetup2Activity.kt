package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.lovelink.ProfileSetup3Activity
import java.util.Arrays

class ProfileSetup2Activity : Activity() {
    private var selectedOrientation = ""
    private var selectedZodiac = ""
    private var selectedIntention = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_2)

        val jobEditText = findViewById<EditText>(R.id.jobEditText)
        val heightEditText = findViewById<EditText>(R.id.heightEditText)
        val nextButton = findViewById<Button>(R.id.nextButton)

        // === ORIENTACIÓN SEXUAL ===
        val orientationOptions = Arrays.asList(
            findViewById(R.id.orientationHetero),
            findViewById(R.id.orientationHomo),
            findViewById(R.id.orientationBi),
            findViewById(R.id.orientationPan),
            findViewById(R.id.orientationAsexual),
            findViewById<TextView>(R.id.orientationOt)
        )

        val orientationClickListener = View.OnClickListener { view: View ->
            for (option in orientationOptions) {
                option.isSelected = false
                option.setTextColor(resources.getColor(R.color.gender_unselected))
                option.setBackgroundResource(R.drawable.gender_selected_magenta)
            }
            view.isSelected = true
            (view as TextView).setTextColor(resources.getColor(android.R.color.black))
            view.setBackgroundResource(R.drawable.gender_selected_magenta)
            selectedOrientation = view.text.toString()
        }

        for (option in orientationOptions) {
            option.setOnClickListener(orientationClickListener)
        }

        // === SIGNO DEL ZODIACO ===
        val zodiacOptions = Arrays.asList(
            findViewById(R.id.zodiacAries),
            findViewById(R.id.zodiacTaurus),
            findViewById(R.id.zodiacGemini),
            findViewById(R.id.zodiacCancer),
            findViewById(R.id.zodiacLeo),
            findViewById(R.id.zodiacVirgo),
            findViewById(R.id.zodiacLibra),
            findViewById(R.id.zodiacScorpio),
            findViewById(R.id.zodiacSagittarius),
            findViewById(R.id.zodiacCapricorn),
            findViewById(R.id.zodiacAquarius),
            findViewById<TextView>(R.id.zodiacPisces)
        )

        val zodiacClickListener = View.OnClickListener { view: View ->
            for (option in zodiacOptions) {
                option.isSelected = false
                option.setTextColor(resources.getColor(R.color.gender_unselected))
                option.setBackgroundResource(R.drawable.gender_selected_magenta)
            }
            view.isSelected = true
            (view as TextView).setTextColor(resources.getColor(android.R.color.black))
            view.setBackgroundResource(R.drawable.gender_selected_magenta)
            selectedZodiac = view.text.toString()
        }

        for (option in zodiacOptions) {
            option.setOnClickListener(zodiacClickListener)
        }

        // === INTENCIÓN EN LA APP ===
        val intentionOptions = Arrays.asList(
            findViewById(R.id.intentionRelationship),
            findViewById(R.id.intentionCasual),
            findViewById(R.id.intentionFriendship),
            findViewById<TextView>(R.id.intentionUnknown)
        )

        val intentionClickListener = View.OnClickListener { view: View ->
            for (option in intentionOptions) {
                option.isSelected = false
                option.setTextColor(resources.getColor(R.color.gender_unselected))
                option.setBackgroundResource(R.drawable.gender_selected_magenta)
            }
            view.isSelected = true
            (view as TextView).setTextColor(resources.getColor(android.R.color.black))
            view.setBackgroundResource(R.drawable.gender_selected_magenta)
            selectedIntention = view.text.toString()
        }

        for (option in intentionOptions) {
            option.setOnClickListener(intentionClickListener)
        }

        // === BOTÓN CONTINUAR ===
        nextButton.setOnClickListener { v: View? ->
            val job = jobEditText.text.toString().trim { it <= ' ' }
            val height = heightEditText.text.toString().trim { it <= ' ' }

            if (selectedOrientation.isEmpty() || selectedZodiac.isEmpty() || selectedIntention.isEmpty()
                || job.isEmpty() || height.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Completa todos los campos antes de continuar",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val intent =
                Intent(
                    this@ProfileSetup2Activity,
                    ProfileSetup3Activity::class.java
                )
            intent.putExtra("orientation", selectedOrientation)
            intent.putExtra("zodiac", selectedZodiac)
            intent.putExtra("intention", selectedIntention)
            intent.putExtra("job", job)
            intent.putExtra("height", height)
            startActivity(intent)
        }
    }
}
