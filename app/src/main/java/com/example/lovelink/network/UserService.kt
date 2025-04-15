package com.example.lovelink.network

import com.example.lovelink.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("api/users/register")  // Asegúrate de que esta ruta coincida con la de tu backend
    fun registerUser(@Body user: User): Call<User>  // Enviar los datos del usuario al servidor
}
