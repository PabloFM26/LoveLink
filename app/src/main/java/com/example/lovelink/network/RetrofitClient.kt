package com.example.lovelink.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Definimos la variable mutable userService
    @kotlin.jvm.JvmField
    var userService: UserService

    private const val BASE_URL = "http://localhost:8081/"  // Asegúrate de que esta URL sea la correcta de tu servidor Spring Boot

    // Retrofit instancia
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())  // Para convertir respuestas a objetos Java
            .build()
    }

    // Creamos la instancia de UserService
    init {
        userService = retrofit.create(UserService::class.java)  // Creamos la instancia de UserService para hacer peticiones
    }
}
