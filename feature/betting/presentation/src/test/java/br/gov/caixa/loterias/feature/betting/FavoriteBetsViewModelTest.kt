package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.AddFavoriteBetToCartUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.DeleteFavoriteBetUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
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
class FavoriteBetsViewModelTest {
    private val lotomania = LotteryModality(4, "Lotomania", "LOTOMANIA")
    private val invalid = LotteryModality(0, "", "")
    private val favorite = FavoriteBet(8, 4, "Lotomania", "Meu jogo", listOf(listOf(1, 2)))
    private val games = listOf(
        game(lotomania, "NORMAL", listOf(2, 4, 2)),
        game(lotomania, "ESPECIAL"),
        game(invalid, "NORMAL")
    )
    private var parameterResult: AppResult<BettingParameters> = AppResult.Success(
        BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, games)
    )
    private var favoriteResult: AppResult<List<FavoriteBet>> = AppResult.Success(listOf(favorite, favorite.copy(id = 0)))
    private var deleteResult: AppResult<Unit> = AppResult.Success(Unit)
    private var addResult: AppResult<ShoppingCart> = AppResult.Success(ShoppingCart(1, BigDecimal.ZERO, emptyList()))
    private var filter: Int? = -1
    private var deleted: Long? = null
    private var added: Pair<Long, FavoriteBetCartOptions>? = null

    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `loads real parameters favorites and derived modality options`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        with(viewModel.state.value) {
            assertFalse(loading)
            assertEquals(listOf(8L), favorites.map { it.id })
            assertEquals(listOf(4), modalities.map { it.id })
            assertEquals("Todas", selectedModalityName)
        }
        viewModel.requestAdd(favorite)
        with(viewModel.state.value) {
            assertEquals(listOf(2, 4), recurringOptions)
            assertTrue(specialAvailable)
            assertTrue(mirrorAvailable)
        }
        viewModel.cancelAdd()
        assertNull(viewModel.state.value.addCandidate)
    }

    @Test fun `filters deletes and cancels deletion`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.selectModality(4)
        advanceUntilIdle()
        assertEquals(4, filter)
        assertEquals("Lotomania", viewModel.state.value.selectedModalityName)
        viewModel.requestDelete(favorite)
        viewModel.cancelDelete()
        assertNull(viewModel.state.value.deleteCandidate)
        viewModel.requestDelete(favorite)
        viewModel.confirmDelete()
        advanceUntilIdle()
        assertEquals(8L, deleted)
        assertFalse(viewModel.state.value.loading)
    }

    @Test fun `configures favorite and adds it to cart`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.requestAdd(favorite)
        viewModel.setContestType(9)
        viewModel.setRecurringDraws(-2)
        viewModel.toggleMirror()
        with(viewModel.state.value) {
            assertEquals(2, contestType)
            assertEquals(0, recurringDraws)
            assertTrue(mirror)
        }
        viewModel.setContestType(1)
        viewModel.setRecurringDraws(4)
        viewModel.confirmAdd()
        advanceUntilIdle()
        assertEquals(8L, added?.first)
        assertEquals(FavoriteBetCartOptions(1, 4, true), added?.second)
        assertTrue(viewModel.state.value.addedToCart)
        viewModel.consumeAdded()
        assertFalse(viewModel.state.value.addedToCart)
    }

    @Test fun `picker default without zero is the value sent only after explicit add`() = runTest {
        parameterResult = AppResult.Success(
            BettingParameters(
                BigDecimal.TEN,
                BigDecimal.ONE,
                10,
                listOf(game(lotomania, "NORMAL", listOf(4, 2)))
            )
        )
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.requestAdd(favorite)
        val state = viewModel.state.value
        assertEquals(listOf(4, 2), state.recurringOptions)
        assertEquals(4, state.recurringOptions[state.recurringSelectionIndex])
        assertEquals(state.recurringOptions[state.recurringSelectionIndex], state.recurringDraws)
        assertNull(added)
        viewModel.confirmAdd()
        advanceUntilIdle()
        assertEquals(FavoriteBetCartOptions(1, 4, false), added?.second)
    }

    @Test fun `contest change resets picker to actual contest options and submitted value`() = runTest {
        parameterResult = AppResult.Success(
            BettingParameters(
                BigDecimal.TEN,
                BigDecimal.ONE,
                10,
                listOf(game(lotomania, "NORMAL", listOf(4, 2)), game(lotomania, "ESPECIAL", listOf(8, 6)))
            )
        )
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.requestAdd(favorite)
        viewModel.setRecurringDraws(2)
        viewModel.setContestType(2)
        with(viewModel.state.value) {
            assertEquals(listOf(8, 6), recurringOptions)
            assertEquals(8, recurringDraws)
            assertEquals(recurringOptions[recurringSelectionIndex], recurringDraws)
        }
        viewModel.setRecurringDraws(999)
        assertEquals(8, viewModel.state.value.recurringDraws)
        assertNull(added)
        viewModel.confirmAdd()
        advanceUntilIdle()
        assertEquals(FavoriteBetCartOptions(2, 8, false), added?.second)
    }

    @Test fun `no candidate actions are safe`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.confirmDelete()
        viewModel.confirmAdd()
        assertNull(deleted)
        assertNull(added)
    }

    @Test fun `load delete add and reload failures map all error kinds`() = runTest {
        parameterResult = AppResult.Failure(AppError.Network)
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals("Não foi possível carregar as apostas favoritas. Verifique sua internet.", viewModel.state.value.errorMessage)

        parameterResult = AppResult.Success(BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0, games))
        favoriteResult = AppResult.Failure(AppError.Unexpected)
        viewModel.loadInitial()
        advanceUntilIdle()
        assertEquals("Ocorreu um erro inesperado ao carregar as apostas favoritas.", viewModel.state.value.errorMessage)

        favoriteResult = AppResult.Success(listOf(favorite))
        viewModel.loadInitial()
        advanceUntilIdle()
        deleteResult = AppResult.Failure(AppError.Validation(""))
        viewModel.requestDelete(favorite)
        viewModel.confirmDelete()
        advanceUntilIdle()
        assertEquals("Não foi possível atualizar a aposta favorita.", viewModel.state.value.errorMessage)

        deleteResult = AppResult.Success(Unit)
        favoriteResult = AppResult.Failure(AppError.Validation("Filtro recusado"))
        viewModel.requestDelete(favorite)
        viewModel.confirmDelete()
        advanceUntilIdle()
        assertEquals("Filtro recusado", viewModel.state.value.errorMessage)

        favoriteResult = AppResult.Success(listOf(favorite))
        viewModel.loadInitial()
        advanceUntilIdle()
        addResult = AppResult.Failure(AppError.Network)
        viewModel.requestAdd(favorite)
        viewModel.confirmAdd()
        advanceUntilIdle()
        assertTrue(viewModel.state.value.errorMessage?.contains("internet") == true)
        viewModel.dismissError()
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test fun `deferred visitor favorites keep initial controls without reading private favorites`() = runTest {
        var reads = 0
        val vm = viewModel(autoLoad = false, onRead = { reads++ })
        advanceUntilIdle()
        assertEquals(0, reads)
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.favorites.isEmpty())
        vm.loadInitial()
        advanceUntilIdle()
        assertEquals(1, reads)
        assertEquals(listOf(favorite), vm.state.value.favorites)
    }

    @Test fun `expired lease between parameters and favorites prevents old query and new owner loads once`() = runTest {
        var current = true
        var reads = 0
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<BettingParameters>>()
        val old = viewModel(isSessionCurrent = { current }, parameters = { pending.await() }, onRead = { reads++ })
        advanceUntilIdle()
        current = false
        pending.complete(parameterResult)
        advanceUntilIdle()
        assertEquals(0, reads)
        assertTrue(old.state.value.favorites.isEmpty())
        val fresh = viewModel(onRead = { reads++ })
        advanceUntilIdle()
        assertEquals(1, reads)
        assertEquals(listOf(favorite), fresh.state.value.favorites)
    }

    @Test fun `expired lease ignores pending favorites response and any further read`() = runTest {
        var current = true
        var reads = 0
        val pending = kotlinx.coroutines.CompletableDeferred<AppResult<List<FavoriteBet>>>()
        val old = viewModel(isSessionCurrent = { current }, favorites = { pending.await() }, onRead = { reads++ })
        advanceUntilIdle()
        assertEquals(1, reads)
        current = false
        pending.complete(favoriteResult)
        advanceUntilIdle()
        assertTrue(old.state.value.favorites.isEmpty())
        old.selectModality(2)
        advanceUntilIdle()
        assertEquals(1, reads)
    }

    @Test fun `special only favorite skips picker and adds directly`() = runTest {
        parameterResult = AppResult.Success(
            BettingParameters(
                BigDecimal.TEN,
                BigDecimal.ONE,
                10,
                listOf(game(lotomania, "ESPECIAL"))
            )
        )
        val vm = viewModel()
        advanceUntilIdle()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertNull(vm.state.value.addCandidate)
        assertEquals(8L, added?.first)
        assertEquals(2, added?.second?.contestType)
        assertEquals(0, added?.second?.recurringDraws)
        assertTrue(vm.state.value.addedToCart)
    }

    @Test fun `unavailable modality never opens picker or sends favorite`() = runTest {
        parameterResult = AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, emptyList()))
        val vm = viewModel()
        advanceUntilIdle()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertNull(vm.state.value.addCandidate)
        assertEquals("Modalidade não disponível no momento.", vm.state.value.errorMessage)
        assertNull(added)
    }

    @Test fun `deleting last filtered favorite returns to all modalities`() = runTest {
        val vm = viewModel()
        advanceUntilIdle()
        vm.selectModality(4)
        advanceUntilIdle()
        favoriteResult = AppResult.Success(emptyList())
        vm.requestDelete(favorite)
        vm.confirmDelete()
        advanceUntilIdle()
        assertNull(vm.state.value.selectedModalityId)
        assertNull(filter)
    }

    @Test fun `expired session cannot delete or submit a prepared favorite`() = runTest {
        var current = true
        val vm = viewModel(isSessionCurrent = { current })
        advanceUntilIdle()
        vm.requestAdd(favorite)
        vm.requestDelete(favorite)
        current = false
        vm.confirmAdd()
        vm.confirmDelete()
        advanceUntilIdle()
        assertNull(added)
        assertNull(deleted)
    }

    private fun viewModel(
        autoLoad: Boolean = true,
        onRead: () -> Unit = {},
        isSessionCurrent: () -> Boolean = { true },
        parameters: suspend () -> AppResult<BettingParameters> = { parameterResult },
        favorites: suspend () -> AppResult<List<FavoriteBet>> = { favoriteResult },
    ): FavoriteBetsViewModel {
        val dispatcher = UnconfinedTestDispatcher()
        return FavoriteBetsViewModel(
            GetFavoriteBetsUseCase { modality ->
                onRead()
                filter = modality
                favorites()
            },
            GetBettingParametersUseCase { parameters() },
            DeleteFavoriteBetUseCase { id ->
                deleted = id
                deleteResult
            },
            AddFavoriteBetToCartUseCase { id, options ->
                added = id to options
                addResult
            },
            FixedCoroutineDispatchers(dispatcher),
            autoLoad = autoLoad,
            isSessionCurrent = isSessionCurrent
        )
    }

    private fun game(modality: LotteryModality, type: String, recurring: List<Int> = emptyList()) =
        LotteryGameParameters(modality, 1, "", 60, 6, 20, 15, recurring, emptyList(), contestType = type)
}
