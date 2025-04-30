package com.example.lovelink.models

import com.google.gson.annotations.SerializedName

data class Match(
    @SerializedName("id_match")
    val idMatch: Long? = null,

    @SerializedName("usuario_1")
    val usuario1: Long,

    @SerializedName("usuario_2")
    val usuario2: Long,

    @SerializedName("like_usuario_1")
    var likeUsuario1: Boolean? = null,

    @SerializedName("like_usuario_2")
    var likeUsuario2: Boolean? = null
)
