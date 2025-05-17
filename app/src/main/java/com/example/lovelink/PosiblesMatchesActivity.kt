package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lovelink.models.ImagenesUsuario
import com.example.lovelink.models.Match
import com.example.lovelink.models.Usuario
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PosiblesMatchesActivity : Activity() {

    private lateinit var btnLike: Button
    private lateinit var btnNope: Button
    private lateinit var nombreEdadText: TextView
    private lateinit var localidadText: TextView
    private lateinit var imagenUsuario: ImageView

    private var usuarioId: Long = -1L
    private var miPerfil: Usuario? = null

    private val usuariosFiltrados = mutableListOf<Usuario>()
    private val imagenesUsuarios = mutableMapOf<Long, List<String>>()
    private val usuariosDescartados = mutableSetOf<Long>()

    private var indiceActual = 0
    private var imagenActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posibles_matches)

        usuarioId = intent.getLongExtra("usuario_id", -1L)

        nombreEdadText = findViewById(R.id.nombreEdadText)
        localidadText = findViewById(R.id.localidadText)
        imagenUsuario = findViewById(R.id.imagenUsuario)
        btnLike = findViewById(R.id.btnLike)
        btnNope = findViewById(R.id.btnNope)

        configurarNavegacionInferior()
        cargarMiPerfilYUsuarios()

        btnLike.setOnClickListener {
            if (indiceActual < usuariosFiltrados.size) {
                Toast.makeText(this, "¡Le diste Like a ${usuariosFiltrados[indiceActual].nombre}! 💖", Toast.LENGTH_SHORT).show()
                darLike()
            }
        }

        btnNope.setOnClickListener {
            if (indiceActual < usuariosFiltrados.size) {
                Toast.makeText(this, "${usuariosFiltrados[indiceActual].nombre} descartado 💔", Toast.LENGTH_SHORT).show()
                darNope()
            }
        }

        imagenUsuario.setOnClickListener {
            mostrarSiguienteImagen()
        }
    }

    private fun cargarMiPerfilYUsuarios() {
        RetrofitClient.usuarioService.getUsuarioById(usuarioId).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    miPerfil = response.body()
                    cargarTodosLosUsuarios()
                } else {
                    Toast.makeText(this@PosiblesMatchesActivity, "Error cargando tu perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@PosiblesMatchesActivity, "Fallo al cargar tu perfil", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarTodosLosUsuarios() {
        RetrofitClient.matchService.obtenerMatchesUsuario(usuarioId).enqueue(object : Callback<List<Match>> {
            override fun onResponse(call: Call<List<Match>>, response: Response<List<Match>>) {
                if (response.isSuccessful) {
                    val matches = response.body() ?: emptyList()
                    val usuariosNoMostrar = mutableSetOf<Long>()

                    matches.forEach {
                        if (it.usuario1 == usuarioId && it.likeUsuario1 == true) {
                            usuariosNoMostrar.add(it.usuario2)
                        } else if (it.usuario2 == usuarioId && it.likeUsuario2 == true) {
                            usuariosNoMostrar.add(it.usuario1)
                        }
                    }

                    RetrofitClient.usuarioService.obtenerTodosLosUsuarios().enqueue(object : Callback<List<Usuario>> {
                        override fun onResponse(call: Call<List<Usuario>>, response: Response<List<Usuario>>) {
                            if (response.isSuccessful) {
                                val usuarios = response.body()?.filter {
                                    it.id != usuarioId && !usuariosNoMostrar.contains(it.id)
                                } ?: emptyList()
                                filtrarUsuariosCompatibles(usuarios)
                            } else {
                                Toast.makeText(this@PosiblesMatchesActivity, "Error cargando usuarios", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<List<Usuario>>, t: Throwable) {
                            Toast.makeText(this@PosiblesMatchesActivity, "Fallo cargando usuarios", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@PosiblesMatchesActivity, "Error obteniendo matches", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Match>>, t: Throwable) {
                Toast.makeText(this@PosiblesMatchesActivity, "Fallo obteniendo matches", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filtrarUsuariosCompatibles(usuarios: List<Usuario>) {
        usuariosFiltrados.clear()
        usuarios.forEach { if (esCompatible(miPerfil!!, it)) usuariosFiltrados.add(it) }
        usuariosFiltrados.shuffle()

        if (usuariosFiltrados.isEmpty()) {
            mostrarUsuarioActual()
            return
        }

        val primerUsuario = usuariosFiltrados[0]
        cargarImagenesUsuario(primerUsuario.id!!) {
            mostrarUsuarioActual()
        }
    }

    private fun esCompatible(yo: Usuario, candidato: Usuario): Boolean {
        val edadOk = candidato.edad != null && yo.edad != null && candidato.edad in (yo.edad!! - 5).coerceAtLeast(18)..(yo.edad!! + 5)
        val yoGenero = yo.genero?.lowercase() ?: return false
        val yoOrientacion = yo.orientacionSexual?.lowercase() ?: return false
        val candidatoGenero = candidato.genero?.lowercase() ?: return false
        val candidatoOrientacion = candidato.orientacionSexual?.lowercase() ?: return false

        val generoOk = when (yoOrientacion) {
            "heterosexual" -> (yoGenero == "hombre" && candidatoGenero == "mujer" && candidatoOrientacion == "heterosexual") ||
                    (yoGenero == "mujer" && candidatoGenero == "hombre" && candidatoOrientacion == "heterosexual")
            "homosexual" -> yoGenero == candidatoGenero && candidatoOrientacion == "homosexual"
            "bisexual", "pansexual", "asexual" -> candidatoOrientacion == yoOrientacion
            "otro" -> true
            else -> false
        }

        return edadOk && generoOk
    }

    private fun cargarImagenesUsuario(idUsuario: Long, onComplete: (() -> Unit)? = null) {
        RetrofitClient.imagenesUsuarioService.getImagenesByUsuarioId(idUsuario).enqueue(object : Callback<ImagenesUsuario> {
            override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                val imagenes = response.body()
                if (imagenes != null) {
                    val lista = listOf(imagenes.imagen1, imagenes.imagen2, imagenes.imagen3, imagenes.imagen4, imagenes.imagen5, imagenes.imagen6).map { it ?: "" }
                    imagenesUsuarios[idUsuario] = lista
                }
                onComplete?.invoke()
            }

            override fun onFailure(call: Call<ImagenesUsuario>, t: Throwable) {
                onComplete?.invoke()
            }
        })
    }

    private fun mostrarUsuarioActual() {
        if (indiceActual >= usuariosFiltrados.size) {
            nombreEdadText.text = "No hay más usuarios disponibles"
            localidadText.text = ""
            imagenUsuario.setImageResource(android.R.color.transparent)
            btnLike.isEnabled = false
            btnNope.isEnabled = false
            return
        }

        val usuario = usuariosFiltrados[indiceActual]
        if (usuariosDescartados.contains(usuario.id)) {
            avanzarUsuario()
            return
        }

        nombreEdadText.text = "${usuario.nombre}, ${usuario.edad}"
        localidadText.text = usuario.localidad ?: ""

        val imagenes = imagenesUsuarios[usuario.id]
        imagenActual = 0

        if (!imagenes.isNullOrEmpty()) cargarImagen(imagenes[imagenActual])
        else imagenUsuario.setImageResource(android.R.color.transparent)

        btnLike.isEnabled = true
        btnNope.isEnabled = true
    }

    private fun mostrarSiguienteImagen() {
        val usuario = usuariosFiltrados.getOrNull(indiceActual) ?: return
        val imagenes = imagenesUsuarios[usuario.id] ?: return
        if (imagenes.isNotEmpty()) {
            imagenActual = (imagenActual + 1) % imagenes.size
            cargarImagen(imagenes[imagenActual])
        }
    }

    private fun cargarImagen(ruta: String) {
        if (ruta.isBlank()) imagenUsuario.setImageResource(android.R.color.transparent)
        else Glide.with(this).load(ruta).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(imagenUsuario)
    }

    private fun mostrarSiguienteUsuario() {
        val siguiente = usuariosFiltrados.getOrNull(indiceActual + 1)
        usuariosFiltrados.removeAt(indiceActual)
        if (siguiente != null) {
            cargarImagenesUsuario(siguiente.id!!) {
                mostrarUsuarioActual()
            }
        } else {
            mostrarUsuarioActual()
        }
    }

    private fun darLike() {
        val usuarioLikeado = usuariosFiltrados[indiceActual]

        RetrofitClient.matchService.buscarMatchEntreUsuarios(usuarioId, usuarioLikeado.id!!).enqueue(object : Callback<Match?> {
            override fun onResponse(call: Call<Match?>, response: Response<Match?>) {
                val match = response.body()

                if (match != null) {
                    val actualizado = match.copy(
                        likeUsuario1 = if (match.usuario1 == usuarioId) true else match.likeUsuario1,
                        likeUsuario2 = if (match.usuario2 == usuarioId) true else match.likeUsuario2
                    )

                    RetrofitClient.matchService.actualizarMatch(actualizado.idMatch!!, actualizado).enqueue(object : Callback<Match> {
                        override fun onResponse(call: Call<Match>, response: Response<Match>) {
                            Toast.makeText(this@PosiblesMatchesActivity, "¡Tienes un nuevo match!", Toast.LENGTH_SHORT).show()
                            mostrarSiguienteUsuario()
                        }

                        override fun onFailure(call: Call<Match>, t: Throwable) {
                            Toast.makeText(this@PosiblesMatchesActivity, "Error al actualizar match", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    val nuevoMatch = Match(usuario1 = usuarioId, usuario2 = usuarioLikeado.id!!, likeUsuario1 = true, likeUsuario2 = false)

                    RetrofitClient.matchService.crearMatch(nuevoMatch).enqueue(object : Callback<Match> {
                        override fun onResponse(call: Call<Match>, response: Response<Match>) {
                            mostrarSiguienteUsuario()
                        }

                        override fun onFailure(call: Call<Match>, t: Throwable) {
                            Toast.makeText(this@PosiblesMatchesActivity, "Error creando match", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onFailure(call: Call<Match?>, t: Throwable) {
                Toast.makeText(this@PosiblesMatchesActivity, "Error buscando match", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun darNope() {
        val usuarioDescartado = usuariosFiltrados[indiceActual]
        usuariosDescartados.add(usuarioDescartado.id!!)
        mostrarSiguienteUsuario()
    }

    private fun avanzarUsuario() {
        indiceActual++
        mostrarUsuarioActual()
    }

    private fun configurarNavegacionInferior() {
        findViewById<Button>(R.id.nav_home).setOnClickListener {
            Toast.makeText(this, "Ya estás en Inicio 🏠", Toast.LENGTH_SHORT).show()
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
            startActivity(Intent(this, PerfilActivity::class.java).putExtra("usuario_id", usuarioId))
            finish()
        }
    }
}
