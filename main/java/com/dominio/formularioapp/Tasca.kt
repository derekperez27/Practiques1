package com.dominio.formularioapp

data class Tasca(
    val id: Int,
    val titol: String,
    val descripcio: String,
    var completada: Boolean = false, // Per defecte, una tasca nova no està completada
    val tipo: String // El nou camp per diferenciar 'TASCA' o 'NOTA'
)
