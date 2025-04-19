package com.example.lovelink

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class PerfilActivity : AppCompatActivity() {

    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var password2EditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var jobEditText: EditText
    private lateinit var heightEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var hobbyInput: EditText
    private lateinit var addHobbyButton: Button
    private lateinit var finishButton: Button
    private lateinit var hobbyListContainer: LinearLayout
    private lateinit var imageSlots: Array<ImageView>

    private var selectedGender = ""
    private var selectedOrientation = ""
    private var selectedZodiac = ""
    private var selectedIntention = ""
    private var currentSlotIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        supportActionBar?.hide()

        // Referencias
        phoneEditText = findViewById(R.id.phoneEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        password2EditText = findViewById(R.id.password2EditText)
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        birthdayEditText = findViewById(R.id.birthdayEditText)
        cityEditText = findViewById(R.id.cityEditText)
        jobEditText = findViewById(R.id.jobEditText)
        heightEditText = findViewById(R.id.heightEditText)
        finishButton = findViewById(R.id.finishButton)

        imageSlots = arrayOf(
            findViewById(R.id.imageSlot1), findViewById(R.id.imageSlot2), findViewById(R.id.imageSlot3),
            findViewById(R.id.imageSlot4), findViewById(R.id.imageSlot5), findViewById(R.id.imageSlot6)
        )

        imageSlots.forEachIndexed { index, imageView ->
            imageView.setOnClickListener { openGalleryOrCamera(index) }
        }

        setupTextOptionSelectors(
            listOf(R.id.genderMale, R.id.genderFemale, R.id.genderOther, R.id.genderNoSay)
        ) { selectedGender = it }

        setupTextOptionSelectors(
            listOf(R.id.orientationHetero, R.id.orientationHomo, R.id.orientationBi, R.id.orientationPan, R.id.orientationAsexual, R.id.orientationOt)
        ) { selectedOrientation = it }

        setupTextOptionSelectors(
            listOf(R.id.zodiacAries, R.id.zodiacTaurus, R.id.zodiacGemini, R.id.zodiacCancer, R.id.zodiacLeo, R.id.zodiacVirgo,
                R.id.zodiacLibra, R.id.zodiacScorpio, R.id.zodiacSagittarius, R.id.zodiacCapricorn, R.id.zodiacAquarius, R.id.zodiacPisces)
        ) { selectedZodiac = it }

        setupTextOptionSelectors(
            listOf(R.id.intentionRelationship, R.id.intentionCasual, R.id.intentionFriendship, R.id.intentionUnknown)
        ) { selectedIntention = it }

        birthdayEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                birthdayEditText.setText("$d/${m + 1}/$y")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun openGalleryOrCamera(slotIndex: Int) {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                currentSlotIndex = slotIndex
                if (which == 0) openCamera() else openGallery()
            }.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(intent)
    }

    private fun updateImageSlot(uri: Uri?, index: Int) {
        uri?.let { imageSlots[index].setImageURI(it) }
    }

    private fun setupTextOptionSelectors(ids: List<Int>, onSelected: (String) -> Unit) {
        val views = ids.map { findViewById<TextView>(it) }
        views.forEach { view ->
            view.setOnClickListener {
                views.forEach {
                    it.setTextColor(ContextCompat.getColor(this, R.color.gender_unselected))
                    it.setBackgroundResource(R.drawable.gender_selected_magenta)
                }
                view.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                onSelected(view.text.toString())
            }
        }
    }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                updateImageSlot(imageUri, currentSlotIndex)
            }
        }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                updateImageSlot(imageUri, currentSlotIndex)
            }
        }

    companion object {
        private const val MAX_IMAGES = 6
    }
    private fun configurarNavegacionInferior() {
        findViewById<Button>(R.id.nav_home).setOnClickListener {
            Toast.makeText(this, "Ya estás en Inicio 🏠", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.nav_matches).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.nav_chats).setOnClickListener {
            startActivity(Intent(this, ChatsActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.nav_profile).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }
    }
}