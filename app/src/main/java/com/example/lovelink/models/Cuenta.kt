package com.example.lovelink.models

import com.google.gson.annotations.SerializedName

data class Cuenta(
    @SerializedName("id") val id: Long? = null,
    val telefono: String,
    val email: String,
    val password: String
)
