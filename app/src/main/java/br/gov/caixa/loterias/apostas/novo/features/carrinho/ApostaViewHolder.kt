package br.gov.caixa.loterias.apostas.novo.features.carrinho

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades

class ApostaViewHolder(
    itemView: View,
    private val onExcluir: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val tvNome =
        itemView.findViewById<TextView>(R.id.tvNome)

    private val tvNumeros =
        itemView.findViewById<TextView>(R.id.tvNumeros)

    private val tvValor =
        itemView.findViewById<TextView>(R.id.tvValor)

    private val btnExcluir =
        itemView.findViewById<ImageView>(R.id.btnExcluir)

    fun bind(item: CarrinhoItem.Aposta) {

        val aposta = item.aposta

        tvNome.text = aposta.getModalidade()?.name

        btnExcluir.setOnClickListener {
            onExcluir(aposta)
        }
    }
}