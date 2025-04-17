package com.example.lovelink.models

data class Usuario(
    val id_usuario: Int? = null,
    val id_cuenta: Int,
    val nombre: String,
    val apellidos: String,
    val genero: String,
    val localidad: String,
    val edad: Int,
    val orientacionSexual: String,
    val signoZodiaco: String,
    val intencion: String,
    val altura: Int
)
