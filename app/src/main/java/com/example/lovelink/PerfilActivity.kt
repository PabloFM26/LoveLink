package com.example.lovelink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lovelink.models.ImagenesUsuario
import com.example.lovelink.models.Usuario
import com.example.lovelink.models.Cuenta
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody


class PerfilActivity : AppCompatActivity() {

    private var usuarioId: Long = -1L
    private var cuentaId: Long = -1L
    private var imagenesId: Long = -1L

    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var password2EditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var surnameEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var heightEditText: EditText
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

        usuarioId = intent.getLongExtra("usuario_id", -1L)

        phoneEditText = findViewById(R.id.phoneEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        password2EditText = findViewById(R.id.newPassword2EditText)
        nameEditText = findViewById(R.id.nameEditText)
        surnameEditText = findViewById(R.id.surnameEditText)
        birthdayEditText = findViewById(R.id.birthdayEditText)
        cityEditText = findViewById(R.id.cityEditText)
        heightEditText = findViewById(R.id.heightEditText)

        birthdayEditText.isEnabled = false
        birthdayEditText.isFocusable = false
        birthdayEditText.setTextColor(ContextCompat.getColor(this, R.color.cyan_500))
        phoneEditText.isEnabled = false
        phoneEditText.isFocusable = false
        phoneEditText.setTextColor(ContextCompat.getColor(this, R.color.cyan_500))


        imageSlots = arrayOf(
            findViewById(R.id.imageSlot1), findViewById(R.id.imageSlot2),
            findViewById(R.id.imageSlot3), findViewById(R.id.imageSlot4),
            findViewById(R.id.imageSlot5), findViewById(R.id.imageSlot6)
        )

        imageSlots.forEachIndexed { index, imageView ->
            imageView.setOnClickListener { openGalleryOrCamera(index) }
        }

        setupTextOptionSelectors(
            listOf(R.id.genderMale, R.id.genderFemale, R.id.genderOther, R.id.genderNoSay)
        ) { selectedGender = it }

        setupTextOptionSelectors(
            listOf(R.id.orientationHetero, R.id.orientationHomo, R.id.orientationBi,
                R.id.orientationPan, R.id.orientationAsexual, R.id.orientationOt)
        ) { selectedOrientation = it }

        setupTextOptionSelectors(
            listOf(R.id.zodiacAries, R.id.zodiacTaurus, R.id.zodiacGemini, R.id.zodiacCancer,
                R.id.zodiacLeo, R.id.zodiacVirgo, R.id.zodiacLibra, R.id.zodiacScorpio,
                R.id.zodiacSagittarius, R.id.zodiacCapricorn, R.id.zodiacAquarius, R.id.zodiacPisces)
        ) { selectedZodiac = it }

        setupTextOptionSelectors(
            listOf(R.id.intentionRelationship, R.id.intentionCasual,
                R.id.intentionFriendship, R.id.intentionUnknown)
        ) { selectedIntention = it }

        configurarNavegacionInferior()
        cargarDatosUsuario()
        cargarImagenesUsuario()

        findViewById<Button>(R.id.apliButton2).setOnClickListener {
            actualizarCorreo()
        }
        findViewById<Button>(R.id.apliButton3).setOnClickListener {
            actualizarContrasena()
        }
        findViewById<Button>(R.id.apliButton4).setOnClickListener {
            actualizarUsuario()
        }
        findViewById<Button>(R.id.closeButton).setOnClickListener {
            cerrarSesion()
        }
        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            eliminarCuenta()
        }

    }

    private fun cargarDatosUsuario() {
        RetrofitClient.usuarioService.getUsuarioById(usuarioId).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                val usuario = response.body()
                if (usuario != null) {
                    nameEditText.setText(usuario.nombre)
                    surnameEditText.setText(usuario.apellidos)
                    cityEditText.setText(usuario.localidad)
                    heightEditText.setText(usuario.altura?.toString() ?: "")
                    birthdayEditText.setText(usuario.edad?.toString() ?: "")
                    seleccionarTexto(usuario.genero, usuario.orientacionSexual, usuario.signoZodiaco, usuario.intencion)
                    cargarCuenta(usuario.id_cuenta)
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error cargando usuario", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarCuenta(idCuenta: Long) {
        cuentaId = idCuenta

        RetrofitClient.cuentaService.obtenerCuentaPorId(idCuenta).enqueue(object : Callback<Cuenta> {
            override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                val cuenta = response.body()
                if (cuenta != null) {
                    emailEditText.setText(cuenta.email)
                    phoneEditText.setText(cuenta.telefono)
                }
            }
            override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error cargando datos de cuenta", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun cargarImagenesUsuario() {
        RetrofitClient.imagenesUsuarioService.getImagenesByUsuarioId(usuarioId)
            .enqueue(object : Callback<ImagenesUsuario> {
                override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                    val imagenes = response.body()
                    if (imagenes != null) {
                        imagenesId = imagenes.idImagen ?: -1L
                     }

                    val rutas = listOf(
                        imagenes?.imagen1, imagenes?.imagen2, imagenes?.imagen3,
                        imagenes?.imagen4, imagenes?.imagen5, imagenes?.imagen6
                    )
                    rutas.forEachIndexed { i, ruta ->
                        if (!ruta.isNullOrEmpty()) {
                            val uri = Uri.parse(ruta)
                            Glide.with(this@PerfilActivity)
                                .load(uri)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(imageSlots[i])
                            imageSlots[i].tag = uri.toString()
                        }
                    }
                }

                override fun onFailure(call: Call<ImagenesUsuario>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Error cargando imágenes", Toast.LENGTH_SHORT).show()
                }
            })
    }



    private fun seleccionarTexto(genero: String?, orientacion: String?, zodiaco: String?, intencion: String?) {
        val activar = { ids: IntArray, valor: String? ->
            ids.map { findViewById<TextView>(it) }.forEach { tv ->
                if (tv.text.toString().equals(valor, ignoreCase = true)) tv.performClick()
            }
        }
        activar(intArrayOf(R.id.genderMale, R.id.genderFemale, R.id.genderOther, R.id.genderNoSay), genero)
        activar(intArrayOf(R.id.orientationHetero, R.id.orientationHomo, R.id.orientationBi, R.id.orientationPan, R.id.orientationAsexual, R.id.orientationOt), orientacion)
        activar(intArrayOf(R.id.zodiacAries, R.id.zodiacTaurus, R.id.zodiacGemini, R.id.zodiacCancer, R.id.zodiacLeo, R.id.zodiacVirgo, R.id.zodiacLibra, R.id.zodiacScorpio, R.id.zodiacSagittarius, R.id.zodiacCapricorn, R.id.zodiacAquarius, R.id.zodiacPisces), zodiaco)
        activar(intArrayOf(R.id.intentionRelationship, R.id.intentionCasual, R.id.intentionFriendship, R.id.intentionUnknown), intencion)
    }

    private fun openGalleryOrCamera(slotIndex: Int) {
        val options = arrayOf("Tomar foto", "Seleccionar de la galería")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una opción")
            .setItems(options) { _, which ->
                currentSlotIndex = slotIndex
                if (which == 0) openCamera() else openGallery()
            }
            .show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResultLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        galleryActivityResultLauncher.launch(intent)
    }


    private fun setupTextOptionSelectors(ids: List<Int>, onSelected: (String) -> Unit) {
        val views = ids.map { findViewById<TextView>(it) }
        views.forEach { view ->
            view.setOnClickListener {
                views.forEach {
                    it.setTextColor(ContextCompat.getColor(this, R.color.gender_unselected))
                    it.setBackgroundResource(R.drawable.gender_selector)
                }
                view.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                view.setBackgroundResource(R.drawable.gender_selector_blue)
                onSelected(view.text.toString())
            }
        }
    }

    private fun configurarNavegacionInferior() {
        findViewById<Button>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, PosiblesMatchesActivity::class.java).putExtra("usuario_id", usuarioId))
            finish()
        }
        findViewById<Button>(R.id.nav_matches).setOnClickListener {
            startActivity(Intent(this, MatchesActivity::class.java).putExtra("usuario_id", usuarioId))
            finish()
        }
        findViewById<Button>(R.id.nav_chats).setOnClickListener {
            startActivity(Intent(this, ChatsActivity::class.java).putExtra("usuario_id", usuarioId))
            finish()
        }
        findViewById<Button>(R.id.nav_profile).setOnClickListener {
            Toast.makeText(this, "Ya estás en Perfil 👤", Toast.LENGTH_SHORT).show()
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
                if (imageUri != null) {
                    contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    updateImageSlot(imageUri, currentSlotIndex)
                }
            }
        }


    private fun actualizarCorreo() {
        val nuevoCorreo = emailEditText.text.toString().trim()
        if (!nuevoCorreo.contains("@") || !nuevoCorreo.contains(".")) {
            Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (nuevoCorreo.isEmpty()) {
            Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val emailUpdate = mapOf("email" to nuevoCorreo)

        RetrofitClient.cuentaService.actualizarCorreo(cuentaId, emailUpdate)
            .enqueue(object : Callback<Cuenta> {
                override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Correo actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al actualizar correo", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun actualizarContrasena() {
        val contrasenaActual = passwordEditText.text.toString().trim()
        val nuevaContrasena = findViewById<EditText>(R.id.newPasswordEditText).text.toString().trim()
        val repetirNuevaContrasena = password2EditText.text.toString().trim()

        // Validaciones
        if (contrasenaActual.isEmpty() || nuevaContrasena.isEmpty() || repetirNuevaContrasena.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (nuevaContrasena != repetirNuevaContrasena) {
            Toast.makeText(this, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        // Ahora validamos que la contraseña actual sea correcta
        RetrofitClient.cuentaService.obtenerCuentaPorId(cuentaId).enqueue(object : Callback<Cuenta> {
            override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                if (response.isSuccessful) {
                    val cuenta = response.body()
                    if (cuenta != null && cuenta.password == contrasenaActual) {
                        // Coincide, actualizar contraseña
                        val passwordUpdate = mapOf("password" to nuevaContrasena)

                        RetrofitClient.cuentaService.actualizarContrasena(cuentaId, passwordUpdate)
                            .enqueue(object : Callback<Cuenta> {
                                override fun onResponse(call: Call<Cuenta>, response: Response<Cuenta>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(this@PerfilActivity, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this@PerfilActivity, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                                    Toast.makeText(this@PerfilActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(this@PerfilActivity, "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al comprobar la contraseña actual", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Cuenta>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarUsuario() {
        val nombre = nameEditText.text.toString().trim()
        val apellidos = surnameEditText.text.toString().trim()
        val genero = selectedGender
        val localidad = cityEditText.text.toString().trim()
        val orientacionSexual = selectedOrientation
        val signoZodiaco = selectedZodiac
        val intencion = selectedIntention
        val altura = heightEditText.text.toString().toIntOrNull()

        if (nombre.isEmpty() || apellidos.isEmpty() || localidad.isEmpty() || altura == null) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (altura == null || altura < 100 || altura > 250) {
            Toast.makeText(this, "Altura debe estar entre 100 y 250 cm", Toast.LENGTH_SHORT).show()
            return
        }


        val usuarioActualizado = Usuario(
            id = usuarioId,   // No se va a cambiar
            id_cuenta = cuentaId,      // No se va a cambiar
            nombre = nombre,
            apellidos = apellidos,
            genero = genero,
            localidad = localidad,
            edad = null,  // No tocamos edad
            orientacionSexual = orientacionSexual,
            signoZodiaco = signoZodiaco,
            intencion = intencion,
            altura = altura
        )

        RetrofitClient.usuarioService.actualizarUsuario(usuarioId, usuarioActualizado)
            .enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun cerrarSesion() {
        // Limpiar datos guardados
        getSharedPreferences("LoveLinkPrefs", MODE_PRIVATE).edit().clear().apply()

        // Redirigir al login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun eliminarCuenta() {
        RetrofitClient.cuentaService.eliminarCuenta(cuentaId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                        cerrarSesion() // Nos manda de nuevo al login
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al eliminar cuenta", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@PerfilActivity, "Fallo de red", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateImageSlot(uri: Uri?, index: Int) {
        uri?.let {
            imageSlots[index].setImageURI(it)
            imageSlots[index].tag = it.toString()
            subirImagenSlot(it, index + 1) // +1 porque los slots son del 1 al 6 en backend
        }
    }
    private fun subirImagenSlot(uri: Uri, slotIndex: Int) {
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val fileBytes = inputStream.readBytes()
        val fileName = "usuario_${usuarioId}_$slotIndex.jpg"

        val requestFile = MultipartBody.Part.createFormData(
            "file", fileName,
            RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
        )

        val idUsuarioBody = RequestBody.create("text/plain".toMediaTypeOrNull(), usuarioId.toString())
        val numeroBody = RequestBody.create("text/plain".toMediaTypeOrNull(), slotIndex.toString())

        RetrofitClient.imagenesUsuarioService.actualizarImagenSlot(
            requestFile, idUsuarioBody, numeroBody
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PerfilActivity, "Imagen $slotIndex actualizada ✅", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al actualizar imagen $slotIndex ❌", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Fallo de red al subir imagen $slotIndex", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
