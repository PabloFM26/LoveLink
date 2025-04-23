package com.example.lovelink.network

import com.example.lovelink.models.Cuenta
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CuentaService {
    @POST("api/cuentas")
    fun registrarCuenta(@Body cuenta: Cuenta): Call<Cuenta>

    @GET("api/cuentas/{id}")
    fun obtenerCuentaPorId(@Path("id") id: Long): Call<Cuenta>


}


