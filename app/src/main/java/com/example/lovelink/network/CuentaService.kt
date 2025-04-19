package com.example.lovelink.network

import com.example.lovelink.models.Cuenta
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CuentaService {
    @POST("api/cuentas")
    fun registrarCuenta(@Body cuenta: Cuenta): Call<Cuenta>

    @GET("api/cuentas/ultima")
    fun obtenerUltimaCuenta(): Call<Cuenta>

}


