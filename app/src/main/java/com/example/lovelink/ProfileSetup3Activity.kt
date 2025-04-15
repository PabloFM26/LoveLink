package com.example.lovelink

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lovelink.models.User
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileSetup3Activity : AppCompatActivity() {

    private val imageUris: MutableList<Uri> = mutableListOf()

    private lateinit var hobbyListContainer: LinearLayout
    private lateinit var bioEditText: EditText
    private lateinit var hobbyInput: EditText
    private lateinit var addHobbyButton: Button
    private lateinit var finishButton: Button
    private lateinit var imageSlots: Array<ImageView>

    private lateinit var passedIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_3)

        supportActionBar?.hide()

        passedIntent = intent

        // Referencias
        hobbyListContainer = findViewById(R.id.hobbyListContainer)
        bioEditText = findViewById(R.id.bioEditText)
        hobbyInput = findViewById(R.id.hobbyInput)
        addHobbyButton = findViewById(R.id.addHobbyButton)
        finishButton = findViewById(R.id.finishButton)

        imageSlots = arrayOf(
            findViewById(R.id.imageSlot1),
            findViewById(R.id.imageSlot2),
            findViewById(R.id.imageSlot3),
            findViewById(R.id.imageSlot4),
            findViewById(R.id.imageSlot5),
            findViewById(R.id.imageSlot6)
        )

        imageSlots.forEachIndexed { index, imageView ->
            imageView.setOnClickListener { openGalleryOrCamera(index) }
        }

        addHobbyButton.setOnClickListener { addHobby() }
        finishButton.setOnClickListener { finishProfileSetup() }
    }

    private fun openGalleryOrCamera(slotIndex: Int) {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                if (which == 0) openCamera(slotIndex)
                else openGallery(slotIndex)
            }
            .show()
    }

    private fun openCamera(slotIndex: Int) {
        val photoUri = createImageUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        currentSlotIndex = slotIndex
        cameraActivityResultLauncher.launch(intent)
    }

    private fun openGallery(slotIndex: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        currentSlotIndex = slotIndex
        galleryActivityResultLauncher.launch(intent)
    }

    private fun createImageUri(): Uri? {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "ProfileImage")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun updateImageSlot(imageUri: Uri?, slotIndex: Int) {
        imageUri?.let {
            if (slotIndex < MAX_IMAGES) {
                imageUris.add(it)
                imageSlots[slotIndex].setImageURI(it)
            }
        }
    }

    private fun addHobby() {
        val hobby = hobbyInput.text.toString().trim()
        if (hobby.isNotEmpty() && hobbyListContainer.childCount < 5) {
            val hobbyButton = Button(this).apply {
                text = hobby
                setBackgroundResource(R.drawable.rounded_button)
                setTextColor(resources.getColor(R.color.black, theme))
                setBackgroundColor(resources.getColor(R.color.magenta_500, theme))

                setOnClickListener {
                    hobbyListContainer.removeView(this)
                }
            }
            hobbyListContainer.addView(hobbyButton)
            hobbyInput.setText("")
        } else {
            Toast.makeText(this, "Solo puedes agregar hasta 5 hobbies", Toast.LENGTH_SHORT).show()
        }
    }

    private fun finishProfileSetup() {
        val user = createUserFromIntent()
        if (user == null) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.userService.registerUser(user).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileSetup3Activity, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileSetup3Activity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ProfileSetup3Activity, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Toast.makeText(this@ProfileSetup3Activity, "Error en la conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createUserFromIntent(): User? {
        val bio = bioEditText.text.toString()
        val hobbies = mutableListOf<String>()

        for (i in 0 until hobbyListContainer.childCount) {
            val hobbyButton = hobbyListContainer.getChildAt(i) as Button
            hobbies.add(hobbyButton.text.toString())
        }

        if (bio.isEmpty() || hobbies.isEmpty() || imageUris.isEmpty()) return null

        val phone = passedIntent.getStringExtra("phone")
        val email = passedIntent.getStringExtra("email")
        val password = passedIntent.getStringExtra("password")
        val name = passedIntent.getStringExtra("name")
        val surname = passedIntent.getStringExtra("surname")
        val gender = passedIntent.getStringExtra("gender")
        val birthday = passedIntent.getStringExtra("birthday")
        val age = passedIntent.getIntExtra("age", 0)
        val city = passedIntent.getStringExtra("city")
        val orientation = passedIntent.getStringExtra("orientation")
        val zodiac = passedIntent.getStringExtra("zodiac")
        val intention = passedIntent.getStringExtra("intention")
        val job = passedIntent.getStringExtra("job")
        val height = passedIntent.getStringExtra("height")

        val images = getImagePaths(imageUris)

        return if (listOfNotNull(phone, email, password, name, surname, gender, birthday, city, orientation, zodiac, intention, job, height).size < 13)
            null
        else
            User(
                name!!, surname!!, email!!, phone!!, password!!, gender!!,
                city!!, age, orientation!!, zodiac!!, bio,
                hobbies.joinToString(","), job!!, intention!!, height!!, images
            )
    }

    private fun getImagePaths(imageUris: List<Uri>): String {
        return imageUris.mapNotNull { getRealPathFromURI(it) }.joinToString(",")
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(contentUri, proj, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return null
    }

    // Slot index temporal para saber dónde colocar la imagen
    private var currentSlotIndex = 0

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val photoUri = createImageUri() // Asegúrate de manejar la URI real si es necesario
                updateImageSlot(photoUri, currentSlotIndex)
            }
        }

    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri = result.data?.data
                updateImageSlot(selectedImageUri, currentSlotIndex)
            }
        }

    companion object {
        private const val MAX_IMAGES = 6
    }
}
