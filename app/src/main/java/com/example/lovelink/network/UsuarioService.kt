package com.example.lovelink.network

import com.example.lovelink.models.Usuario
import retrofit2.Call
import retrofit2.http.*

interface UsuarioService {

    @POST("api/usuarios")
    fun crearUsuario(@Body usuario: Usuario): Call<Usuario>

    @GET("api/usuarios/{id}")
    fun getUsuarioById(@Path("id") id: Long): Call<Usuario>

    @PUT("/api/usuarios/actualizar/{id}")
    fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Call<Usuario>

    @GET("api/usuarios")
    fun obtenerTodosLosUsuarios(): Call<List<Usuario>>
}
