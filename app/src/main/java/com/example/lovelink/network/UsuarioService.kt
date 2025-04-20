package com.example.lovelink.network

import com.example.lovelink.models.Usuario
import retrofit2.Call
import retrofit2.http.*

interface UsuarioService {
    @POST("api/usuarios")
    fun crearUsuario(@Body usuario: Usuario): Call<Usuario>

    @PUT("api/usuarios/{id}")
    fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Call<Usuario>

    @GET("api/usuarios/ultimo")
    fun obtenerUltimoUsuario(): Call<Usuario>

}
