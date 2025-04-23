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
import com.bumptech.glide.Glide
import com.example.lovelink.models.ImagenesUsuario
import com.example.lovelink.models.Usuario
import com.example.lovelink.models.Cuenta
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PerfilActivity : AppCompatActivity() {

    private var usuarioId: Long = -1L
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

        birthdayEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                birthdayEditText.setText("$day/${month + 1}/$year")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        configurarNavegacionInferior()
        cargarDatosUsuario()
        cargarImagenesUsuario()
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
        RetrofitClient.imagenesUsuarioService.getImagenesByUsuarioId(usuarioId).enqueue(object : Callback<ImagenesUsuario> {
            override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                val imagenes = response.body()
                val rutas = listOf(
                    imagenes?.imagen1, imagenes?.imagen2, imagenes?.imagen3,
                    imagenes?.imagen4, imagenes?.imagen5, imagenes?.imagen6
                )
                rutas.forEachIndexed { i, url ->
                    if (!url.isNullOrEmpty()) Glide.with(this@PerfilActivity).load(url).into(imageSlots[i])
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
                updateImageSlot(imageUri, currentSlotIndex)
            }
        }

    companion object {
        private const val MAX_IMAGES = 6
    }
}
