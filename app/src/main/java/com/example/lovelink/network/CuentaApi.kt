package com.example.lovelink.network

import com.example.lovelink.models.Cuenta
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CuentaApi {
    @POST("api/cuentas/login")
    fun login(@Body cuenta: Cuenta): Call<Cuenta>
}
