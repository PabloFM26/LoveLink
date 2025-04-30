package com.example.lovelink.models

import com.google.gson.annotations.SerializedName

data class ImagenesUsuario(
    @SerializedName("id_imagen") val idImagen: Long? = null,
    @SerializedName("id_usuario") val idUsuario: Long,
    var imagen1: String?= null,
    var imagen2: String?= null,
    var imagen3: String?= null,
    var imagen4: String?= null,
    var imagen5: String?= null,
    var imagen6: String?= null
)
