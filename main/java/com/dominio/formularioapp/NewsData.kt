package com.dominio.formularioapp // Asegúrate de que el nombre del paquete sea el correcto

import com.google.gson.annotations.SerializedName

/**
 * Clase principal que representa toda la respuesta de la API.
 * Corresponde a la estructura general del JSON que devuelve NewsAPI.
 */
data class NewsResponse(
    @SerializedName("status") val status: String,
    @SerializedName("totalResults") val totalResults: Int,
    @SerializedName("articles") val articles: List<Article>
)

/**
 * Clase que representa un solo artículo de noticia.
 * Los campos que podrían no venir en la respuesta se marcan como opcionales con '?'.
 */
data class Article(
    @SerializedName("source") val source: Source,
    @SerializedName("author") val author: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("url") val url: String,
    @SerializedName("urlToImage") val imageUrl: String?, // Le damos un nombre más amigable
    @SerializedName("publishedAt") val publishedAt: String,
    @SerializedName("content") val content: String?
)

/**
 * Clase que representa la fuente de la noticia (ej: "BBC News").
 */
data class Source(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String
)
