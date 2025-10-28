package com.dominio.formularioapp

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 'object' crea una clase Singleton, lo que significa que solo habrá una instancia
 * de HistorialManager en toda la aplicación. Es perfecto para gestores o "ayudantes".
 */
object HistorialManager {

    /**
     * Esta es la función principal que llamaremos desde otras partes de la app (como las Activities).
     * Se encarga de registrar una nueva acción en la base de datos.
     *
     * @param context El contexto de la aplicación, necesario para acceder a la base de datos.
     * @param userEmail El email del usuario que realiza la acción.
     * @param tipoAccion Una categoría breve para la acción (ej: "REGISTRO").
     * @param descripcion Un texto más detallado sobre lo que ha sucedido.
     */
    fun registrarAccion(
        context: Context,
        userEmail: String,
        tipoAccion: String,
        descripcion: String
    ) {
        // Usamos una Corrutina en el hilo de IO (Input/Output) para hacer la operación
        // de escritura en la base de datos en segundo plano. Esto evita que la
        // interfaz de usuario se congele o se vuelva lenta.
        CoroutineScope(Dispatchers.IO).launch {
            // 1. Creamos el objeto EventoHistorial con la información recibida.
            val evento = EventoHistorial(
                userEmail = userEmail,
                tipoAccion = tipoAccion,
                descripcion = descripcion
            )

            // 2. Obtenemos la instancia de nuestra base de datos.
            val database = AppDatabase.getDatabase(context)

            // 3. Usamos el DAO para insertar el nuevo evento en la tabla.
            database.historialDao().insertarEvento(evento)
        }
    }
}
