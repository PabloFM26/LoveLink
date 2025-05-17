package com.example.lovelink

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lovelink.models.ImagenesUsuario
import com.example.lovelink.models.Mensaje
import com.example.lovelink.models.Usuario
import com.example.lovelink.network.RetrofitClient
import com.example.lovelink.ui.MensajeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper


class ConversacionActivity : AppCompatActivity() {

    private var usuarioId: Long = -1L
    private var otroUsuarioId: Long = -1L
    private var chatId: Long = -1L

    private lateinit var recyclerMensajes: RecyclerView
    private lateinit var adapter: MensajeAdapter
    private lateinit var inputMensaje: EditText
    private lateinit var btnEnviar: TextView
    private lateinit var nombreUsuario: TextView
    private lateinit var btnVolver: TextView
    private lateinit var fotoUsuario: ImageView
    private val handler = Handler(Looper.getMainLooper())
    private val intervaloActualizacion = 2000L


    private val preguntasRandom = listOf(
        "¿Cuál es tu ciudad favorita?",
        "¿Prefieres playa o montaña?",
        "¿Tendrías mascotas juntos?",
        "¿Qué superpoder te gustaría tener?",
        "¿Cuál es tu película favorita?",
        "¿Tu comida favorita para una cita?",
        "¿Eres más de noche o de día?",
        "¿Qué harías en tu primer viaje con tu pareja?",
        "¿Series o pelis?",
        "¿Qué emoji te representa?",
        "¿Crees en el amor a primera vista?",
        "¿Qué harías en un día sin tecnología?",
        "¿Tu canción favorita últimamente?",
        "¿Te gustaría tener hijos?",
        "¿Cuál es tu miedo más raro?",
        "¿Qué animal te representa?",
        "¿Una palabra que te defina?",
        "¿Tu plan ideal para un domingo?",
        "¿Cuál fue tu crush de la infancia?",
        "¿Cómo sería tu cita perfecta?"
    )



    private val listaMensajes = mutableListOf<Mensaje>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_conversacion)

        usuarioId = intent.getLongExtra("usuario_id", -1)
        otroUsuarioId = intent.getLongExtra("otro_usuario_id", -1)
        chatId = intent.getLongExtra("chat_id", -1)

        recyclerMensajes = findViewById(R.id.recyclerMensajes)
        inputMensaje = findViewById(R.id.inputMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)
        nombreUsuario = findViewById(R.id.nombreUsuario)
        btnVolver = findViewById(R.id.btnVolver)

        adapter = MensajeAdapter(listaMensajes, usuarioId)
        recyclerMensajes.layoutManager = LinearLayoutManager(this)
        recyclerMensajes.adapter = adapter

        cargarNombreUsuario()
        fotoUsuario = findViewById(R.id.fotoUsuario)
        cargarMensajes()

        btnVolver.setOnClickListener { finish() }

        btnEnviar.setOnClickListener {
            val contenido = inputMensaje.text.toString().trim()
            if (contenido.isNotEmpty()) {
                enviarMensaje(contenido)
            }
        }

        val btnPregunta = findViewById<TextView>(R.id.btnPregunta)
        btnPregunta.setOnClickListener {
            val pregunta = preguntasRandom.random()
            mostrarDialogoPregunta(pregunta)
        }

    }

    private fun cargarNombreUsuario() {
        RetrofitClient.usuarioService.getUsuarioById(otroUsuarioId)
            .enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    val usuario = response.body()
                    nombreUsuario.text = usuario?.nombre ?: "Usuario"

                    if (usuario?.id != null) {
                        RetrofitClient.imagenesUsuarioService.getImagenesByUsuarioId(usuario.id)
                            .enqueue(object : Callback<ImagenesUsuario> {
                                override fun onResponse(
                                    call: Call<ImagenesUsuario>,
                                    response: Response<ImagenesUsuario>
                                ) {
                                    val imagenUrl = response.body()?.imagen1
                                    if (!imagenUrl.isNullOrEmpty()) {
                                        Glide.with(this@ConversacionActivity)
                                            .load(imagenUrl)
                                            .circleCrop()
                                            .into(fotoUsuario)
                                    }
                                }

                                override fun onFailure(call: Call<ImagenesUsuario>, t: Throwable) {
                                }
                            })
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    nombreUsuario.text = "Error"
                }
            })
    }

    private fun mostrarDialogoPregunta(pregunta: String) {
        val input = EditText(this).apply {
            hint = "Tu respuesta..."
        }

        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Pregunta sorpresa 🎉")
            .setMessage(pregunta)
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val respuesta = input.text.toString().trim()
                if (respuesta.isNotEmpty()) {
                    val mensajePregunta = Mensaje(chatId = chatId, emisor = usuarioId, contenido = "❓ $pregunta")
                    val mensajeRespuesta = Mensaje(chatId = chatId, emisor = usuarioId, contenido = "💬 $respuesta")
                    enviarMensajeAlServidor(mensajePregunta) {
                        enviarMensajeAlServidor(mensajeRespuesta)
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()

    }

    private fun enviarMensajeAlServidor(mensaje: Mensaje, onSent: (() -> Unit)? = null) {
        RetrofitClient.mensajeService.enviarMensaje(mensaje)
            .enqueue(object : Callback<Mensaje> {
                override fun onResponse(call: Call<Mensaje>, response: Response<Mensaje>) {
                    response.body()?.let {
                        listaMensajes.add(it)
                        adapter.notifyItemInserted(listaMensajes.size - 1)
                        recyclerMensajes.scrollToPosition(listaMensajes.size - 1)
                        onSent?.invoke() // ✅ Solo ahora lanzamos la siguiente acción
                    }
                }

                override fun onFailure(call: Call<Mensaje>, t: Throwable) {
                    Toast.makeText(this@ConversacionActivity, "Error enviando mensaje", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun cargarMensajes() {
        RetrofitClient.mensajeService.obtenerMensajesPorChat(chatId)
            .enqueue(object : Callback<List<Mensaje>> {
                override fun onResponse(
                    call: Call<List<Mensaje>>,
                    response: Response<List<Mensaje>>
                ) {
                    val mensajes = response.body() ?: emptyList()
                    listaMensajes.clear()
                    listaMensajes.addAll(mensajes)
                    adapter.notifyDataSetChanged()
                    recyclerMensajes.scrollToPosition(listaMensajes.size - 1)
                }

                override fun onFailure(call: Call<List<Mensaje>>, t: Throwable) {
                    Toast.makeText(this@ConversacionActivity, "Error al cargar mensajes", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun enviarMensaje(contenido: String) {
        val mensaje = Mensaje(
            chatId = chatId,
            emisor = usuarioId,
            contenido = contenido
        )

        RetrofitClient.mensajeService.enviarMensaje(mensaje)
            .enqueue(object : Callback<Mensaje> {
                override fun onResponse(call: Call<Mensaje>, response: Response<Mensaje>) {
                    inputMensaje.text.clear()
                    val nuevoMensaje = response.body()
                    if (nuevoMensaje != null) {
                        listaMensajes.add(nuevoMensaje)
                        adapter.notifyItemInserted(listaMensajes.size - 1)
                        recyclerMensajes.scrollToPosition(listaMensajes.size - 1)
                    }
                }

                override fun onFailure(call: Call<Mensaje>, t: Throwable) {
                    Toast.makeText(this@ConversacionActivity, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private val actualizarMensajes = object : Runnable {
        override fun run() {
            cargarMensajes()
            handler.postDelayed(this, intervaloActualizacion)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(actualizarMensajes)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(actualizarMensajes)
    }


}
