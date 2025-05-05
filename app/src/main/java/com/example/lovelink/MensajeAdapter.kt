package com.example.lovelink.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lovelink.R
import com.example.lovelink.models.Mensaje

class MensajeAdapter(
    private val mensajes: List<Mensaje>,
    private val usuarioActualId: Long
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TIPO_EMISOR = 1
        private const val TIPO_RECEPTOR = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (mensajes[position].emisor == usuarioActualId) TIPO_EMISOR else TIPO_RECEPTOR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TIPO_EMISOR) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mensaje_derecha, parent, false)
            EmisorViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_mensaje_izquierda, parent, false)
            ReceptorViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = mensajes[position]
        if (holder is EmisorViewHolder) {
            holder.texto.text = mensaje.contenido
        } else if (holder is ReceptorViewHolder) {
            holder.texto.text = mensaje.contenido
        }
    }

    override fun getItemCount(): Int = mensajes.size

    class EmisorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val texto: TextView = view.findViewById(R.id.mensajeTexto)
    }

    class ReceptorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val texto: TextView = view.findViewById(R.id.mensajeTexto)
    }
}
