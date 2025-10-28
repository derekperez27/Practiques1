package com.dominio.formularioapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
// 1. Importar la clase de Binding generada
import com.dominio.formularioapp.databinding.ActivityLlistaUsuarisBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class LlistaUsuarisActivity : AppCompatActivity() {

    // 2. Declarar la variable para el binding
    private lateinit var binding: ActivityLlistaUsuarisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 3. Inflar el layout usando View Binding
        binding = ActivityLlistaUsuarisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Activa la flecha para volver atrás y pone el título traducido
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.user_list_title)

        // 4. Accedemos a las vistas a través del binding
        binding.recyclerViewUsuaris.layoutManager = LinearLayoutManager(this)

        // Llamamos a la función para obtener y mostrar los usuarios
        obtenirUsuaris()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun obtenirUsuaris() {
        // Mostramos la barra de progreso y ocultamos la lista de forma segura
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewUsuaris.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://10.0.2.2/consultar_usuaris.php")
                val conn = url.openConnection() as HttpURLConnection
                // --- ¡¡¡LÍNEA CORREGIDA!!! ---
                // Cambiamos "GET" por "POST" para que coincida con lo que espera el servidor PHP.
                conn.requestMethod = "POST"
                conn.connectTimeout = 15000 // Timeout de 15 segundos
                conn.readTimeout = 15000    // Timeout de 15 segundos

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val respostaJson = conn.inputStream.bufferedReader().use { it.readText() }

                    // Procesamos el JSON
                    val llistaUsuaris = parsejarUsuaris(respostaJson)

                    // Una vez tenemos la lista, pasamos al hilo principal para actualizar la UI
                    withContext(Dispatchers.Main) {
                        binding.recyclerViewUsuaris.adapter = UsuariAdapter(llistaUsuaris)
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerViewUsuaris.visibility = View.VISIBLE
                    }
                } else {
                    // Si el servidor responde con un error (ej. 404 Not Found, 500 Internal Server Error)
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LlistaUsuarisActivity, "Error del servidor: $responseCode", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                // Si la excepción es de conexión o de cualquier otro tipo
                Log.e("LlistaUsuaris", "Error en obtenir usuaris: ", e)
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    // Mensaje de error más específico para el usuario
                    val errorMessage = when (e) {
                        is java.net.ConnectException -> "Error de connexió. Assegura't que el servidor i el firewall estan ben configurats."
                        is java.net.SocketTimeoutException -> "Temps d'espera esgotat. La resposta del servidor triga massa."
                        // Mensaje añadido para el error de JSON que vimos en el Logcat
                        is org.json.JSONException -> "Error: La resposta del servidor no és una llista vàlida. Resposta rebuda: ${e.message}"
                        else -> "Error en carregar usuaris."
                    }
                    Toast.makeText(this@LlistaUsuarisActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun parsejarUsuaris(jsonString: String): List<Usuari> {
        val llista = mutableListOf<Usuari>()
        // Añadimos un control por si el JSON está vacío o malformado
        if (jsonString.isNotBlank()) {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val nom = jsonObject.getString("nombre")
                val edat = jsonObject.getInt("edat")
                val email = jsonObject.getString("email")
                llista.add(Usuari(nom, edat, email))
            }
        }
        return llista
    }
}
