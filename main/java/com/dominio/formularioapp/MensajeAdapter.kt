package com.dominio.formularioapp

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MensajeAdapter(
    private var mensajes: MutableList<Mensaje>,
    private val currentUserEmail: String,
    private val onLongClick: (Mensaje) -> Unit
) : RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    class MensajeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emailTextView: TextView = view.findViewById(R.id.messageUser)
        val messageTextView: TextView = view.findViewById(R.id.messageText)
        val stickerImageView: ImageView = view.findViewById(R.id.stickerImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.emailTextView.text = mensaje.email_usuario

        // ▼▼▼ ESTA ES LA LÍNEA AÑADIDA PARA DEPURAR ▼▼▼
        // Imprime en el Logcat el valor exacto que tiene el campo "tipo_mensaje" para cada fila.
        Log.d("TIPO_MENSAJE_CHECK", "Fila $position - Tipo Recibido: '${mensaje.tipo_mensaje}'")

        // Se usa .equals con ignoreCase para hacer la comparación más robusta
        if (mensaje.tipo_mensaje.trim().equals("sticker", ignoreCase = true)) {
            // --- ES UN STICKER ---
            // Se oculta el TextView y se muestra el ImageView
            holder.messageTextView.visibility = View.GONE
            holder.stickerImageView.visibility = View.VISIBLE

            try {
                // Limpiamos la cabecera del Base64 ("data:image/webp;base64,")
                val base64String = mensaje.texto_mensaje.substringAfter(",")

                // Decodificamos el string limpio
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                // Asignamos el Bitmap al ImageView
                holder.stickerImageView.setImageBitmap(decodedImage)

            } catch (e: Exception) {
                // Si algo falla, la app ya no crasheará y se mostrará un icono de alerta.
                Log.e("BASE64_DECODE_ERROR", "No se pudo decodificar el sticker en la posición $position", e)
                holder.stickerImageView.setImageResource(android.R.drawable.ic_dialog_alert)
            }

        } else {
            // --- ES TEXTO NORMAL ---
            // Se oculta el ImageView y se muestra el TextView
            holder.stickerImageView.visibility = View.GONE
            holder.messageTextView.visibility = View.VISIBLE

            // Se asigna el texto
            holder.messageTextView.text = mensaje.texto_mensaje

            // Se limpia el ImageView para prevenir que muestre una imagen anterior por el reciclaje de vistas
            holder.stickerImageView.setImageBitmap(null)
        }

        // Funcionalidad de borrado (sin cambios)
        holder.itemView.setOnLongClickListener {
            if (mensaje.email_usuario == currentUserEmail) {
                onLongClick(mensaje)
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount() = mensajes.size

    fun updateMessages(newMessages: List<Mensaje>) {
        this.mensajes.clear()
        this.mensajes.addAll(newMessages)
        notifyDataSetChanged()
    }
}
