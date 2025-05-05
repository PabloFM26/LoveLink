package com.example.lovelink.models

data class Mensaje(
    val idMensaje: Long = 0,
    val chatId: Long,
    val emisor: Long,
    val contenido: String,
    val fechaEnvio: String? = null
)

