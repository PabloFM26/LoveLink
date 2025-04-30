package com.example.lovelink.network

import com.example.lovelink.models.Match
import retrofit2.Call
import retrofit2.http.*

interface MatchService {

    @POST("/api/matches")
    fun crearMatch(@Body match: Match): Call<Match>

    @GET("/api/matches/usuario/{usuarioId}")
    fun obtenerMatchesUsuario(@Path("usuarioId") usuarioId: Long): Call<List<Match>>

    @PUT("/api/matches/{matchId}")
    fun actualizarMatch(
        @Path("matchId") matchId: Long,
        @Body match: Match
    ): Call<Match>

    @GET("/api/matches/buscar")
    fun buscarMatchEntreUsuarios(
        @Query("usuario1") usuario1: Long,
        @Query("usuario2") usuario2: Long
    ): Call<Match?>

}
