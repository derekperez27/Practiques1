package com.dominio.formularioapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // --- PASO 1: AÑADIR LA FLECHA A LA BARRA SUPERIOR ---
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Historial" // Opcional: poner un título a la pantalla

        // 1. Recuperamos el email del usuario que pasamos desde la actividad anterior
        userEmail = intent.getStringExtra("USER_EMAIL")

        // 2. Verificación de seguridad
        if (userEmail == null) {
            Toast.makeText(this, "Error: No se ha podido identificar al usuario.", Toast.LENGTH_LONG).show()
            finish() // Cierra la actividad si no hay email
            return   // Detiene la ejecución de onCreate
        }

        // 3. Configuramos el RecyclerView
        setupRecyclerView()

        // 4. Cargamos los datos desde la base de datos
        cargarHistorial()
    }

    // --- PASO 2: GESTIONAR EL CLIC EN LA FLECHA DE "ATRÁS" ---
    override fun onSupportNavigateUp(): Boolean {
        // Cierra la actividad actual y vuelve a la pantalla anterior.
        finish()
        return true
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewHistorial)
        historialAdapter = HistorialAdapter(emptyList()) // Inicializa con lista vacía
        recyclerView.adapter = historialAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarHistorial() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("HISTORIAL_DEBUG", "Cargando historial para el email: $userEmail")

            val database = AppDatabase.getDatabase(this@HistorialActivity)
            val listaEventos = database.historialDao().getHistorialPorEmail(userEmail!!)

            Log.d("HISTORIAL_DEBUG", "Consulta a la BD completada. Eventos encontrados: ${listaEventos.size}")

            // Volvemos al hilo principal para actualizar la interfaz
            withContext(Dispatchers.Main) {
                Log.d("HISTORIAL_DEBUG", "Actualizando el adapter en el hilo principal.")

                // Asignamos la nueva lista al adapter
                historialAdapter = HistorialAdapter(listaEventos)
                recyclerView.adapter = historialAdapter

                if (listaEventos.isEmpty()) {
                    Toast.makeText(this@HistorialActivity, "No hay eventos en el historial para este usuario.", Toast.LENGTH_LONG).show()
                    Log.w("HISTORIAL_DEBUG", "La lista de eventos está vacía.")
                }
            }
        }
    }
}
