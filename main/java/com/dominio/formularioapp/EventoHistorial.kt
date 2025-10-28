package com.dominio.formularioapp

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Esta es una clase de datos que representa una tabla en nuestra base de datos.
 * Cada instancia de EventoHistorial será una fila en la tabla "historial_acciones".
 *
 * @param id Es la clave primaria, un número único que se genera automáticamente para cada evento.
 * @param userEmail El email del usuario que realiza la acción. Será clave para filtrar el historial.
 * @param tipoAccion Una categoría para la acción (ej: "REGISTRO", "LOGIN").
 * @param descripcion Un texto detallado de lo que ocurrió.
 * @param timestamp La fecha y hora exactas en que se guardó el evento.
 */
@Entity(tableName = "historial_acciones")
data class EventoHistorial(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userEmail: String,

    val tipoAccion: String,

    val descripcion: String,

    val timestamp: Long = System.currentTimeMillis()
)
