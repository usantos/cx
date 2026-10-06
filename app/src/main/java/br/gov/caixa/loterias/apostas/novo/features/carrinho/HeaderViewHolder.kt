package br.gov.caixa.loterias.apostas.novo.features.carrinho

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R

class HeaderViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val tvTitulo =
        itemView.findViewById<TextView>(R.id.tvTitulo)

    fun bind(item: CarrinhoItem.Header) {
        tvTitulo.text = item.titulo
    }
}