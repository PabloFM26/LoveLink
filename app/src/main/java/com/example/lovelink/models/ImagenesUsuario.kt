package com.example.lovelink.models

import com.google.gson.annotations.SerializedName

data class ImagenesUsuario(
    @SerializedName("id_imagen") val idImagen: Long? = null,
    @SerializedName("id_usuario") val idUsuario: Long,
    var imagen1: String?,
    var imagen2: String?,
    var imagen3: String?,
    var imagen4: String?,
    var imagen5: String?,
    var imagen6: String?
)
