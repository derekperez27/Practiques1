package com.dominio.formularioapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dominio.formularioapp.databinding.ItemFaqBinding

class FaqAdapter(private val faqs: List<FaqItem>) : RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    class FaqViewHolder(private val binding: ItemFaqBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(faq: FaqItem) {
            binding.faqQuestionText.text = faq.pregunta
            binding.faqAnswerText.text = faq.respuesta
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(faqs[position])
    }

    override fun getItemCount() = faqs.size
}
