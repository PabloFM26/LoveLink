package com.example.lovelink.network

import com.example.lovelink.models.ImagenesUsuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ImagenesUsuarioService {

    @POST("api/imagenes")
    fun subirImagenes(@Body imagenesUsuario: ImagenesUsuario): Call<ImagenesUsuario>

    @GET("api/usuarios/ultimo")
    fun obtenerUltimoUsuario(): Call<com.example.lovelink.models.Usuario>
}
