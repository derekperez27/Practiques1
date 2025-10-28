package com.dominio.formularioapp

// --- IMPORTACIONES NECESARIAS ---
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dominio.formularioapp.databinding.ActivityTasquesBinding
import kotlinx.coroutines.launch

// --- DECLARACIÓN DE LA CLASE ---
class TasquesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTasquesBinding
    private lateinit var tasquesAdapter: TasquesAdapter
    private val TAG = "TasquesActivity_DEBUG" // Etiqueta para filtrar en Logcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasquesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()

        binding.fabAfegirTasca.setOnClickListener {
            val intent = Intent(this, CreateEditTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        // 1. Inicializamos el adaptador con una lista vacía y definimos la acción onClick.
        tasquesAdapter = TasquesAdapter(emptyList()) { tascaClicada ->
            // Esto es lo que pasa cuando se hace clic en un item de la lista
            val intent = Intent(this, CreateEditTaskActivity::class.java).apply {
                putExtra("EXTRA_TASK_ID", tascaClicada.id)
                putExtra("EXTRA_TASK_TITLE", tascaClicada.titol)
                putExtra("EXTRA_TASK_DESC", tascaClicada.descripcio)
                // --- Pasamos los nuevos datos a la pantalla de edición ---
                putExtra("EXTRA_TASK_TYPE", tascaClicada.tipo)
                putExtra("EXTRA_TASK_COMPLETED", tascaClicada.completada)
            }
            startActivity(intent)
        }

        // 2. Asignamos el adaptador y el layout manager al RecyclerView.
        binding.recyclerViewTasques.apply {
            adapter = tasquesAdapter
            layoutManager = LinearLayoutManager(this@TasquesActivity)
        }

        // 3. Configuramos la acción de deslizar para eliminar.
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) { // Buena práctica: asegurar que la posición es válida
                    val tascaPerEliminar = tasquesAdapter.getTaskAt(position)
                    Log.d(TAG, "--- INICIANDO ELIMINACIÓN ---")
                    Log.d(TAG, "Se ha deslizado el elemento con ID: ${tascaPerEliminar.id}, Título: '${tascaPerEliminar.titol}'")
                    eliminarTasca(tascaPerEliminar)
                }
            }
        }

        // 4. Adjuntamos el "ayudante" de swipe al RecyclerView.
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewTasques)
    }

    private fun eliminarTasca(tasca: Tasca) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Llamando a la API para eliminar ID: ${tasca.id}")
                val response = RetrofitClient.tasquesApi.eliminarTasca(tasca.id)

                Log.d(TAG, "Respuesta recibida del servidor. Código: ${response.code()}. ¿Exitosa?: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    Toast.makeText(this@TasquesActivity, "Element eliminat correctament", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "La eliminación fue exitosa según el servidor. Refrescando la lista AHORA.")
                    fetchTasks()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Error del servidor al eliminar. Código: ${response.code()}. Mensaje: $errorBody")
                    Toast.makeText(this@TasquesActivity, "Error en eliminar l'element (Servidor)", Toast.LENGTH_SHORT).show()
                    tasquesAdapter.notifyItemChanged(tasquesAdapter.getTasks().indexOf(tasca))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en la corrutina de eliminarTasca: ${e.message}", e)
                Toast.makeText(this@TasquesActivity, "Error de connexió: ${e.message}", Toast.LENGTH_SHORT).show()
                tasquesAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() llamado, iniciando fetchTasks().")
        fetchTasks()
    }

    private fun fetchTasks() {
        Log.d(TAG, "--- Iniciando fetchTasks ---")
        binding.tasksProgressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.tasquesApi.getTasks()
                if (response.isSuccessful && response.body() != null) {
                    val tasques = response.body()!!.records
                    Log.d(TAG, "Fetch exitoso. Recibidas ${tasques.size} tareas del servidor.")
                    // Log para ver los títulos y tipos de las tareas recibidas
                    tasques.forEach { Log.d(TAG, " - Recibido: ID=${it.id}, Tipo=${it.tipo}, Título='${it.titol}'") }
                    tasquesAdapter.updateTasks(tasques)
                } else {
                    Log.w(TAG, "Fetch fallido o cuerpo vacío. Código: ${response.code()}")
                    Toast.makeText(this@TasquesActivity, "No s'han trobat elements o hi ha hagut un error", Toast.LENGTH_SHORT).show()
                    tasquesAdapter.updateTasks(emptyList())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción en la corrutina de fetchTasks: ${e.message}", e)
                Toast.makeText(this@TasquesActivity, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.tasksProgressBar.visibility = View.GONE
                binding.recyclerViewTasques.visibility = View.VISIBLE
                Log.d(TAG, "--- Fin de fetchTasks ---")
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
