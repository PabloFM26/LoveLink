package com.example.lovelink

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.lovelink.models.ImagenesUsuario
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileSetup2Activity : AppCompatActivity() {

    private val imageUris: MutableList<Uri?> = MutableList(6) { null }
    private lateinit var imageSlots: Array<ImageView>
    private lateinit var finishButton: Button
    private var currentSlotIndex = 0
    private var usuarioId: Long = -1L

    private var currentPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_2)
        supportActionBar?.hide()

        usuarioId = intent.getLongExtra("usuario_id", -1L)
        if (usuarioId == -1L) {
            Toast.makeText(this, "Error: ID de usuario no recibido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        imageSlots = arrayOf(
            findViewById(R.id.imageSlot1),
            findViewById(R.id.imageSlot2),
            findViewById(R.id.imageSlot3),
            findViewById(R.id.imageSlot4),
            findViewById(R.id.imageSlot5),
            findViewById(R.id.imageSlot6)
        )

        finishButton = findViewById(R.id.finishButton)

        imageSlots.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                currentSlotIndex = index
                showImageSourceDialog()
            }
        }

        finishButton.setOnClickListener { subirImagenes() }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                if (which == 0) openCamera()
                else openGallery()
            }.show()
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "Imagen de perfil")
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        currentPhotoUri = uri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }


    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK && currentPhotoUri != null) {
            imageUris[currentSlotIndex] = currentPhotoUri
            Glide.with(this).load(currentPhotoUri).into(imageSlots[currentSlotIndex])
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imageUris[currentSlotIndex] = uri
                Glide.with(this).load(uri).into(imageSlots[currentSlotIndex])
            }
        }
    }


    private fun subirImagenes() {
        // Validamos que todas las imágenes estén seleccionadas
        val imagenesFaltantes = imageUris.withIndex().filter { it.value == null }.map { it.index + 1 }
        if (imagenesFaltantes.isNotEmpty()) {
            Toast.makeText(
                this,
                "Por favor, selecciona una imagen para todos los huecos (faltan: ${imagenesFaltantes.joinToString(", ")})",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val imagenes = ImagenesUsuario(
            idUsuario = usuarioId,
            imagen1 = imageUris[0]?.toString(),
            imagen2 = imageUris[1]?.toString(),
            imagen3 = imageUris[2]?.toString(),
            imagen4 = imageUris[3]?.toString(),
            imagen5 = imageUris[4]?.toString(),
            imagen6 = imageUris[5]?.toString()
        )

        RetrofitClient.imagenesUsuarioService.subirImagenes(imagenes).enqueue(object : Callback<ImagenesUsuario> {
            override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileSetup2Activity, "Imágenes guardadas", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProfileSetup2Activity, PosiblesMatchesActivity::class.java)
                    intent.putExtra("usuario_id", usuarioId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ProfileSetup2Activity, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImagenesUsuario>, t: Throwable) {
                Toast.makeText(this@ProfileSetup2Activity, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
