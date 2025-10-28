package com.dominio.formularioapp

// 1. Usa "data class" para obtener funcionalidades extra automáticamente (como equals, hashCode, etc.).
// 2. Añade el campo "id" que necesitamos para el borrado.
data class Mensaje(
    val id: Int,
    val email_usuario: String,
    val texto_mensaje: String,
    val tipo_mensaje: String
)
