package com.dominio.formularioapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dominio.formularioapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.login_title) // OK, este ya existía

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmailLogin.text.toString().trim()

            if (email.isNotEmpty()) {
                // ▼▼▼ CORREGIDO: Usamos un string más genérico que seguro existe ▼▼▼
                Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show()
                realitzarLogin(email)
            } else {
                // ▼▼▼ CORREGIDO: Usamos el string que ya creamos para el "hint" ▼▼▼
                Toast.makeText(this, getString(R.string.email_hint), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun realitzarLogin(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // --- INICIO DE LA PARTE QUE FALTABA ---
                val url = URL("http://10.0.2.2/login.php")
                val postData = "email=${URLEncoder.encode(email, "UTF-8")}"

                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                OutputStreamWriter(conn.outputStream).use { writer ->
                    writer.write(postData)
                    writer.flush()
                }
                // --- FIN DE LA PARTE QUE FALTABA ---

                val responseCode = conn.responseCode // Ahora 'conn' ya existe
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // --- INICIO DE LA PARTE QUE FALTABA ---
                    val respostaJson = conn.inputStream.bufferedReader().use { it.readText() }
                    val jsonObject = JSONObject(respostaJson)
                    val success = jsonObject.getBoolean("success") // Ahora 'success' ya existe
                    // --- FIN DE LA PARTE QUE FALTABA ---

                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(applicationContext, "Login exitoso", Toast.LENGTH_SHORT).show()

                            val prefs = getSharedPreferences("PreferenciasUsuario", Context.MODE_PRIVATE)
                            prefs.edit().putString("email", email).apply()

                            // ▼▼▼ LÍNEAS PARA GUARDAR EN EL HISTORIAL (Estas ya estaban bien) ▼▼▼
                            val descripcionLogin = "L'usuari ha iniciat sessió correctament."
                            HistorialManager.registrarAccion(this@LoginActivity, email, "LOGIN_EXITOSO", descripcionLogin)
                            // ▲▲▲ FIN DE LAS LÍNEAS PARA GUARDAR ▲▲▲

                            val intent = Intent(this@LoginActivity, BenvingudaActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val message = jsonObject.getString("message")
                            Toast.makeText(applicationContext, "Error: $message", Toast.LENGTH_LONG).show()

                            // Opcional: También puedes registrar el intento fallido
                            val descripcionFallo = "Intent de login fallit: $message"
                            HistorialManager.registrarAccion(this@LoginActivity, email, "LOGIN_FALLIDO", descripcionFallo)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error del servidor: $responseCode", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("LoginActivity", "Error en el login: ", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}

