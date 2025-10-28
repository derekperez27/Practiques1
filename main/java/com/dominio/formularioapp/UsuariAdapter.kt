package com.dominio.formularioapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UsuariAdapter(private val usuaris: List<Usuari>) : RecyclerView.Adapter<UsuariAdapter.UsuariViewHolder>() {

    class UsuariViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomTextView: TextView = itemView.findViewById(R.id.textViewNom)
        val emailTextView: TextView = itemView.findViewById(R.id.textViewEmail)
        val edatTextView: TextView = itemView.findViewById(R.id.textViewEdat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuari, parent, false)
        return UsuariViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuariViewHolder, position: Int) {
        val usuariActual = usuaris[position]


        holder.nomTextView.text = usuariActual.nombre
        holder.emailTextView.text = usuariActual.email
        holder.edatTextView.text = "Edat: ${usuariActual.edat}"
    }

    override fun getItemCount() = usuaris.size
}
