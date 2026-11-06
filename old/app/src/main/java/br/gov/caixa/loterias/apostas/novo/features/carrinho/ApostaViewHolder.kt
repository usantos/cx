package br.gov.caixa.loterias.apostas.novo.features.carrinho

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import br.gov.caixa.loterias.apostas.R
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoConcursoEnum
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP
import br.gov.caixa.loterias.apostas.utils.ViewUtils
import br.gov.caixa.loterias.apostas.utils.dpToPx

class ApostaViewHolder(
    itemView: View, private val onExcluir: (IdentificaoDeUmaApostaDas8Modalidades<*>) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val flowIndicadores = itemView.findViewById<Flow>(R.id.flowIndicadores)
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
    private val tvExtra = itemView.findViewById<TextView>(R.id.tvExtra)

    private val btnExcluir = itemView.findViewById<ImageView>(R.id.btnExcluir)

    private val tamanhoColunaSuperSete = itemView.dpToPx(22)
    private val margemColunaSuperSete = itemView.dpToPx(6)

    init {
        criarLayoutSuperSete()
    }

    fun bind(item: CarrinhoItem.Aposta) {
        val context = itemView.context
        val aposta = item.aposta
        val estilo = EstiloModalidadeMKP(aposta.modalidade)
        val isSuperSete = aposta.modalidade == ModalidadeEnum.SUPER_7
        val isSurpresinha = aposta.surpresinha == true

        configurarModalidade(
            aposta = aposta, estilo = estilo
        )

        tvConcurso.text = context.getString(
            R.string.txt_prefix_conc, aposta.concursoAlvo.toString()
        )
        tvValor.text = ViewUtils.getMoedaFormat(aposta.valor)

        containerSuperSete.visibility = if (isSuperSete) View.VISIBLE else View.GONE
        containerNumeros.visibility = if (isSuperSete) View.GONE else View.VISIBLE

        if (isSuperSete) {
            configurarSuperSete(
                aposta = aposta, isSurpresinha = isSurpresinha
            )
        } else {
            adicionarNumeros(
                container = containerNumeros,
                numeros = aposta.listaNumerosSelecionados.orEmpty(),
                isSurpresinha = isSurpresinha
            )
        }

        configurarExtras(aposta)
        configurarPosicaoValor()
        configurarIndicadores(aposta)

        btnExcluir.setOnClickListener {
            onExcluir(aposta)
        }
    }

    private fun configurarPosicaoValor() {
        tvValor.updateLayoutParams<ConstraintLayout.LayoutParams> {
            if (tvExtra.isVisible) {
                topToTop = ConstraintLayout.LayoutParams.UNSET
                topToBottom = ConstraintLayout.LayoutParams.UNSET
                bottomToTop = ConstraintLayout.LayoutParams.UNSET
                bottomToBottom = ConstraintLayout.LayoutParams.UNSET

                baselineToBaseline = R.id.tvExtra

                topMargin = 0
                bottomMargin = 0
            } else {
                baselineToBaseline = ConstraintLayout.LayoutParams.UNSET
                topToTop = ConstraintLayout.LayoutParams.UNSET
                bottomToTop = ConstraintLayout.LayoutParams.UNSET
                bottomToBottom = ConstraintLayout.LayoutParams.UNSET

                topToBottom = R.id.tvConcurso
                topMargin = itemView.dpToPx(12)
                bottomMargin = 0
            }
        }
    }

    private fun configurarIndicadores(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        val isEspelho = aposta.espelho == true
        val isSurpresinha = aposta.surpresinha == true
        val isTeimosinha = (aposta.quantidadeTeimosinhas ?: 0) > 0

        tvEspelho.visibleIf(isEspelho)
        tvSurpresinha.visibleIf(isSurpresinha)
        tvTeimosinha.visibleIf(isTeimosinha)

        val indicadores = buildList {
            if (isEspelho) add(R.id.tvEspelho)
            if (isSurpresinha) add(R.id.tvSurpresinha)
            if (isTeimosinha) add(R.id.tvTeimosinha)
        }

        flowIndicadores.referencedIds = indicadores.toIntArray()

        if (indicadores.isEmpty()) {
            flowIndicadores.visibility = View.GONE
            return
        }

        flowIndicadores.visibility = View.VISIBLE

        configurarPosicaoIndicadores(aposta)
    }

    private fun configurarPosicaoIndicadores(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        when {
            tvExtra.isVisible -> {
                posicionarIndicadoresAbaixoDosExtras()
            }

            devePosicionarIndicadoresAbaixoDoConteudo(aposta) -> {
                posicionarIndicadoresAbaixoDoConteudoNumerico(aposta)
            }

            else -> {
                posicionarIndicadoresNaLinhaDoValor()
            }
        }
    }

    private fun devePosicionarIndicadoresAbaixoDoConteudo(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ): Boolean {
        return when {
            aposta.modalidade == ModalidadeEnum.SUPER_7 -> true
            aposta.surpresinha == true -> false
            else -> aposta.listaNumerosSelecionados.orEmpty().size > NUMEROS_POR_LINHA
        }
    }

    private fun posicionarIndicadoresAbaixoDoConteudoNumerico(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        if (aposta.modalidade == ModalidadeEnum.SUPER_7) {
            posicionarIndicadoresAbaixoDoSuperSete()
        } else {
            posicionarIndicadoresAbaixoDosNumeros()
        }
    }

    private fun posicionarIndicadoresAbaixoDosNumeros() =
        flowIndicadores.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            topToBottom = R.id.containerNumeros
            topMargin = 0
            bottomMargin = 0
        }

    private fun posicionarIndicadoresAbaixoDoSuperSete() =
        flowIndicadores.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            topToBottom = R.id.containerSuperSete
            topMargin = 0
            bottomMargin = 0
        }

    private fun posicionarIndicadoresNaLinhaDoValor() =
        flowIndicadores.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = R.id.tvValor
            topToBottom = ConstraintLayout.LayoutParams.UNSET
            bottomToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToBottom = R.id.tvValor
            topMargin = 0
            bottomMargin = 0
        }

    private fun posicionarIndicadoresAbaixoDosExtras() =
        flowIndicadores.updateLayoutParams<ConstraintLayout.LayoutParams> {
            topToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToTop = ConstraintLayout.LayoutParams.UNSET
            bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            topToBottom = R.id.tvExtra
            topMargin = 0
            bottomMargin = 0
        }

    private fun configurarModalidade(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>, estilo: EstiloModalidadeMKP
    ) {
        tvModalidade.apply {
            text = obterNomeModalidade(aposta)

            setTextColor(
                ContextCompat.getColor(
                    context, estilo.corClara
                )
            )
        }
    }

    private fun obterNomeModalidade(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ): String {
        val modalidade = aposta.modalidade ?: return ""

        val isEspecial = aposta.tipoConcurso?.valor?.equals(
            TipoConcursoEnum.ESPECIAL.valor, ignoreCase = true
        ) == true

        if (!isEspecial) {
            return ModalidadeEnum.fromString(modalidade)
        }

        return ModalidadeEnum.getDescricaoEspecial(modalidade)?.takeIf { it.isNotBlank() }
            ?.lowercase() ?: ModalidadeEnum.fromString(modalidade)
    }

    private fun configurarExtras(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        tvExtra.visibility = View.GONE
        tvExtra.text = ""

        when (aposta.modalidade) {
            ModalidadeEnum.DIA_DE_SORTE -> adicionarMesDaSorte(aposta)
            ModalidadeEnum.TIMEMANIA -> adicionarTimeDoCoracao(aposta)
            ModalidadeEnum.MAIS_MILIONARIA -> adicionarTrevo(aposta)
            else -> Unit
        }
    }

    private fun adicionarTrevo(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        val descricao = if (aposta.surpresinha == true) {
            List(TREVOS_SURPRESINHA) {
                TEXTO_NUMERO_SURPRESINHA
            }.joinToString(" - ")
        } else {
            aposta.trevosSelecionados.orEmpty().takeIf { it.isNotEmpty() }
                ?.joinToString(" - ") { trevo ->
                    trevo.toString().padStart(2, '0')
                } ?: return
        }

        adicionarExtra(
            label = itemView.context.getString(R.string.carrinho_trevos), value = descricao
        )
    }

    private fun adicionarMesDaSorte(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        val mes = aposta.mesDeSorte ?: return

        val descricao =
            mes.nome?.takeIf { it.isNotBlank() } ?: mes.abreviacao?.takeIf { it.isNotBlank() }
            ?: return

        adicionarExtra(
            label = itemView.context.getString(R.string.carrinho_mes_da_sorte),
            value = descricao.lowercase()
        )
    }

    private fun adicionarTimeDoCoracao(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>
    ) {
        val time = aposta.timeDoCoracao ?: return

        val nome =
            time.nome?.takeIf { it.isNotBlank() } ?: time.descricaoCurta?.takeIf { it.isNotBlank() }
            ?: return

        val descricao = time.uf?.takeIf { it.isNotBlank() }?.let { uf -> "$nome/$uf" } ?: nome

        adicionarExtra(
            label = itemView.context.getString(R.string.carrinho_time_do_coracao),
            value = descricao.lowercase()
        )
    }

    private fun adicionarExtra(
        label: String, value: String
    ) {
        tvExtra.text = "$label: $value"
        tvExtra.visibility = View.VISIBLE
    }

    private fun configurarSuperSete(
        aposta: IdentificaoDeUmaApostaDas8Modalidades<*>, isSurpresinha: Boolean
    ) {
        val matriz = aposta.matrizNumerosSelecionados.orEmpty()

        colunasNumerosSuperSete.forEachIndexed { index, containerColuna ->
            containerColuna.removeAllViews()

            if (isSurpresinha) {
                containerColuna.addView(
                    criarNumeroSuperSete(TEXTO_NUMERO_SURPRESINHA)
                )
                return@forEachIndexed
            }

            matriz.getOrNull(index).orEmpty().forEach { numero ->
                containerColuna.addView(
                    criarNumeroSuperSete(
                        numero.toString()
                    )
                )
            }
        }
    }

    private fun criarTextView(
        context: Context, texto: String
    ): TextView {
        return TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            text = texto
            textSize = 14f
            letterSpacing = 0.1f

            typeface = ResourcesCompat.getFont(
                context, R.font.caixa_std_regular
            )

            setTextColor(
                ContextCompat.getColor(
                    context, R.color.greyText
                )
            )
        }
    }

    private fun adicionarNumeros(
        container: LinearLayout, numeros: List<Int>, isSurpresinha: Boolean
    ) {
        container.removeAllViews()

        if (isSurpresinha) {
            container.addView(
                criarTextView(
                    context = container.context, texto = criarNumerosSurpresinha()
                )
            )
            return
        }

        numeros.chunked(NUMEROS_POR_LINHA).forEach { grupo ->
            val texto = grupo.joinToString(" - ") { numero ->
                numero.toString().padStart(2, '0')
            }

            container.addView(
                criarTextView(
                    context = container.context, texto = texto
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
        textoNumero: String
    ): TextView {
        return TextView(itemView.context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            gravity = Gravity.CENTER
            text = textoNumero

            setTextColor(
                ContextCompat.getColor(
                    context, R.color.greyText
                )
            )

            textSize = 12f
        }
    }

    private fun criarNumerosSurpresinha(): String =
        List(NUMEROS_SURPRESINHA) { "XX" }.joinToString(" - ")

    private fun View.visibleIf(condition: Boolean) {
        visibility = if (condition) View.VISIBLE else View.GONE
    }

    private fun obterNumerosSurpresinha(quantidadeNumeros: Int): String {
        return List(quantidadeNumeros) {
            TEXTO_NUMERO_SURPRESINHA
        }.joinToString(" - ")
    }

    companion object {
        private const val TREVOS_SURPRESINHA = 2
        private const val SUPERSETE_HEADER = 7
        private const val NUMEROS_SURPRESINHA = 6
        private const val TEXTO_NUMERO_SURPRESINHA = "XX"
        private const val NUMEROS_POR_LINHA = 6
    }
}