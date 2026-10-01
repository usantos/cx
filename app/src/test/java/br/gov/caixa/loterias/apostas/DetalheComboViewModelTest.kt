package br.gov.caixa.loterias.apostas

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum.fromStringToIdModalidade
import br.gov.caixa.loterias.apostas.viewModel.DetalheComboViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class DetalheComboViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun carregarCombo_devePopularLiveDatasCorretamente() {

        val viewModel = DetalheComboViewModel()

        val modalidade = ModalidadeComboDTO().apply {
            setQuantidadeApostas(5)
            setQuantidadeTeimosinhas(1)
        }

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(modalidade)
            )
            setValor(BigDecimal("15.50"))
        }

        viewModel.carregarCombo(combo)

        assertEquals(
            1,
            viewModel.getModalidadesComboLiveData().value?.size
        )

        assertEquals(
            5,
            viewModel.getQtdTotalApostasLiveData().value
        )

        assertEquals(
            BigDecimal("15.50"),
            viewModel.getValorComboLiveData().value
        )
    }

    @Test
    fun atualizarQtdJogosItem_deveIncrementarQuantidade() {

        val viewModel = DetalheComboViewModel()

        val modalidade = ModalidadeComboDTO().apply {
            setQuantidadeApostas(1)
        }

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(modalidade)
            )
            setValor(BigDecimal.TEN)
        }

        viewModel.carregarCombo(combo)

        viewModel.atualizarQtdJogosItem(
            indexItem = 0,
            incrementar = true
        )

        val resultado =
            viewModel
                .getModalidadesComboLiveData()
                .value
                ?.first()

        assertEquals(
            2,
            resultado?.getQuantidadeApostas()
        )
    }

    @Test
    fun atualizarQtdJogosItem_deveDecrementarQuantidade() {

        val viewModel = DetalheComboViewModel()

        val modalidade = ModalidadeComboDTO().apply {
            setQuantidadeApostas(3)
        }

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(modalidade)
            )
            setValor(BigDecimal.TEN)
        }

        viewModel.carregarCombo(combo)

        viewModel.atualizarQtdJogosItem(
            indexItem = 0,
            incrementar = false
        )

        val resultado =
            viewModel
                .getModalidadesComboLiveData()
                .value
                ?.first()

        assertEquals(
            2,
            resultado?.getQuantidadeApostas()
        )
    }

    @Test
    fun incrementarApostas_modalidadeComum_deveAdicionarUmaAposta() {

        val viewModel = DetalheComboViewModel()

        val mega = criarModalidade(
            fromStringToIdModalidade(
                ModalidadeEnum.MEGA_SENA
            ),
            1
        )

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(mega)
            )
            setValor(BigDecimal.TEN)
        }

        viewModel.carregarCombo(combo)

        viewModel.incrementarApostas()

        val resultado =
            viewModel.getModalidadesComboLiveData()
                .value!!
                .first()

        assertEquals(
            2,
            resultado.getQuantidadeApostas()
        )

        assertEquals(
            2,
            viewModel.getQtdTotalApostasLiveData().value
        )
    }

    @Test
    fun incrementarApostas_lotomania_deveAdicionarDuasApostas() {

        val viewModel = DetalheComboViewModel()

        val lotomania = criarModalidade(
            fromStringToIdModalidade(
                ModalidadeEnum.LOTOMANIA
            ),
            1
        )

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(lotomania)
            )
            setValor(BigDecimal.TEN)
        }

        viewModel.carregarCombo(combo)

        try {
            viewModel.incrementarApostas()
        } catch (_: Exception) {
        }

        val resultado =
            viewModel.getModalidadesComboLiveData()
                .value!!
                .first()

        assertEquals(
            3,
            resultado.getQuantidadeApostas()
        )

        assertEquals(
            3,
            viewModel.getQtdTotalApostasLiveData().value
        )
    }

    @Test
    fun decrementarApostas_modalidadeComum_deveDecrementarUmaAposta() {

        val viewModel = DetalheComboViewModel()

        val mega = criarModalidade(
            fromStringToIdModalidade(
                ModalidadeEnum.MEGA_SENA
            ),
            1
        )

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(mega)
            )
            setValor(BigDecimal.TEN)
        }

        viewModel.carregarCombo(combo)

        try {
            viewModel.incrementarApostas()
            viewModel.decrementarApostas()
        } catch (_: Exception) {
        }

        val resultado =
            viewModel.getModalidadesComboLiveData()
                .value!!
                .first()

        assertEquals(
            1,
            resultado.getQuantidadeApostas()
        )

        assertEquals(
            1,
            viewModel.getQtdTotalApostasLiveData().value
        )
    }

    @Test
    fun decrementarApostas_lotomania_deveDecrementarDuasApostas() {

        val viewModel = DetalheComboViewModel()

        val lotomania = criarModalidade(
            fromStringToIdModalidade(
                ModalidadeEnum.LOTOMANIA
            ),
            1
        )

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(lotomania)
            )
            setValor(BigDecimal.TEN)
        }

        viewModel.carregarCombo(combo)

        try {
            viewModel.incrementarApostas()
            viewModel.decrementarApostas()
        } catch (_: Exception) {
        }

        val resultado =
            viewModel.getModalidadesComboLiveData()
                .value!!
                .first()

        assertEquals(
            1,
            resultado.getQuantidadeApostas()
        )

        assertEquals(
            1,
            viewModel.getQtdTotalApostasLiveData().value
        )
    }

    @Test
    fun incrementarApostas_deveRotacionarIndiceAposLotomania() {

        val viewModel = DetalheComboViewModel()

        val lotomania = criarModalidade(
            fromStringToIdModalidade(
                ModalidadeEnum.LOTOMANIA
            ),
            1
        )

        val mega = criarModalidade(
            fromStringToIdModalidade(
                ModalidadeEnum.MEGA_SENA
            ),
            1
        )

        val combo = CombosDTO().apply {
            setModalidadesCombo(
                mutableListOf(
                    lotomania,
                    mega
                )
            )
            setValor(BigDecimal.TEN)
        }

        viewModel.carregarCombo(combo)

        try {
            viewModel.incrementarApostas()
            viewModel.incrementarApostas()
        } catch (_: Exception) {
        }

        val lista =
            viewModel.getModalidadesComboLiveData().value!!

        assertEquals(
            3,
            lista[0].getQuantidadeApostas()
        )

        assertEquals(
            2,
            lista[1].getQuantidadeApostas()
        )
    }

    private fun criarModalidade(
        codigoModalidade: Int,
        qtdApostas: Int
    ): ModalidadeComboDTO {

        val modalidadeDTO = ModalidadeDTO().apply {
            setValor(codigoModalidade)
        }

        return ModalidadeComboDTO().apply {
            setModalidade(modalidadeDTO)
            setQuantidadeApostas(qtdApostas)
            setQuantidadeTeimosinhas(1)
        }
    }
}