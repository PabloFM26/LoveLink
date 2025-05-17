package com.example.lovelink.network

import com.example.lovelink.models.Usuario
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UsuarioApi {
    @GET("/api/usuarios/cuenta/{idCuenta}")
    fun obtenerUsuarioPorCuenta(@Path("idCuenta") idCuenta: Long): Call<Usuario>
}
