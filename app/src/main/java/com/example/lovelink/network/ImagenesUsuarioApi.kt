package com.example.lovelink.network

import com.example.lovelink.models.ImagenesUsuario
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ImagenesUsuarioApi {
    @GET("api/imagenes/usuario/{idUsuario}")
    fun getImagenesPorUsuario(@Path("idUsuario") idUsuario: Long): Call<ImagenesUsuario>
}
