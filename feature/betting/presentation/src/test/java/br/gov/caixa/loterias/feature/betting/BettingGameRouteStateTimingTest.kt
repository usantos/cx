package br.gov.caixa.loterias.feature.betting

import androidx.compose.runtime.mutableStateOf
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.presentation.route.clearBettingStep
import br.gov.caixa.loterias.feature.betting.presentation.route.selectBettingNumberQuantity
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LotecaOutcome
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.BettingGameViewModel
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingGameRouteStateTimingTest {
    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `quantity reduction reads the observed state after the synchronous selection`() = runTest {
        val viewModel = lotecaModel()
        val observed = mutableStateOf(viewModel.state.value)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { observed.value = it }
        }
        viewModel.chooseNumberQuantity(HIGH_QUANTITY)
        assertEquals(HIGH_QUANTITY, observed.value.numberQuantity)
        var descriptionRebinds = 0

        selectBettingNumberQuantity(viewModel, { observed.value }, LOW_QUANTITY) { descriptionRebinds++ }

        assertEquals(LOW_QUANTITY, observed.value.numberQuantity)
        assertEquals(0, descriptionRebinds)
    }

    @Test fun `clearing Loteca publishes empty selections before rebinding descriptions`() = runTest {
        val viewModel = lotecaModel()
        val observed = mutableStateOf(viewModel.state.value)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { observed.value = it }
        }
        viewModel.toggleLotecaOutcome(1, LotecaOutcome.HOME)
        assertEquals(setOf(LotecaOutcome.HOME), observed.value.lotecaOutcomes[1])
        var descriptionRebinds = 0

        clearBettingStep(viewModel, { observed.value }) {
            assertTrue(observed.value.lotecaOutcomes.isEmpty())
            descriptionRebinds++
        }

        assertTrue(observed.value.lotecaOutcomes.isEmpty())
        assertEquals(1, descriptionRebinds)
    }

    private fun lotecaModel(): BettingGameViewModel {
        val home = SportsTeam(1, "Bahia", "BA")
        val away = SportsTeam(2, "Flamengo", "RJ")
        val game = LotteryGameParameters(
            LotteryModality(LOTECA_ID, "Loteca", "LOTECA"),
            1, "18/10/2026", 0, LOW_QUANTITY, HIGH_QUANTITY, 1, listOf(0),
            listOf(
                BetPrice(LOW_QUANTITY, 0, BigDecimal.TEN, 1, 0),
                BetPrice(HIGH_QUANTITY, 0, BigDecimal.TEN, 2, 0)
            ),
            matches = (1 until LOW_QUANTITY).map { SportsMatch(it, home, away) }
        )
        return BettingGameViewModel(
            LOTECA_ID,
            GetBettingParametersUseCase { error("Os parâmetros preparados não devem ser recarregados") },
            AddBetToCartUseCase { error("Este teste não permite inclusão no carrinho") },
            initialParameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 1, listOf(game))
        )
    }

    private companion object {
        const val LOW_QUANTITY = 15
        const val HIGH_QUANTITY = 16
    }
}
