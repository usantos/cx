package br.gov.caixa.loterias.apostas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum
import br.gov.caixa.loterias.apostas.utils.ViewUtils
import java.math.BigDecimal
import kotlin.math.max

class DetalheComboViewModel : ViewModel() {

    private val modalidadesComboLiveData =
        MutableLiveData<MutableList<ModalidadeComboDTO>>(mutableListOf())

    private val qtdTotalApostasLiveData =
        MutableLiveData(0)

    private val valorComboLiveData =
        MutableLiveData(BigDecimal.ZERO)

    var combo: CombosDTO? = null
        private set

    private var qtdInicialApostas = 0
    private var index = 0
    private var sessaoUsuario: SessaoUsuario? = null

    fun carregarCombo(combo: CombosDTO?) {
        this.combo = combo
        sessaoUsuario = SessaoUsuario.getInstance()

        val modalidades =
            combo?.modalidadesCombo?.toMutableList()
                ?: return

        modalidadesComboLiveData.value = modalidades

        qtdInicialApostas = calcularQtdApostas(modalidades)

        qtdTotalApostasLiveData.value = qtdInicialApostas

        valorComboLiveData.value =
            combo.valor ?: BigDecimal.ZERO
    }

    fun getModalidadesComboLiveData(): LiveData<MutableList<ModalidadeComboDTO>> =
        modalidadesComboLiveData

    fun getQtdTotalApostasLiveData(): LiveData<Int> =
        qtdTotalApostasLiveData

    fun getValorComboLiveData(): LiveData<BigDecimal> =
        valorComboLiveData

    fun incrementarApostas() {

        val modalidades = modalidadesComboLiveData.value ?: return
        val qtdAtual = qtdTotalApostasLiveData.value ?: return

        if (index !in modalidades.indices) {
            return
        }

        val modalidadeAtual = modalidades[index]

        if (ModalidadeEnum.fromInteger(modalidadeAtual.modalidade?.getValor()) == ModalidadeEnum.LOTOMANIA) {
            qtdTotalApostasLiveData.value = qtdAtual + 1
            atualizarQtdJogosItem(index, true)
        }

        qtdTotalApostasLiveData.value =
            (qtdTotalApostasLiveData.value ?: 0) + 1

        atualizarQtdJogosItem(index, true)

        index = (index + 1) % modalidades.size

        atualizarValorCombo()
    }

    fun decrementarApostas() {

        val modalidades = modalidadesComboLiveData.value ?: return
        val qtdAtual = qtdTotalApostasLiveData.value ?: return

        if (qtdAtual > qtdInicialApostas) {

            qtdTotalApostasLiveData.value = qtdAtual - 1

            index =
                if (index == 0)
                    modalidades.size - 1
                else
                    index - 1

            atualizarQtdJogosItem(index, false)

            if (ModalidadeEnum.fromInteger(modalidades[index]
                    .modalidade
                    .getValor()) == ModalidadeEnum.LOTOMANIA
            ) {

                qtdTotalApostasLiveData.value =
                    (qtdTotalApostasLiveData.value ?: 0) - 1

                atualizarQtdJogosItem(index, false)
            }
        } else {
            qtdTotalApostasLiveData.value = qtdInicialApostas
        }

        atualizarValorCombo()
    }

    private fun calcularQtdApostas(
        modalidadesCombo: List<ModalidadeComboDTO>
    ): Int {
        return modalidadesCombo.sumOf {
            it.quantidadeApostas?:0
        }
    }

    private fun atualizarValorCombo() {

        var valorCombo = BigDecimal.ZERO

        val usuario = sessaoUsuario ?: return

        val modalidades =
            modalidadesComboLiveData.value ?: return

        for (modalidadeCombo in modalidades) {

            val modalidade =
                ModalidadeEnum.fromInteger(
                    modalidadeCombo.modalidade?.getValor()
                ) ?: continue

            val parametroSimulacao =
                ViewUtils.getParametroSimulacao(
                    usuario,
                    modalidade
                )

            if (parametroSimulacao != null) {

                val valores =
                    parametroSimulacao
                        .parametroJogo
                        .getValoresAposta()

                var valorAposta =
                    valores.first().getValor()

                val qtdApostas =
                    modalidadeCombo.quantidadeApostas

                val qtdTeimosinhas =
                    max(
                        modalidadeCombo.quantidadeTeimosinhas,
                        1
                    )

                for (valor in valores) {

                    if (modalidadeCombo
                            .modalidade
                            .getValor() != 9
                    ) {

                        if (modalidadeCombo.quantidadeDezenas ==
                            valor.getNumeroPrognosticos()
                        ) {

                            valorAposta = valor.getValor()
                            break
                        }
                    } else {

                        if (
                            modalidadeCombo.quantidadeDezenas ==
                            valor.getNumeroPrognosticos() &&
                            modalidadeCombo.quantidadeDezenasTrevos ==
                            valor.getNumeroTrevos()
                        ) {

                            valorAposta = valor.getValor()
                            break
                        }
                    }
                }

                val subtotal =
                    valorAposta
                        .multiply(
                            BigDecimal.valueOf(
                                qtdApostas.toLong()
                            )
                        )
                        .multiply(
                            BigDecimal.valueOf(
                                qtdTeimosinhas.toLong()
                            )
                        )

                valorCombo = valorCombo.add(subtotal)
            }
        }

        valorComboLiveData.value = valorCombo
    }

    fun atualizarQtdJogosItem(
        indexItem: Int,
        incrementar: Boolean
    ) {

        val lista =
            modalidadesComboLiveData.value ?: return

        if (indexItem !in lista.indices) {
            return
        }

        val itemOriginal = lista[indexItem]

        val novaQuantidade =
            if (incrementar) {
                itemOriginal.quantidadeApostas + 1
            } else {
                itemOriginal.quantidadeApostas - 1
            }

        val itemAtualizado =
            getModalidadeComboDTO(
                itemOriginal,
                novaQuantidade
            )

        val novaLista = lista.toMutableList()

        novaLista[indexItem] = itemAtualizado

        modalidadesComboLiveData.value = novaLista
    }

    companion object {

        private fun getModalidadeComboDTO(
            itemOriginal: ModalidadeComboDTO,
            novaQuantidade: Int
        ): ModalidadeComboDTO {

            return ModalidadeComboDTO().apply {
                modalidade = itemOriginal.modalidade
                tipoConcurso = itemOriginal.tipoConcurso
                quantidadeApostas = novaQuantidade
                quantidadeTeimosinhas = itemOriginal.quantidadeTeimosinhas
                numero = itemOriginal.numero
                quantidadeDezenas = itemOriginal.quantidadeDezenas
                quantidadeDezenasTrevos = itemOriginal.quantidadeDezenasTrevos
            }
        }
    }
}