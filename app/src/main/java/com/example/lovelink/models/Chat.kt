package com.example.lovelink.models

data class Chat(
    val idChat: Long = 0,
    val matchId: Long,
    val usuario1: Long,
    val usuario2: Long,
    val fechaCreacion: String? = null,
    val estado: String = "activo"
)
