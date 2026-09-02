package br.gov.caixa.loterias.apostas

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO
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

        // Given
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

        // When
        viewModel.carregarCombo(combo)

        // Then
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

        // Given
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

        // When
        viewModel.atualizarQtdJogosItem(
            indexItem = 0,
            incrementar = true
        )

        // Then
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

        // Given
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

        // When
        viewModel.atualizarQtdJogosItem(
            indexItem = 0,
            incrementar = false
        )

        // Then
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
}