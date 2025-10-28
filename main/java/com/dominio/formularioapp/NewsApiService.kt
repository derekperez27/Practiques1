package com.dominio.formularioapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    /**
     * Obtiene los titulares principales (top-headlines) para un país y categoría.
     */
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>

    /**
     * ▼▼▼ FUNCIÓN AÑADIDA PARA BÚSQUEDA ▼▼▼
     * Busca en todos los artículos una palabra clave (query).
     * El endpoint es "v2/everything".
     */
    @GET("v2/everything")
    suspend fun searchEverything(
        @Query("q") query: String, // La palabra clave a buscar (ej: "tesla")
        @Query("sortBy") sortBy: String, // Criterio de ordenación (ej: "publishedAt" para los más recientes)
        @Query("apiKey") apiKey: String
    ): Response<NewsResponse>
    // ▲▲▲ FIN DE LA FUNCIÓN AÑADIDA ▲▲▲
}
