package com.dominio.formularioapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

data class TasquesListResponse(
    val records: List<Tasca>
)

interface TasquesApiService {

    @POST("crear_tasca.php")
    suspend fun crearTasca(@Body tasca: Tasca): Response<Unit>

    @GET("obtenir_tasques.php")
    suspend fun getTasks(): Response<TasquesListResponse>

    @PUT("actualitzar_tasca.php")
    suspend fun actualitzarTasca(@Body tasca: Tasca): Response<Unit>

    // --- VERSIÓN FINAL PARA ELIMINAR ---
    @FormUrlEncoded
    @POST("eliminar_tasca.php")
    suspend fun eliminarTasca(@Field("id") tascaId: Int): Response<Unit>
}
