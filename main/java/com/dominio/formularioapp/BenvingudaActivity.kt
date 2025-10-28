package com.dominio.formularioapp

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class BenvingudaActivity : AppCompatActivity() {

    private lateinit var messageEditText: TextInputEditText
    private lateinit var sendButton: Button
    private lateinit var stickerButton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var mensajesAdapter: MensajeAdapter
    private val listaDeMensajes = mutableListOf<Mensaje>()
    private var currentUserEmail: String? = null

    private val TU_IP_LOCAL = "192.168.33.190"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_benvinguda)

        val prefs: SharedPreferences = getSharedPreferences("PreferenciasUsuario", MODE_PRIVATE)
        currentUserEmail = prefs.getString("email", null)

        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: Sesión no encontrada.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // --- CÓDIGO PARA AÑADIR LA FLECHA DE ATRÁS ---
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chat - $currentUserEmail"
        // --- FIN DEL CÓDIGO DE LA FLECHA ---

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        stickerButton = findViewById(R.id.stickerButton)
        recyclerView = findViewById(R.id.chatRecyclerView)

        setupRecyclerView()

        sendButton.setOnClickListener {
            val texto = messageEditText.text.toString().trim()
            if (texto.isNotBlank()) {
                enviarMensaje(texto, "texto")
                messageEditText.text?.clear()
            }
        }

        stickerButton.setOnClickListener {
            mostrarDialogoDeStickers()
        }

        cargarMensajes()
    }

    private fun setupRecyclerView() {
        mensajesAdapter = MensajeAdapter(listaDeMensajes, currentUserEmail!!) { mensaje ->
            mostrarDialogoBorrado(mensaje)
        }
        recyclerView.adapter = mensajesAdapter
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
    }

    private fun mostrarDialogoDeStickers() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Elige un Sticker")
            .setNegativeButton("Cancelar", null)

        val stickerContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(48, 32, 48, 32)
        }

        val dialog = builder.create()

        val stickers = listOf(R.drawable.sticker1, R.drawable.sticker2, R.drawable.sticker3)

        stickers.forEach { stickerResId ->
            val imageView = ImageView(this).apply {
                setImageResource(stickerResId)
                layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                    marginEnd = 24
                }
                setOnClickListener {
                    Toast.makeText(this@BenvingudaActivity, "Enviando sticker...", Toast.LENGTH_SHORT).show()
                    enviarStickerLocal(stickerResId)
                    dialog.dismiss()
                }
            }
            stickerContainer.addView(imageView)
        }

        dialog.setView(stickerContainer)
        dialog.show()
    }

    private fun enviarStickerLocal(stickerResId: Int) {
        try {
            val bitmap = BitmapFactory.decodeResource(resources, stickerResId)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            enviarMensaje(base64String, "sticker")

        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar el sticker", Toast.LENGTH_SHORT).show()
            Log.e("STICKER_BASE64_ERROR", "Error: ", e)
        }
    }
    private fun cargarMensajes() {
        val url = "http://$TU_IP_LOCAL/obtener_mensajes.php"
        val queue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Method.GET, url,
            { response -> // onResponse
                try {
                    val jsonArray = JSONArray(response)
                    val nuevaLista = mutableListOf<Mensaje>()
                    for (i in 0 until jsonArray.length()) {
                        val mensajeJson = jsonArray.getJSONObject(i)
                        nuevaLista.add(
                            Mensaje(
                                id = mensajeJson.getInt("id"),
                                email_usuario = mensajeJson.getString("email_usuario"),
                                texto_mensaje = mensajeJson.getString("texto_mensaje"),
                                tipo_mensaje = mensajeJson.optString("tipo_mensaje", "texto")
                            )
                        )
                    }
                    mensajesAdapter.updateMessages(nuevaLista)
                    if (nuevaLista.isNotEmpty()) {
                        recyclerView.post { recyclerView.smoothScrollToPosition(nuevaLista.size - 1) }
                    }
                } catch (e: Exception) {
                    Log.e("CARGAR_MSG_ERROR", "Error parseando JSON con Volley", e)
                    Toast.makeText(this, "Error al procesar los mensajes recibidos.", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Log.e("CARGAR_MSG_ERROR", "Error de red con Volley", error)
                Toast.makeText(this, "No se pudieron cargar los mensajes.", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Cache-Control"] = "no-cache"
                return headers
            }
        }
        queue.add(stringRequest)
    }


    private fun enviarMensaje(textoMensaje: String, tipo: String) {
        val url = "http://$TU_IP_LOCAL/enviar_mensaje.php"
        val request = object : StringRequest(
            Method.POST, url,
            { response ->
                Log.d("ENVIAR_MSG_SUCCESS", "Respuesta: $response")
                cargarMensajes() // Recargamos los mensajes para ver el nuestro
            },
            { error ->
                Log.e("ENVIAR_MSG_ERROR", "Error de red", error)
                Toast.makeText(this, "Error al enviar el mensaje.", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                return hashMapOf(
                    "email" to currentUserEmail!!,
                    "mensaje" to textoMensaje,
                    "tipo_mensaje" to tipo
                )
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarDialogoBorrado(mensaje: Mensaje) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar borrado")
            .setMessage("¿Estás seguro de que quieres borrar este mensaje?")
            .setPositiveButton("Borrar") { _, _ -> borrarMensajeEnServidor(mensaje) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun borrarMensajeEnServidor(mensaje: Mensaje) {
        val url = "http://$TU_IP_LOCAL/delete_message.php"
        val peticionBorrado = object : StringRequest(
            Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        cargarMensajes()
                    } else {
                        Toast.makeText(this, "Error: ${jsonResponse.optString("message")}", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) { /*...*/ }
            },
            { error -> Log.e("BORRAR_MSG_ERROR", "Error de red", error) }) {
            override fun getParams(): Map<String, String> {
                return hashMapOf("id" to mensaje.id.toString(), "email" to currentUserEmail!!)
            }
        }
        Volley.newRequestQueue(this).add(peticionBorrado)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Este caso maneja el clic en la flecha de "atrás" de la barra superior
            android.R.id.home -> {
                finish() // Cierra la actividad actual y vuelve a la anterior
                true
            }

            // Este caso maneja el clic en la opción de FAQ
            R.id.action_faq -> {
                val intent = Intent(this, FaqActivity::class.java)
                startActivity(intent)
                true
            }

            // Este es el caso para ver el historial
            R.id.veurehistorial -> {
                if (currentUserEmail != null) {
                    val intent = Intent(this, HistorialActivity::class.java)
                    intent.putExtra("USER_EMAIL", currentUserEmail)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se pudo identificar al usuario.", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
