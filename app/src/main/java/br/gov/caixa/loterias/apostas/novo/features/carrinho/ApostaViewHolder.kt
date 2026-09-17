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
        itemView.findViewById<TextView>(R.id.tvTitulo)

    private val tvDescricao =
        itemView.findViewById<TextView>(R.id.tvDescricao)

    private val tvValor =
        itemView.findViewById<TextView>(R.id.tvValor)

    private val tvTempo =
        itemView.findViewById<TextView>(R.id.tvTempo)

    private val tvConcurso =
        itemView.findViewById<TextView>(R.id.tvConcurso)

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