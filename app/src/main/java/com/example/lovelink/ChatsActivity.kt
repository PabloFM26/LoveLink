package com.example.lovelink

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lovelink.models.Chat
import com.example.lovelink.models.ImagenesUsuario
import com.example.lovelink.models.Usuario
import com.example.lovelink.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatsActivity : Activity() {

    private var usuarioId: Long = -1L
    private lateinit var chatsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        usuarioId = intent.getLongExtra("usuario_id", -1L)
        chatsContainer = findViewById(R.id.chatsContainer)

        cargarChatsDelUsuario()
        configurarNavegacionInferior()
    }

    private fun cargarChatsDelUsuario() {
        RetrofitClient.chatService.obtenerTodosLosChats().enqueue(object : Callback<List<Chat>> {
            override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
                val todos = response.body() ?: return
                val propios = todos.filter { it.usuario1 == usuarioId || it.usuario2 == usuarioId }

                if (propios.isEmpty()) {
                    mostrarMensajeVacio()
                } else {
                    for (chat in propios) {
                        val otroUsuarioId = if (chat.usuario1 == usuarioId) chat.usuario2 else chat.usuario1
                        cargarDatosDeUsuario(chat, otroUsuarioId)
                    }
                }
            }

            override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
                Toast.makeText(this@ChatsActivity, "Error al cargar los chats 😵", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarDatosDeUsuario(chat: Chat, otroId: Long) {
        RetrofitClient.usuarioService.getUsuarioById(otroId).enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                val usuario = response.body() ?: return

                RetrofitClient.imagenesUsuarioService.getImagenesByUsuarioId(usuario.id!!).enqueue(object : Callback<ImagenesUsuario> {
                    override fun onResponse(call: Call<ImagenesUsuario>, response: Response<ImagenesUsuario>) {
                        val imagenes = response.body()
                        val imagenUrl = imagenes?.imagen1
                        mostrarChatItem(chat, usuario, imagenUrl)
                    }

                    override fun onFailure(call: Call<ImagenesUsuario>, t: Throwable) {
                        mostrarChatItem(chat, usuario, null)
                    }
                })
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@ChatsActivity, "Error al obtener usuario del chat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarChatItem(chat: Chat, usuario: Usuario, imagenUrl: String?) {
        val item = layoutInflater.inflate(R.layout.item_chat, null)

        val nombre = item.findViewById<TextView>(R.id.chatNombre)
        val imagen = item.findViewById<ImageView>(R.id.chatImagen)

        nombre.text = usuario.nombre

        if (!imagenUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagenUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imagen)
        } else {
            imagen.setImageResource(android.R.color.transparent)
        }

        // 👇 Aquí está el toque maestro
        item.setOnClickListener {
            val intent = Intent(this@ChatsActivity, ConversacionActivity::class.java).apply {
                putExtra("usuario_id", usuarioId)
                putExtra("otro_usuario_id", usuario.id)
                putExtra("chat_id", chat.idChat)
            }
            startActivity(intent)
        }

        chatsContainer.addView(item)
    }


    private fun mostrarMensajeVacio() {
        val mensaje = TextView(this).apply {
            text = "No tienes chats activos todavía... ¡Haz un match y empieza una conversación! 💌"
            textSize = 18f
            setTextColor(resources.getColor(android.R.color.white, null))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        }
        chatsContainer.addView(mensaje)
    }

    private fun configurarNavegacionInferior() {
        findViewById<Button>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, PosiblesMatchesActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_matches).setOnClickListener {
            val intent = Intent(this, MatchesActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.nav_chats).setOnClickListener {
            Toast.makeText(this, "Ya estás en Chats 💬", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
            finish()
        }
    }
}
