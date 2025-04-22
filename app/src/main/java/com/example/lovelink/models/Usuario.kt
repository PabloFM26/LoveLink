package com.example.lovelink.models

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Long? = null,
    val id_cuenta: Long,
    val nombre: String?,
    val apellidos: String?,
    val genero: String?,
    val localidad: String?,
    val edad: Int?,
    val orientacionSexual: String?,
    val signoZodiaco: String?,
    val intencion: String?,
    val altura: Int?
)
