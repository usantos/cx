package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.betting.AddComboToCartUseCase
import br.gov.caixa.loterias.core.domain.betting.BetCombo
import br.gov.caixa.loterias.core.domain.betting.BetComboEntry
import br.gov.caixa.loterias.core.domain.betting.BettingParameters
import br.gov.caixa.loterias.core.domain.betting.GetBetCombosUseCase
import br.gov.caixa.loterias.core.domain.betting.GetBettingParametersUseCase
import br.gov.caixa.loterias.core.domain.betting.GetLotteryModalitiesUseCase
import br.gov.caixa.loterias.core.domain.betting.LotteryModality
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BettingCatalogViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val modality = LotteryModality(1, "Mega-Sena", "MEGA_SENA")
    private val combo = BetCombo(
        id = 8,
        name = "Combo da Sorte",
        description = "Duas apostas",
        new = true,
        amount = BigDecimal.TEN,
        entries = listOf(BetComboEntry(modality, "NORMAL", 2, 1, 6, 0, 2900)),
    )
    private val parameters = BettingParameters(BigDecimal("500"), BigDecimal("30"), 50, emptyList())
    private var modalitiesResult: AppResult<List<LotteryModality>> = AppResult.Success(listOf(modality))
    private var parametersResult: AppResult<BettingParameters> = AppResult.Success(parameters)
    private var combosResult: AppResult<List<BetCombo>> = AppResult.Success(listOf(combo))
    private var addResult: AppResult<ShoppingCart> = AppResult.Success(ShoppingCart(1, BigDecimal.TEN, emptyList()))
    private var submitted: Triple<Int, Int, Boolean>? = null

    @Before fun setUp() = Dispatchers.setMain(dispatcher)
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `modalities mode loads backend modalities and parameters`() = runTest(dispatcher) {
        modalitiesResult = AppResult.Success(listOf(LotteryModality(0, "Todas", "ALL"), modality))
        val viewModel = viewModel(BettingCatalogMode.MODALITIES)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.loading)
        assertEquals(listOf(modality), viewModel.state.value.modalities)
        assertEquals(parameters, viewModel.state.value.parameters)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test fun `modalities mode maps modality and parameter failures`() = runTest(dispatcher) {
        modalitiesResult = AppResult.Failure(AppError.Network)
        val viewModel = viewModel(BettingCatalogMode.MODALITIES)
        advanceUntilIdle()
        assertEquals(
            "Não foi possível carregar as opções de apostas. Verifique sua internet.",
            viewModel.state.value.errorMessage,
        )

        modalitiesResult = AppResult.Success(listOf(modality))
        parametersResult = AppResult.Failure(AppError.Unexpected)
        viewModel.load()
        advanceUntilIdle()
        assertEquals("Ocorreu um erro inesperado ao carregar as apostas.", viewModel.state.value.errorMessage)
    }

    @Test fun `combos mode filters invalid combos without selecting one`() = runTest(dispatcher) {
        combosResult = AppResult.Success(listOf(combo.copy(id = 0), combo))
        val viewModel = viewModel(BettingCatalogMode.COMBOS)
        advanceUntilIdle()

        assertEquals(listOf(combo), viewModel.state.value.combos)
        assertNull(viewModel.state.value.selectedCombo)
        viewModel.addSelectedCombo()
        assertNull(submitted)
    }

    @Test fun `detail controls quantity surprise and adds selected combo`() = runTest(dispatcher) {
        val viewModel = viewModel(BettingCatalogMode.COMBO_DETAIL, combo.id)
        advanceUntilIdle()
        assertEquals(combo, viewModel.state.value.selectedCombo)

        repeat(20) { viewModel.increaseQuantity() }
        assertEquals(15, viewModel.state.value.comboQuantity)
        repeat(20) { viewModel.decreaseQuantity() }
        assertEquals(1, viewModel.state.value.comboQuantity)
        viewModel.increaseQuantity()
        viewModel.toggleSurprise()
        viewModel.addSelectedCombo()
        advanceUntilIdle()

        assertEquals(Triple(combo.id, 2, false), submitted)
        assertTrue(viewModel.state.value.addedToCart)
        assertFalse(viewModel.state.value.loading)
    }

    @Test fun `missing combo and combo failures expose validation messages`() = runTest(dispatcher) {
        val missing = viewModel(BettingCatalogMode.COMBO_DETAIL, 999)
        advanceUntilIdle()
        assertEquals("Combo não encontrado.", missing.state.value.errorMessage)

        combosResult = AppResult.Failure(AppError.Validation(""))
        missing.load()
        advanceUntilIdle()
        assertEquals("Não foi possível atualizar a aposta.", missing.state.value.errorMessage)

        combosResult = AppResult.Failure(AppError.Validation("Combos indisponíveis"))
        missing.load()
        advanceUntilIdle()
        assertEquals("Combos indisponíveis", missing.state.value.errorMessage)
    }

    @Test fun `add combo failure is mapped and cleared by reload`() = runTest(dispatcher) {
        val viewModel = viewModel(BettingCatalogMode.COMBO_DETAIL, combo.id)
        advanceUntilIdle()
        addResult = AppResult.Failure(AppError.Network)
        viewModel.addSelectedCombo()
        advanceUntilIdle()
        assertEquals(
            "Não foi possível carregar as opções de apostas. Verifique sua internet.",
            viewModel.state.value.errorMessage,
        )

        viewModel.load()
        advanceUntilIdle()
        assertNull(viewModel.state.value.errorMessage)
    }

    private fun viewModel(mode: BettingCatalogMode, comboId: Int? = null) = BettingCatalogViewModel(
        mode = mode,
        comboId = comboId,
        getModalities = GetLotteryModalitiesUseCase { modalitiesResult },
        getParameters = GetBettingParametersUseCase { parametersResult },
        getCombos = GetBetCombosUseCase { combosResult },
        addCombo = AddComboToCartUseCase { id, quantity, surprise ->
            submitted = Triple(id, quantity, surprise)
            addResult
        },
        dispatchers = FixedCoroutineDispatchers(dispatcher),
    )
}
