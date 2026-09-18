package br.gov.caixa.loterias.apostas.novo.features.carrinho

import CarrinhoItem
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP
import br.gov.caixa.loterias.apostas.utils.ViewUtils

class ApostaViewHolder(
    itemView: View,
    private val onExcluir: (IdentificaoDeUmaApostaDas8Modalidades<*>, isBolao: Boolean) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val tvModalidade =
        itemView.findViewById<TextView>(R.id.tvModalidade)

    private val containerNumeros =
        itemView.findViewById<LinearLayout>(R.id.containerNumeros)

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

        val externalContext = itemView.context
        val aposta = item.aposta
        val estilo = EstiloModalidadeMKP(aposta.getModalidade())

        tvModalidade.text = ModalidadeEnum.fromString(aposta.modalidade)
        tvModalidade.setTextColor(ContextCompat.getColor(tvModalidade.context, estilo.corClara))

        tvConcurso.text = externalContext.getString(
            R.string.txt_prefix_conc,
            aposta.concursoAlvo.toString()
        )

        tvValor.text = ViewUtils.getMoedaFormat(aposta.getValor())

        if (aposta.modalidade != ModalidadeEnum.SUPER_7) {
            adicionarNumeros(containerNumeros, aposta.listaNumerosSelecionados)
        }

        btnExcluir.setOnClickListener {
            onExcluir(aposta, false)
        }
    }

    private fun criarTextView(context: Context, texto: String): TextView {
        return TextView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            text = texto
            setTextColor(ContextCompat.getColor(context, R.color.greyText))
            textSize = 14f
        }
    }

    private fun adicionarNumeros(
        container: LinearLayout,
        numeros: List<Int>
    ) {
        container.removeAllViews()
        numeros.chunked(6).forEach { grupo ->
            val texto = grupo.joinToString(" - ") { numero ->
                numero.toString().padStart(2, '0')
            }
            container.addView(
                criarTextView(container.context, texto)
            )
        }
    }
}