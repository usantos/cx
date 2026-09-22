package br.gov.caixa.loterias.apostas.novo.features.carrinho

import CarrinhoItem
import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
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
import br.gov.caixa.loterias.apostas.utils.dpToPx

class ApostaViewHolder(
    itemView: View, private val onExcluir: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val tvModalidade = itemView.findViewById<TextView>(R.id.tvModalidade)

    private val containerSuperSete = itemView.findViewById<LinearLayout>(R.id.containerSuperSete)

    private val containerNumeros = itemView.findViewById<LinearLayout>(R.id.containerNumeros)

    private val containerColunasSuperSete =
        itemView.findViewById<LinearLayout>(R.id.containerColunasSuperSete)

    private val containerNumerosSuperSete =
        itemView.findViewById<LinearLayout>(R.id.containerNumerosSuperSete)

    private val colunasNumerosSuperSete = mutableListOf<LinearLayout>()

    private val tvEspelho = itemView.findViewById<TextView>(R.id.tvEspelho)

    private val tvSurpresinha = itemView.findViewById<TextView>(R.id.tvSurpresinha)

    private val tvTeimosinha = itemView.findViewById<TextView>(R.id.tvTeimosinha)

    private val tvValor = itemView.findViewById<TextView>(R.id.tvValor)

    private val tvConcurso = itemView.findViewById<TextView>(R.id.tvConcurso)

    private val btnExcluir = itemView.findViewById<ImageView>(R.id.btnExcluir)

    private val tamanhoColunaSuperSete = itemView.dpToPx(25)
    private val margemColunaSuperSete = itemView.dpToPx(8)

    init {
        criarLayoutSuperSete()
    }

    fun bind(item: CarrinhoItem.Aposta) {
        val context = itemView.context
        val aposta = item.aposta
        val estilo = EstiloModalidadeMKP(aposta.modalidade)

        tvModalidade.apply {
            text = ModalidadeEnum.fromString(aposta.modalidade)?.toString()
            setTextColor(
                ContextCompat.getColor(
                    context, estilo.corClara
                )
            )
        }

        tvConcurso.text = context.getString(
            R.string.txt_prefix_conc, aposta.concursoAlvo.toString()
        )

        tvValor.text = ViewUtils.getMoedaFormat(aposta.valor)

        val isSuperSete = aposta.modalidade == ModalidadeEnum.SUPER_7

        containerSuperSete.visibility = if (isSuperSete) View.VISIBLE else View.GONE
        containerNumeros.visibility = if (isSuperSete) View.GONE else View.VISIBLE

        if (isSuperSete) {
            configurarSuperSete(aposta)
        } else {
            adicionarNumeros(
                container = containerNumeros,
                numeros = aposta.listaNumerosSelecionados.orEmpty(),
                isSurpresinha = aposta.surpresinha == true
            )
        }

        configurarTiposAposta(aposta)

        btnExcluir.setOnClickListener {
            onExcluir(aposta)
        }
    }

    private fun configurarTiposAposta(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        tvEspelho.visibility = if (aposta.espelho == true) View.VISIBLE else View.GONE

        tvSurpresinha.visibility = if (aposta.surpresinha == true) View.VISIBLE else View.GONE

        tvTeimosinha.visibility = if ((aposta.quantidadeTeimosinhas ?: 0) > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun configurarSuperSete(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        val matriz = aposta.matrizNumerosSelecionados.orEmpty()

        colunasNumerosSuperSete.forEachIndexed { index, containerColuna ->
            containerColuna.removeAllViews()

            matriz.getOrNull(index).orEmpty().forEach { numero ->
                containerColuna.addView(
                    criarNumeroSuperSete(numero)
                )
            }
        }
    }

    private fun criarTextView(
        context: Context, texto: String
    ): TextView {
        return TextView(context).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            text = texto
            setTextColor(
                ContextCompat.getColor(
                    context, R.color.greyText
                )
            )
            textSize = 14f
        }
    }

    private fun adicionarNumeros(
        container: LinearLayout,
        numeros: List<Int>,
        isSurpresinha: Boolean
    ) {
        container.removeAllViews()

        if (isSurpresinha) {
            container.addView(
                criarTextView(
                    context = container.context,
                    texto = "XX - XX - XX - XX - XX - XX"
                )
            )
            return
        }

        numeros.chunked(6).forEach { grupo ->
            val texto = grupo.joinToString(" - ") { numero ->
                numero.toString().padStart(2, '0')
            }

            container.addView(
                criarTextView(
                    context = container.context,
                    texto = texto
                )
            )
        }
    }

    private fun criarLayoutSuperSete() {
        repeat(SUPERSETE_HEADER) { index ->

            val cabecalho = criarCabecalhoSuperSete(
                texto = (index + 1).toString()
            )

            containerColunasSuperSete.addView(cabecalho)

            val colunaNumeros = LinearLayout(itemView.context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL

                layoutParams = LinearLayout.LayoutParams(
                    tamanhoColunaSuperSete, ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = margemColunaSuperSete
                }
            }

            containerNumerosSuperSete.addView(colunaNumeros)
            colunasNumerosSuperSete.add(colunaNumeros)
        }
    }

    private fun criarCabecalhoSuperSete(
        texto: String
    ): TextView {
        return TextView(itemView.context).apply {

            layoutParams = LinearLayout.LayoutParams(
                tamanhoColunaSuperSete, tamanhoColunaSuperSete
            ).apply {
                marginStart = margemColunaSuperSete
            }

            text = texto
            gravity = Gravity.CENTER

            setTextColor(
                ContextCompat.getColor(
                    context, R.color.branco
                )
            )

            setBackgroundColor(
                ContextCompat.getColor(
                    context, R.color.cinza90
                )
            )

            setTypeface(typeface, Typeface.BOLD)
        }
    }

    private fun criarNumeroSuperSete(
        numero: Int
    ): TextView {
        return TextView(itemView.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            gravity = Gravity.CENTER
            text = numero.toString()

            setTextColor(
                ContextCompat.getColor(
                    context, R.color.greyText
                )
            )

            textSize = 14f
        }
    }

    companion object {
        private const val NUMEROS_POR_LINHA = 6
        private const val SUPERSETE_HEADER = 7
    }
}