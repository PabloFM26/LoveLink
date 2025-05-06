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
    private val imagenesUsuarios = mutableMapOf<Long, List<String>>() // idUsuario -> lista de imágenes
    private val usuariosDescartados = mutableSetOf<Long>()

    private var indiceActual = 0
    private var imagenActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posibles_matches)

        usuarioId = intent.getLongExtra("usuario_id", -1L)

        // Referencias UI
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
            } else {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        }

        btnNope.setOnClickListener {
            if (indiceActual < usuariosFiltrados.size) {
                Toast.makeText(this, "${usuariosFiltrados[indiceActual].nombre} descartado 💔", Toast.LENGTH_SHORT).show()
                darNope()
            } else {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
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
        RetrofitClient.matchService.obtenerMatchesUsuario(usuarioId)
            .enqueue(object : Callback<List<Match>> {
                override fun onResponse(call: Call<List<Match>>, response: Response<List<Match>>) {
                    if (response.isSuccessful) {
                        val matches = response.body() ?: emptyList()

                        //  Filtramos usuarios con los que ya se han dado like (para que NO se muestren)
                        val usuariosNoMostrar = mutableSetOf<Long>()

                        matches.forEach {
                            if (it.usuario1 == usuarioId && it.likeUsuario1 == true) {
                                usuariosNoMostrar.add(it.usuario2)
                            } else if (it.usuario2 == usuarioId && it.likeUsuario2 == true) {
                                usuariosNoMostrar.add(it.usuario1)
                            }
                        }

                        // Cargamos los usuarios y los filtramos
                        RetrofitClient.usuarioService.obtenerTodosLosUsuarios()
                            .enqueue(object : Callback<List<Usuario>> {
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

        usuarios.forEach { candidato ->
            if (esCompatible(miPerfil!!, candidato)) {
                usuariosFiltrados.add(candidato)
            }
        }

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
        val edadOk = candidato.edad != null && yo.edad != null &&
                candidato.edad in (yo.edad!! - 5).coerceAtLeast(18)..(yo.edad!! + 5)

        val yoGenero = yo.genero?.lowercase() ?: return false
        val yoOrientacion = yo.orientacionSexual?.lowercase() ?: return false
        val candidatoGenero = candidato.genero?.lowercase() ?: return false
        val candidatoOrientacion = candidato.orientacionSexual?.lowercase() ?: return false

        val generoOk = when (yoOrientacion) {
            "heterosexual" -> (
                    (yoGenero == "hombre" && candidatoGenero == "mujer" && candidatoOrientacion == "heterosexual") ||
                            (yoGenero == "mujer" && candidatoGenero == "hombre" && candidatoOrientacion == "heterosexual")
                    )
            "homosexual" -> (
                    yoGenero == candidatoGenero && candidatoOrientacion == "homosexual"
                    )
            "bisexual" -> (
                    (candidatoOrientacion == "bisexual" && (candidatoGenero == "hombre" || candidatoGenero == "mujer"))
                    )
            "pansexual" -> (
                    (candidatoOrientacion == "pansexual" && (candidatoGenero == "hombre" || candidatoGenero == "mujer"))
                    )
            "asexual" -> (
                    (candidatoOrientacion == "asexual" && (candidatoGenero == "hombre" || candidatoGenero == "mujer"))
                    )
            "otro" -> true
            else -> false
        }

        return edadOk && generoOk
    }


    private fun cargarImagenesUsuario(idUsuario: Long, onComplete: (() -> Unit)? = null) {
        RetrofitClient.imagenesUsuarioService.getImagenesByUsuarioId(idUsuario)
            .enqueue(object : Callback<ImagenesUsuario> {
                override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                    val imagenes = response.body()
                    if (imagenes != null) {
                        val listaImagenes = listOf(
                            imagenes.imagen1 ?: "",
                            imagenes.imagen2 ?: "",
                            imagenes.imagen3 ?: "",
                            imagenes.imagen4 ?: "",
                            imagenes.imagen5 ?: "",
                            imagenes.imagen6 ?: ""
                        )
                        imagenesUsuarios[idUsuario] = listaImagenes
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
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            nombreEdadText.text = "No hay más usuarios disponibles"
            localidadText.text = ""
            imagenUsuario.setImageResource(android.R.color.transparent)
            btnLike.isEnabled = false
            btnNope.isEnabled = false
            return
        }

        val usuario = usuariosFiltrados[indiceActual]

        // Si ya lo descartamos antes, pasamos al siguiente automáticamente
        if (usuariosDescartados.contains(usuario.id)) {
            avanzarUsuario()
            return
        }

        nombreEdadText.text = "${usuario.nombre}, ${usuario.edad}"
        localidadText.text = usuario.localidad ?: ""

        val imagenes = imagenesUsuarios[usuario.id]
        imagenActual = 0

        if (!imagenes.isNullOrEmpty()) {
            val rutaImagen = imagenes[imagenActual]
            cargarImagen(rutaImagen)
        } else {
            imagenUsuario.setImageResource(android.R.color.transparent)
        }


        btnLike.isEnabled = true
        btnNope.isEnabled = true
    }

    private fun mostrarSiguienteImagen() {
        val usuario = usuariosFiltrados.getOrNull(indiceActual) ?: return
        val imagenes = imagenesUsuarios[usuario.id] ?: return

        if (imagenes.isNotEmpty()) {
            imagenActual = (imagenActual + 1) % imagenes.size
            val rutaImagen = imagenes[imagenActual]
            cargarImagen(rutaImagen)
        }

    }
    private fun cargarImagen(ruta: String) {
        if (ruta.isBlank()) {
            imagenUsuario.setImageResource(android.R.color.transparent)
            return
        }

        Glide.with(this)
            .load(ruta)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imagenUsuario)
    }


    private fun darLike() {
        val usuarioLikeado = usuariosFiltrados[indiceActual]

        RetrofitClient.matchService.buscarMatchEntreUsuarios(usuarioId, usuarioLikeado.id!!)
            .enqueue(object : Callback<Match?> {
                override fun onResponse(call: Call<Match?>, response: Response<Match?>) {
                    val matchExistente = response.body()

                    if (matchExistente != null) {
                        // Ya existe → actualizamos el like correspondiente
                        val actualizado = matchExistente.copy(
                            likeUsuario1 = if (matchExistente.usuario1 == usuarioId) true else matchExistente.likeUsuario1,
                            likeUsuario2 = if (matchExistente.usuario2 == usuarioId) true else matchExistente.likeUsuario2
                        )

                        RetrofitClient.matchService.actualizarMatch(actualizado.idMatch!!, actualizado)
                            .enqueue(object : Callback<Match> {
                                override fun onResponse(call: Call<Match>, response: Response<Match>) {
                                    Toast.makeText(this@PosiblesMatchesActivity, "¡Like actualizado!", Toast.LENGTH_SHORT).show()
                                    usuariosFiltrados.removeAt(indiceActual)
                                    mostrarUsuarioActual()
                                }

                                override fun onFailure(call: Call<Match>, t: Throwable) {
                                    Toast.makeText(this@PosiblesMatchesActivity, "Error al actualizar match", Toast.LENGTH_SHORT).show()
                                }
                            })

                    } else {
                        // No existe → creamos uno nuevo
                        val nuevoMatch = Match(
                            usuario1 = usuarioId,
                            usuario2 = usuarioLikeado.id!!,
                            likeUsuario1 = true,
                            likeUsuario2 = false
                        )

                        RetrofitClient.matchService.crearMatch(nuevoMatch)
                            .enqueue(object : Callback<Match> {
                                override fun onResponse(call: Call<Match>, response: Response<Match>) {
                                    Toast.makeText(this@PosiblesMatchesActivity, "¡Nuevo match creado!", Toast.LENGTH_SHORT).show()
                                    usuariosFiltrados.removeAt(indiceActual)
                                    mostrarUsuarioActual()
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

        usuariosFiltrados.removeAt(indiceActual)

        mostrarUsuarioActual()
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
            val intent = Intent(this, MatchesActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_chats).setOnClickListener {
            val intent = Intent(this, ChatsActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }
    }
}