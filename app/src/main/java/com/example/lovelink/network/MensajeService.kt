package com.example.lovelink.network

import com.example.lovelink.models.Mensaje
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MensajeService {

    @GET("/api/mensajes/chat/{chatId}")
    fun obtenerMensajesPorChat(@Path("chatId") chatId: Long): Call<List<Mensaje>>

    @POST("/api/mensajes")
    fun enviarMensaje(@Body mensaje: Mensaje): Call<Mensaje>
}
