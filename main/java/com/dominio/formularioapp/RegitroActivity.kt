package com.dominio.formularioapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// ▼▼▼ PASO 5.1: AÑADIR LA IMPORTACIÓN DEL HISTORIALMANAGER ▼▼▼
import com.dominio.formularioapp.HistorialManager

class RegitroActivity : AppCompatActivity() {

    private lateinit var editTextNom: TextInputEditText
    private lateinit var editTextEdat: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var botoDesar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regitro)
        // Dentro de onCreate en RegitroActivity.kt

        val anarALoginTextView: TextView = findViewById(R.id.textViewAnarALogin)
        anarALoginTextView.setOnClickListener {
            // Inicia la actividad de Login
            val intent = Intent(this@RegitroActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextNom = findViewById(R.id.editTextNom)
        editTextEdat = findViewById(R.id.editTextEdat)
        editTextEmail = findViewById(R.id.editTextEmail)
        botoDesar = findViewById(R.id.botoDesar)

        botoDesar.setOnClickListener {
            val nom = editTextNom.text.toString()
            val edatStr = editTextEdat.text.toString()
            val email = editTextEmail.text.toString()

            if (nom.isNotEmpty() && edatStr.isNotEmpty() && email.isNotEmpty()) {
                enviarDatos(nom, edatStr, email)
            } else {
                Toast.makeText(this, "Omple tots els camps", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // El método para manejar la acción de la flecha "atrás"
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun enviarDatos(nombre: String, edat: String, email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            var conn: HttpURLConnection? = null
            try {
                val url = URL("http://10.0.2.2/insertar_usuario.php")
                conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val data = "nombre=${URLEncoder.encode(nombre, "UTF-8")}" +
                        "&edat=${URLEncoder.encode(edat, "UTF-8")}" +
                        "&email=${URLEncoder.encode(email, "UTF-8")}"

                val writer = BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8"))
                writer.write(data)
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode
                Log.d("RegitroActivity", "Response Code: $responseCode")

                val streamReader = if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStreamReader(conn.inputStream)
                } else {
                    InputStreamReader(conn.errorStream)
                }

                val inReader = BufferedReader(streamReader)
                val response = StringBuilder()
                var line: String?
                while (inReader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                inReader.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegitroActivity, "Respuesta del servidor: ${response.toString()}", Toast.LENGTH_LONG).show()
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                        // ▼▼▼ PASO 5.2: REGISTRAR LA ACCIÓN EN EL HISTORIAL LOCAL ▼▼▼
                        val descripcion = "L'usuari '$nombre' s'ha registrat correctament."
                        HistorialManager.registrarAccion(
                            context = this@RegitroActivity,
                            userEmail = email,
                            tipoAccion = "REGISTRO_USUARIO",
                            descripcion = descripcion
                        )
                        // ▲▲▲ FIN DE LA MODIFICACIÓN ▲▲▲

                        editTextNom.text?.clear()
                        editTextEdat.text?.clear()
                        editTextEmail.text?.clear()
                        editTextNom.requestFocus()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Log.e("RegitroActivity", "Excepción: ${e.message}")
                    Toast.makeText(this@RegitroActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                conn?.disconnect()
            }
        }
    }
}
