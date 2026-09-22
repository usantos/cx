package br.gov.caixa.loterias.apostas.novo.features.carrinho

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades

class ComboViewHolder(
    itemView: View,
    private val onExcluirCombo: (ComboApostaDTO) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val tvNomeCombo =
        itemView.findViewById<TextView>(R.id.tvNomeCombo)

    private val tvDescricao =
        itemView.findViewById<TextView>(R.id.tvDescricao)

    private val tvValor =
        itemView.findViewById<TextView>(R.id.tvValor)

    private val tvTempo =
        itemView.findViewById<TextView>(R.id.tvTempo)

    private val tvConcurso =
        itemView.findViewById<TextView>(R.id.tvConcurso)

    private val btnExcluirCombo =
        itemView.findViewById<ImageView>(R.id.btnExcluirCombo)


    fun bind(item: CarrinhoItem.Combo) {

        val combo = item.combo

        tvNomeCombo.text = combo.tipoCombo?.nome

        btnExcluirCombo.setOnClickListener {
            onExcluirCombo(combo)
        }
    }
}