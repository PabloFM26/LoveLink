package com.example.lovelink

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup_2)
        supportActionBar?.hide()

        // Recuperamos el ID del usuario del intent
        usuarioId = intent.getLongExtra("usuario_id", -1)
        if (usuarioId == -1L) {
            Toast.makeText(this, "Error: ID de usuario no recibido", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Toast.makeText(this, "ID usuario recibido: $usuarioId", Toast.LENGTH_SHORT).show()


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
            imageView.setOnClickListener { openGalleryOrCamera(index) }
        }

        finishButton.setOnClickListener { subirImagenes() }
    }

    private fun openGalleryOrCamera(slotIndex: Int) {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                if (which == 0) openCamera(slotIndex)
                else openGallery(slotIndex)
            }.show()
    }

    private fun openCamera(slotIndex: Int) {
        val photoUri = createImageUri()
        imageUris[slotIndex] = photoUri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
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

    private fun getRealPathFromURI(contentUri: Uri?): String? {
        contentUri ?: return null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(contentUri, proj, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return null
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            imageSlots[currentSlotIndex].setImageURI(imageUris[currentSlotIndex])
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUris[currentSlotIndex] = result.data?.data
            imageSlots[currentSlotIndex].setImageURI(imageUris[currentSlotIndex])
        }
    }

    private fun subirImagenes() {
        val imagenes = ImagenesUsuario(
            idUsuario = usuarioId,
            imagen1 = getRealPathFromURI(imageUris[0]),
            imagen2 = getRealPathFromURI(imageUris[1]),
            imagen3 = getRealPathFromURI(imageUris[2]),
            imagen4 = getRealPathFromURI(imageUris[3]),
            imagen5 = getRealPathFromURI(imageUris[4]),
            imagen6 = getRealPathFromURI(imageUris[5])
        )

        RetrofitClient.imagenesUsuarioService.subirImagenes(imagenes).enqueue(object : Callback<ImagenesUsuario> {
            override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ProfileSetup2Activity, "Imágenes guardadas con éxito", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ProfileSetup2Activity, PosiblesMatchesActivity::class.java)
                    intent.putExtra("usuario_id", usuarioId)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ProfileSetup2Activity, "Error al guardar imágenes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImagenesUsuario>, t: Throwable) {
                Toast.makeText(this@ProfileSetup2Activity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
