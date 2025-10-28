package com.dominio.formularioapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * La clase principal de la base de datos para la aplicación.
 *
 * @Database - Anotación que define que esta clase es una base de datos de Room.
 *   - entities: La lista de todas las tablas (entidades) que contendrá la base de datos.
 *   - version: El número de versión de la base de datos. Si cambias la estructura de las tablas, debes incrementar este número.
 *   - exportSchema: Lo ponemos a 'false' para evitar un warning durante la compilación.
 */
@Database(entities = [EventoHistorial::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Room usará esta función abstracta para proveer una instancia de nuestro DAO.
     */
    abstract fun historialDao(): HistorialDao

    /**
     * 'companion object' nos permite tener miembros estáticos. Lo usamos para implementar
     * el patrón Singleton, que asegura que solo se cree UNA instancia de la base de datos
     * en toda la aplicación, evitando problemas de rendimiento y concurrencia.
     */
    companion object {
        // La anotación @Volatile asegura que el valor de INSTANCE siempre esté actualizado
        // y sea visible para todos los hilos de ejecución.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Si la instancia ya existe, la devolvemos.
            // Si es nula, entramos en un bloque 'synchronized' para crearla de forma segura
            // en caso de que varios hilos intenten hacerlo a la vez.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Este será el nombre del archivo de la base de datos en el dispositivo.
                ).build()
                INSTANCE = instance
                // Devolvemos la instancia recién creada.
                instance
            }
        }
    }
}
