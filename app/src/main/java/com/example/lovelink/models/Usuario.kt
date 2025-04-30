package com.example.lovelink.models

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Long? = null,
    val id_cuenta: Long,
    var nombre: String?,
    var apellidos: String?,
    var genero: String?,
    var localidad: String?,
    val edad: Int?,
    var orientacionSexual: String?,
    var signoZodiaco: String?,
    var intencion: String?,
    var altura: Int?
)
