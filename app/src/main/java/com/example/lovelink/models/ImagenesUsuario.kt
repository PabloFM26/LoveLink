package com.example.lovelink.models

import com.google.gson.annotations.SerializedName

data class ImagenesUsuario(
    @SerializedName("id_usuario") val idUsuario: Long,
    val imagen1: String?,
    val imagen2: String?,
    val imagen3: String?,
    val imagen4: String?,
    val imagen5: String?,
    val imagen6: String?
)
