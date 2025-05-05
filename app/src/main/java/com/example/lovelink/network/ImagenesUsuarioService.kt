package com.example.lovelink.network

import com.example.lovelink.models.ImagenesUsuario
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ImagenesUsuarioService {

    // Subida de imagen individual al servidor (uploads)
    @Multipart
    @POST("api/imagenes/upload")
    fun subirImagenIndividual(
        @Part file: MultipartBody.Part,
        @Part("idUsuario") idUsuario: RequestBody,
        @Part("numero") numero: RequestBody
    ): Call<ResponseBody>

    // Obtener imágenes por ID de usuario
    @GET("api/imagenes/usuario/{idUsuario}")
    fun getImagenesByUsuarioId(@Path("idUsuario") idUsuario: Long): Call<ImagenesUsuario>

    @Multipart
    @PUT("api/imagenes/actualizar-slot")
    fun actualizarImagenSlot(
        @Part file: MultipartBody.Part,
        @Part("idUsuario") idUsuario: RequestBody,
        @Part("numero") numero: RequestBody
    ): Call<ResponseBody>



}
