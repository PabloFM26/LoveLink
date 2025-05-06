package com.example.lovelink.network

import com.example.lovelink.models.Cuenta
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CuentaService {
    @POST("api/cuentas")
    fun registrarCuenta(@Body cuenta: Cuenta): Call<Cuenta>

    @GET("api/cuentas/{id}")
    fun obtenerCuentaPorId(@Path("id") id: Long): Call<Cuenta>

    @PUT("/api/cuentas/cambiar-correo/{id}")
    fun actualizarCorreo(@Path("id") id: Long, @Body emailUpdate: Map<String, String>): Call<Cuenta>

    @PUT("/api/cuentas/cambiar-password/{id}")
    fun actualizarContrasena(@Path("id") id: Long, @Body passwordUpdate: Map<String, String>): Call<Cuenta>

    @DELETE("api/cuentas/{id}")
    fun eliminarCuenta(@Path("id") id: Long): Call<Void>

    @GET("/api/cuentas/email/{email}")
    fun getCuentaPorEmail(@Path("email") email: String): Call<Cuenta>


}


