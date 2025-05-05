package com.example.lovelink.network

import com.example.lovelink.models.Chat
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatService {
    @POST("/api/chats")
    fun crearChat(@Body chat: Chat): Call<Chat>

    @GET("api/chats/existe")
    fun obtenerChatEntreUsuarios(
        @Query("usuario1") usuario1: Long,
        @Query("usuario2") usuario2: Long
    ): Call<Chat?>

    @GET("/api/chats")
    fun obtenerTodosLosChats(): Call<List<Chat>>


}
