package com.dominio.formularioapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * El Adapter es el responsable de conectar los datos (la lista de eventos)
 * con las vistas (el RecyclerView). Sabe cómo crear cada fila y cómo rellenarla
 * con los datos de un evento específico.
 */
class HistorialAdapter(private var eventos: List<EventoHistorial>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    /**
     * ViewHolder: Representa una única fila en la lista. Guarda las referencias
     * a los TextViews de 'item_historial.xml' para no tener que buscarlos
     * repetidamente, lo cual es muy eficiente.
     */
    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcionTextView: TextView = itemView.findViewById(R.id.textViewDescripcion)
        val tipoAccionTextView: TextView = itemView.findViewById(R.id.textViewTipoAccion)
        val timestampTextView: TextView = itemView.findViewById(R.id.textViewTimestamp)
    }

    /**
     * Se llama cuando el RecyclerView necesita crear una nueva fila (un ViewHolder).
     * Infla (crea) el layout 'item_historial.xml' y lo devuelve dentro de un ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    /**
     * Se llama para rellenar los datos de una fila específica.
     * Coge el evento de la posición 'position' y usa sus datos para rellenar
     * los TextViews del ViewHolder correspondiente.
     */
    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val evento = eventos[position]

        holder.descripcionTextView.text = evento.descripcion
        holder.tipoAccionTextView.text = "Tipus: ${evento.tipoAccion}"

        // Formateamos el timestamp (que es un número largo) a una fecha y hora legibles.
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val fecha = Date(evento.timestamp)
        holder.timestampTextView.text = "Data: ${sdf.format(fecha)}"
    }

    /**
     * Devuelve el número total de elementos en la lista de datos.
     * El RecyclerView lo usa para saber cuántas filas tiene que dibujar.
     */
    override fun getItemCount(): Int {
        return eventos.size
    }
}
