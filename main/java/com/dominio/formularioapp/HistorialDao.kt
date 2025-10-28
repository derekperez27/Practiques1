package com.dominio.formularioapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistorialDao {

    // Esta función ya la tenías
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEvento(evento: EventoHistorial)

    // ▼▼▼ AÑADIR ESTA NUEVA FUNCIÓN ▼▼▼
    /**
     * Esta función recupera todos los eventos de la tabla 'historial_acciones'
     * que pertenezcan a un email específico.
     * La consulta SQL los ordena por 'timestamp' de forma descendente (DESC),
     * lo que significa que los eventos más recientes aparecerán primero en la lista.
     *
     * @param email El email del usuario para filtrar los resultados.
     * @return Una lista de objetos EventoHistorial.
     */
    @Query("SELECT * FROM historial_acciones WHERE userEmail = :email ORDER BY timestamp DESC")
    suspend fun getHistorialPorEmail(email: String): List<EventoHistorial>
    // ▲▲▲ FIN DEL CÓDIGO A AÑADIR ▲▲▲
}
