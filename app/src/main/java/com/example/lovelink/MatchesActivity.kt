package com.example.lovelink

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lovelink.models.*
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MatchesActivity : Activity() {

    private lateinit var matchesContainer: LinearLayout
    private var usuarioId: Long = -1L

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matches)

        usuarioId = intent.getLongExtra("usuario_id", -1L)
        matchesContainer = findViewById(R.id.matchesContainer)

        obtenerMatches()
        configurarNavegacionInferior()
    }

    private fun obtenerMatches() {
        RetrofitClient.matchService.obtenerMatchesUsuario(usuarioId)
            .enqueue(object : Callback<List<Match>> {
                override fun onResponse(call: Call<List<Match>>, response: Response<List<Match>>) {
                    val todos = response.body() ?: emptyList()
                    val mutuos = todos.filter {
                        (it.usuario1 == usuarioId && it.likeUsuario1 == true && it.likeUsuario2 == true) ||
                                (it.usuario2 == usuarioId && it.likeUsuario1 == true && it.likeUsuario2 == true)
                    }

                    if (mutuos.isEmpty()) {
                        mostrarMensajeVacio()
                    } else {
                        for (match in mutuos) {
                            val idOtro = if (match.usuario1 == usuarioId) match.usuario2 else match.usuario1
                            cargarUsuarioMatch(match, idOtro)
                        }
                    }
                }

                override fun onFailure(call: Call<List<Match>>, t: Throwable) {
                    mostrarMensajeError()
                }
            })
    }

    private fun cargarUsuarioMatch(match: Match, idOtro: Long) {
        RetrofitClient.usuarioService.getUsuarioById(idOtro).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                val usuario = response.body()
                if (usuario != null) {
                    RetrofitClient.imagenesUsuarioService.getImagenesByUsuarioId(usuario.id!!)
                        .enqueue(object : Callback<ImagenesUsuario> {
                            override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                                val imagenes = response.body()
                                val imagenUrl = imagenes?.imagen1
                                mostrarItemMatch(match, usuario, imagenUrl)
                            }

                            override fun onFailure(call: Call<ImagenesUsuario>, t: Throwable) {
                                mostrarItemMatch(match, usuario, null)
                            }
                        })
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@MatchesActivity, "Error al cargar un usuario", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("MissingInflatedId")
    private fun mostrarItemMatch(match: Match, usuario: Usuario, imagenUrl: String?) {
        val item = layoutInflater.inflate(R.layout.item_match, null)

        val nombreEdad = item.findViewById<TextView>(R.id.matchNombreEdad)
        val localidad = item.findViewById<TextView>(R.id.matchLocalidad)
        val imagen = item.findViewById<ImageView>(R.id.matchImagen)
        val btnChat = item.findViewById<Button>(R.id.btnChatMatch)

        nombreEdad.text = "${usuario.nombre}, ${usuario.edad}"
        localidad.text = usuario.localidad ?: ""

        if (!imagenUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imagen)
        } else {
            imagen.setImageResource(android.R.color.transparent)
        }

        btnChat.setOnClickListener {
            RetrofitClient.chatService.obtenerChatEntreUsuarios(usuarioId, usuario.id!!)
                .enqueue(object : Callback<Chat?> {
                    override fun onResponse(call: Call<Chat?>, response: Response<Chat?>) {
                        val chatExistente = response.body()
                        if (chatExistente != null) {
                            irAConversacion(chatExistente.idChat!!, usuario.id!!)
                        } else {
                            val nuevoChat = Chat(
                                matchId = match.idMatch!!,
                                usuario1 = usuarioId,
                                usuario2 = usuario.id!!
                            )
                            RetrofitClient.chatService.crearChat(nuevoChat)
                                .enqueue(object : Callback<Chat> {
                                    override fun onResponse(call: Call<Chat>, response: Response<Chat>) {
                                        irAConversacion(response.body()?.idChat!!, usuario.id!!)
                                    }

                                    override fun onFailure(call: Call<Chat>, t: Throwable) {
                                        Toast.makeText(this@MatchesActivity, "Error creando chat 💀", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                    }

                    override fun onFailure(call: Call<Chat?>, t: Throwable) {
                        Toast.makeText(this@MatchesActivity, "Error consultando chat existente 🥴", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        matchesContainer.addView(item)
    }

    private fun irAConversacion(chatId: Long, otroUsuarioId: Long) {
        val intent = Intent(this, ConversacionActivity::class.java)
        intent.putExtra("usuario_id", usuarioId)
        intent.putExtra("chat_id", chatId)
        intent.putExtra("otro_usuario_id", otroUsuarioId)
        startActivity(intent)
    }

    private fun mostrarMensajeVacio() {
        val mensaje = TextView(this).apply {
            text = "Esto está demasiado vacío...\n¡Ve a dar likes! 💥"
            textSize = 18f
            setTextColor(ContextCompat.getColor(context, android.R.color.white))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        matchesContainer.addView(mensaje)
    }

    private fun mostrarMensajeError() {
        val mensaje = TextView(this).apply {
            text = "Error cargando tus matches 😓"
            textSize = 18f
            setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        matchesContainer.addView(mensaje)
    }

    private fun configurarNavegacionInferior() {
        findViewById<Button>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, PosiblesMatchesActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_matches).setOnClickListener {
            Toast.makeText(this, "Ya estás en Matches 💘", Toast.LENGTH_SHORT).show()
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
