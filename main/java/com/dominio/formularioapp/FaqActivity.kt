package com.dominio.formularioapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
//ELIMINAMOS: import androidx.recyclerview.widget.RecyclerView
//ELIMINAMOS: import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.dominio.formularioapp.databinding.ActivityFaqBinding // <-- IMPORTANTE: AÑADIMOS ESTA LÍNEA
import org.json.JSONArray

class FaqActivity : AppCompatActivity() {

    // ▼▼▼ PASO 1: DECLARAR LA VARIABLE DE BINDING ▼▼▼
    private lateinit var binding: ActivityFaqBinding

    // Ya no necesitamos las variables individuales (faqRecyclerView, fabAddQuestion)
    private lateinit var faqAdapter: FaqAdapter
    private val faqList = mutableListOf<FaqItem>()

    // Reemplaza esto con la IP de tu servidor
    private val TU_IP_LOCAL = "192.168.33.190"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ▼▼▼ PASO 2: INICIALIZAR Y USAR BINDING ▼▼▼
        binding = ActivityFaqBinding.inflate(layoutInflater)
        setContentView(binding.root) // Usamos binding.root en lugar de R.layout.activity_faq

        // Configurar la barra de acción para mostrar el botón de "Atrás"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Preguntas Frecuentes"

        // ▼▼▼ PASO 3: ACCEDER A LAS VISTAS A TRAVÉS DE BINDING ▼▼▼
        // Ya no se usa findViewById
        // faqRecyclerView = findViewById(R.id.faqRecyclerView)
        // fabAddQuestion = findViewById(R.id.fab_add_question)

        setupRecyclerView()
        cargarFaqs()

        binding.fabAddQuestion.setOnClickListener {
            mostrarDialogoNuevaPregunta()
        }
    }

    private fun setupRecyclerView() {
        faqAdapter = FaqAdapter(faqList)
        // Accedemos al RecyclerView a través de 'binding'
        binding.faqRecyclerView.adapter = faqAdapter
        binding.faqRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    // El resto del código no cambia porque no accede directamente a las vistas de la Activity.
    // ... (cargarFaqs, mostrarDialogoNuevaPregunta, etc. se quedan igual) ...

    private fun cargarFaqs() {
        val url = "http://$TU_IP_LOCAL/obtener_faqs.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.GET, url,
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    faqList.clear() // Limpiamos la lista antes de añadir nuevos items
                    for (i in 0 until jsonArray.length()) {
                        val faqJson = jsonArray.getJSONObject(i)
                        faqList.add(
                            FaqItem(
                                pregunta = faqJson.getString("pregunta_texto"),
                                respuesta = faqJson.getString("respuesta_texto")
                            )
                        )
                    }
                    faqAdapter.notifyDataSetChanged() // Notificamos al adaptador que los datos han cambiado
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar las preguntas.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "No se pudieron cargar las preguntas.", Toast.LENGTH_SHORT).show()
            }) {
            // Forzamos a Volley a no usar caché
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Cache-Control"] = "no-cache"
                return headers
            }
        }
        queue.add(stringRequest)
    }

    private fun mostrarDialogoNuevaPregunta() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Envía tu pregunta")

        // Creamos un EditText para que el usuario escriba
        val input = EditText(this)
        input.hint = "Escribe tu duda aquí"
        input.setPadding(48, 32, 48, 32) // Añadimos un poco de padding
        builder.setView(input)

        builder.setPositiveButton("Enviar") { dialog, _ ->
            val pregunta = input.text.toString().trim()
            if (pregunta.isNotEmpty()) {
                enviarPreguntaAlServidor(pregunta)
            } else {
                Toast.makeText(this, "La pregunta no puede estar vacía.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun enviarPreguntaAlServidor(pregunta: String) {
        // Obtenemos el email del usuario que ha iniciado sesión
        val prefs: SharedPreferences = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE)
        val currentUserEmail = prefs.getString("email", null)

        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: sesión no válida.", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://$TU_IP_LOCAL/enviar_pregunta.php"
        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                // Opcional: Analizar la respuesta para ver si fue exitoso
                Toast.makeText(this, "¡Pregunta enviada! La revisaremos pronto.", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this, "Error al enviar la pregunta.", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "email" to currentUserEmail,
                    "pregunta" to pregunta
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    // Esta función se activa cuando se pulsa el botón de "Atrás" en la barra superior
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Cierra esta actividad y vuelve a la anterior
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
