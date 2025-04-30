package com.example.lovelink.network

import com.example.lovelink.models.ImagenesUsuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ImagenesUsuarioService {
    @POST("api/imagenes")
    fun subirImagenes(@Body imagenesUsuario: ImagenesUsuario): Call<ImagenesUsuario>

    @GET("api/imagenes/usuario/{idUsuario}")
    fun getImagenesByUsuarioId(@Path("idUsuario") idUsuario: Long): Call<ImagenesUsuario>

    @PUT("/api/imagenes/{id}")
    fun actualizarImagenes(
        @Path("id") id: Long,
        @Body imagenes: ImagenesUsuario
    ): Call<ImagenesUsuario>


}
