package com.example.lovelink

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.lovelink.models.ImagenesUsuario
import com.example.lovelink.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

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
            findViewById(R.id.imageSlot1), findViewById(R.id.imageSlot2),
            findViewById(R.id.imageSlot3), findViewById(R.id.imageSlot4),
            findViewById(R.id.imageSlot5), findViewById(R.id.imageSlot6)
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
                if (which == 0) openCamera() else openGallery()
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
            uri?.let {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                imageUris[currentSlotIndex] = it
                Glide.with(this).load(it).into(imageSlots[currentSlotIndex])
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("URI inválido")
        val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()
        return file
    }

    private fun subirImagenes() {
        val urls = MutableList(6) { "" }
        var subidas = 0

        val faltantes = imageUris.withIndex().filter { it.value == null }.map { it.index + 1 }
        if (faltantes.isNotEmpty()) {
            Toast.makeText(this, "Selecciona todas las imágenes (faltan: ${faltantes.joinToString()})", Toast.LENGTH_LONG).show()
            return
        }

        imageUris.forEachIndexed { index, uri ->
            val file = uriToFile(uri!!)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagenPart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val idUsuarioPart = usuarioId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val numeroPart = (index + 1).toString().toRequestBody("text/plain".toMediaTypeOrNull())

            RetrofitClient.imagenesUsuarioService.subirImagenIndividual(
                imagenPart, idUsuarioPart, numeroPart
            ).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        urls[index] = response.body()?.string().orEmpty()
                        subidas++
                        if (subidas == 6) {
                            Toast.makeText(this@ProfileSetup2Activity, "", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@ProfileSetup2Activity, PosiblesMatchesActivity::class.java).apply {
                                putExtra("usuario_id", usuarioId)
                            })
                            finish()
                        }
                    } else {
                        Toast.makeText(this@ProfileSetup2Activity, "Error al subir imagen ${index + 1}", Toast.LENGTH_SHORT).show()
                    }
                }


                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("UPLOAD_ERROR", "Fallo de red imagen ${index + 1}", t)
                    Toast.makeText(this@ProfileSetup2Activity, "Fallo de red imagen ${index + 1}", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

}
