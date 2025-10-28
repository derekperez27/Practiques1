package com.dominio.formularioapp

// Aquesta data class serveix com a model per a les dades dels usuaris.
// CORRECCIÓ: El camp 'nom' ha sigut reanomenat a 'nombre' per coincidir amb el JSON.
data class Usuari(
    val nombre: String,
    val edat: Int,
    val email: String
)
