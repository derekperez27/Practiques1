package com.dominio.formularioapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // --- URLs BASE PARA CADA SERVICIO ---
    // ▼▼▼ IP DEL SERVIDOR XAMPP ACTUALIZADA ▼▼▼
    // Después (corregido):
    private const val TASQUES_BASE_URL = "http://192.168.33.190/"


    // Dejamos la URL de la API de noticias que ya tenías.este es el .php:
    private const val NEWS_BASE_URL = "https://newsapi.org/"


    // --- CLIENTE OkHttp Y CONVERSOR GSON (Común para todos) ---

    // Creamos un interceptor para ver los logs. Es útil para ambas APIs.
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Creamos un cliente de OkHttp común.
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // El conversor de JSON también es común.
    private val gsonConverterFactory = GsonConverterFactory.create()


    // --- CONSTRUCTOR GENÉRICO DE RETROFIT ---

    // Creamos una función privada que puede construir un cliente de Retrofit para CUALQUIER URL.
    private fun getRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }


    // --- INSTANCIAS PÚBLICAS DE CADA API (Lo que usaremos desde la app) ---

    // Instancia para la API de TAREAS (usa la URL de tu XAMPP)
    val tasquesApi: TasquesApiService by lazy {
        getRetrofit(TASQUES_BASE_URL).create(TasquesApiService::class.java)
    }

    // Instancia para la API de NOTICIAS (usa la URL de NewsAPI)
    // Renombramos 'instance' a 'newsApi' para que sea más claro.
    val newsApi: NewsApiService by lazy {
        getRetrofit(NEWS_BASE_URL).create(NewsApiService::class.java)
    }
}
