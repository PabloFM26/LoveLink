package com.example.lovelink.models

data class Mensaje(
    val id_mensaje: Int? = null,
    val chat_id: Int,
    val emisor: Int,
    val contenido: String
)
