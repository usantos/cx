package br.gov.caixa.loterias.apostas.novo.features.carrinho

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO
import br.gov.caixa.loterias.apostas.utils.ViewUtils
import java.math.BigDecimal

class ComboViewHolder(
    itemView: View,
    private val onExcluirCombo: (ComboApostaDTO) -> Unit,
    private val onMaisDetalhesCombo: (ComboApostaDTO) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val tvNomeCombo = itemView.findViewById<TextView>(R.id.tvNomeCombo)

    private val tvQuantidadeApostas = itemView.findViewById<TextView>(R.id.tvQuantidadeApostas)

    private val tvValorCombo = itemView.findViewById<TextView>(R.id.tvValorCombo)

    private val btnExcluirCombo = itemView.findViewById<ImageView>(R.id.btnExcluirCombo)

    private val btnMaisDetalhes = itemView.findViewById<TextView>(R.id.btnMaisDetalhes)

    private val icArrow = itemView.findViewById<ImageView>(R.id.ic_arrow)

    fun bind(item: CarrinhoItem.Combo) {
        val combo = item.combo

        tvNomeCombo.text = combo.tipoCombo?.nome.orEmpty()

        val quantidade = combo.apostas.orEmpty().size

        tvQuantidadeApostas.text = itemView.resources.getQuantityString(
            R.plurals.quantidade_apostas, quantidade, quantidade
        )

        tvValorCombo.text = ViewUtils.getMoedaFormat(
            combo.calcularValorTotal()
        )

        btnExcluirCombo.setOnClickListener {
            onExcluirCombo(combo)
        }

        btnMaisDetalhes.setOnClickListener {
            onMaisDetalhesCombo(combo)
        }

        icArrow.setOnClickListener {
            onMaisDetalhesCombo(combo)
        }
    }

    private fun ComboApostaDTO.calcularValorTotal(): BigDecimal =
        apostas.orEmpty().fold(BigDecimal.ZERO) { total, aposta ->
            total.add(aposta.valor ?: BigDecimal.ZERO)
        }
}