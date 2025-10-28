package com.dominio.formularioapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TasquesAdapter(
    private var tasques: List<Tasca>,
    private val onItemClick: (Tasca) -> Unit
) : RecyclerView.Adapter<TasquesAdapter.TascaViewHolder>() {

    // Aquesta classe interna representa cada 'fila' de la nostra llista.
    inner class TascaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titolTextView: TextView = itemView.findViewById(R.id.textViewTitolTasca)
        val descripcioTextView: TextView = itemView.findViewById(R.id.textViewDescripcioTasca)
        val completadaCheckBox: CheckBox = itemView.findViewById(R.id.checkboxTasca)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tascaClicada = tasques[position]
                    onItemClick(tascaClicada)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TascaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tasca, parent, false)
        return TascaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TascaViewHolder, position: Int) {
        val tascaActual = tasques[position]
        holder.titolTextView.text = tascaActual.titol
        holder.descripcioTextView.text = tascaActual.descripcio
        holder.completadaCheckBox.isChecked = tascaActual.completada
    }

    override fun getItemCount(): Int {
        return tasques.size
    }

    fun updateTasks(newTasks: List<Tasca>) {
        this.tasques = newTasks
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): Tasca {
        return tasques[position]
    }

    // --- FUNCIÓN AÑADIDA PARA SOLUCIONAR EL ERROR ---
    // Aquesta funció retorna la llista actual de tasques que té l'adaptador.
    // La necessitem a TasquesActivity per trobar l'índex d'una tasca.
    fun getTasks(): List<Tasca> {
        return tasques
    }
}
