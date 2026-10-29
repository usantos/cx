package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBetCartOptions
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetFavoriteBetsUseCase
import br.gov.caixa.loterias.feature.betting.presentation.viewmodel.FavoriteBetsViewModel
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
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
class FavoriteBetsPreflightRulesTest {
    private val modality = LotteryModality(2, "Mega-Sena", "MEGA_SENA")
    private val favorite = FavoriteBet(9, 2, "Mega-Sena", "Minha Mega", listOf(listOf(1, 2, 3, 4, 5, 6)))
    private val game = LotteryGameParameters(
        modality, 2900, "", 60, 6, 20, 10, listOf(0, 2),
        listOf(BetPrice(6, 0, BigDecimal("6.00")))
    )
    private var cart = ShoppingCart(1, BigDecimal("5.00"), emptyList())
    private var suspended = false
    private var current = true
    private var cartReads = 0
    private var added: FavoriteBetCartOptions? = null
    private var cartResult: AppResult<ShoppingCart>? = null
    private var accountResult: AppResult<AccountAccess>? = null
    private var games = listOf(game)
    private var favorites: GetFavoriteBetsUseCase = GetFavoriteBetsUseCase { AppResult.Success(listOf(favorite)) }
    private val reads = mutableListOf<Pair<Int, Int>>()
    private var catalog: List<LotteryModality>? = null

    @Before fun setup() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @After fun cleanup() = Dispatchers.resetMain()

    @Test fun `suspended account prevents cart read and picker`() = runTest {
        suspended = true
        val vm = model()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertNull(vm.state.value.addCandidate)
        assertTrue(vm.state.value.errorMessage.orEmpty().contains("suspensão temporária"))
        assertTrue(vm.state.value.suspensionNotice)
        assertEquals(0, cartReads)
        assertNull(added)
    }

    @Test fun `limit confirmation precedes duplicate and both precede picker`() = runTest {
        cart = cart.copy(items = listOf(cartBet()))
        val vm = model()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertTrue(vm.state.value.dailyLimitConfirmation)
        assertFalse(vm.state.value.duplicateConfirmation)
        assertNull(vm.state.value.addCandidate)
        vm.confirmDailyLimit()
        assertFalse(vm.state.value.dailyLimitConfirmation)
        assertTrue(vm.state.value.duplicateConfirmation)
        assertNull(vm.state.value.addCandidate)
        vm.confirmDuplicate()
        assertEquals(favorite, vm.state.value.addCandidate)
        assertNull(added)
        vm.confirmAdd()
        advanceUntilIdle()
        assertEquals(FavoriteBetCartOptions(1, 0, false), added)
        assertEquals(1, cartReads)
    }

    @Test fun `equal daily limit opens picker without warning and cancel never submits`() = runTest {
        cart = cart.copy(total = BigDecimal("4.00"))
        val vm = model()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertFalse(vm.state.value.dailyLimitConfirmation)
        assertEquals(favorite, vm.state.value.addCandidate)
        vm.cancelAdd()
        assertNull(added)
        cart = cart.copy(total = BigDecimal("5.00"))
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertTrue(vm.state.value.dailyLimitConfirmation)
        vm.cancelAddConfirmation()
        vm.confirmDailyLimit()
        vm.confirmDuplicate()
        assertNull(vm.state.value.addCandidate)
        assertNull(added)
    }

    @Test fun `same numbers from another modality do not count as duplicate`() = runTest {
        cart = cart.copy(total = BigDecimal.ZERO, items = listOf(cartBet().copy(modalityCode = "QUINA")))
        val vm = model()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertFalse(vm.state.value.duplicateConfirmation)
        assertEquals(favorite, vm.state.value.addCandidate)
    }

    @Test fun `clovers distinguish milionaria duplicates while number order does not`() = runTest {
        val millionaire = modality.copy(id = 8, name = "+Milionária", code = "MAIS_MILIONARIA")
        games = listOf(game.copy(modality = millionaire))
        val bet = favorite.copy(modalityId = 8, modalityName = "+Milionária", clovers = listOf(1, 2))
        cart = cart.copy(
            total = BigDecimal.ZERO,
            items = listOf(cartBet().copy(modalityCode = "MAIS_MILIONARIA", selectedClovers = listOf(1, 3)))
        )
        val vm = model()
        vm.requestAdd(bet)
        advanceUntilIdle()
        assertFalse(vm.state.value.duplicateConfirmation)
        vm.cancelAdd()
        cart = cart.copy(items = listOf(cartBet().copy(modalityCode = "MAIS_MILIONARIA", selectedClovers = listOf(2, 1))))
        vm.requestAdd(bet)
        advanceUntilIdle()
        assertTrue(vm.state.value.duplicateConfirmation)
    }

    @Test fun `failed account or cart never bypasses preflight`() = runTest {
        accountResult = AppResult.Failure(AppError.Validation("Conta indisponível"))
        val vm = model()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertEquals("Conta indisponível", vm.state.value.errorMessage)
        assertEquals(0, cartReads)
        assertNull(vm.state.value.addCandidate)
        accountResult = null
        cartResult = AppResult.Failure(AppError.Network)
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertNull(vm.state.value.addCandidate)
        assertTrue(vm.state.value.errorMessage.orEmpty().contains("internet"))
        assertNull(added)
    }

    @Test fun `filtered pagination appends subsequent pages and stops at empty page`() = runTest {
        favorites = object : GetFavoriteBetsUseCase {
            override suspend fun invoke(modalityId: Int?) = AppResult.Success((1L..8L).map { favorite.copy(id = it) })
            override suspend fun page(modalityId: Int, offset: Int): AppResult<List<FavoriteBet>> {
                reads += modalityId to offset
                return AppResult.Success(if (offset == 8) (9L..13L).map { favorite.copy(id = it) } else emptyList())
            }
        }
        val vm = model()
        vm.loadMore()
        assertTrue(reads.isEmpty())
        vm.selectModality(2)
        advanceUntilIdle()
        vm.loadMore()
        advanceUntilIdle()
        assertEquals((1L..13L).toList(), vm.state.value.favorites.map { it.id })
        vm.loadMore()
        advanceUntilIdle()
        vm.loadMore()
        advanceUntilIdle()
        assertEquals(listOf(2 to 8, 2 to 13), reads)
        assertFalse(vm.state.value.canLoadMore)
    }

    @Test fun `old filtered page never appends after modality changed`() = runTest {
        val pending = CompletableDeferred<AppResult<List<FavoriteBet>>>()
        favorites = object : GetFavoriteBetsUseCase {
            override suspend fun invoke(modalityId: Int?) = AppResult.Success(listOf(favorite.copy(modalityId = modalityId ?: 2)))
            override suspend fun page(modalityId: Int, offset: Int) = pending.await()
        }
        val vm = model()
        vm.selectModality(2)
        advanceUntilIdle()
        vm.loadMore()
        vm.selectModality(3)
        advanceUntilIdle()
        pending.complete(AppResult.Success(listOf(favorite.copy(id = 20))))
        advanceUntilIdle()
        assertEquals(listOf(3), vm.state.value.favorites.map { it.modalityId })
        assertFalse(vm.state.value.loadingMore)
    }

    @Test fun `expired session cannot resume pending limit confirmation`() = runTest {
        val vm = model()
        vm.requestAdd(favorite)
        advanceUntilIdle()
        assertTrue(vm.state.value.dailyLimitConfirmation)
        current = false
        vm.confirmDailyLimit()
        vm.confirmDuplicate()
        assertNull(vm.state.value.addCandidate)
        assertNull(added)
    }

    @Test fun `complete catalog keeps unavailable modality filterable and deletable but cannot add`() = runTest {
        val unavailable = LotteryModality(3, "Quina", "QUINA")
        val saved = favorite.copy(modalityId = 3, modalityName = "Quina")
        catalog = listOf(modality, unavailable)
        favorites = GetFavoriteBetsUseCase { id ->
            AppResult.Success(if (id == null || id == 3) listOf(saved) else emptyList())
        }
        val vm = model()
        advanceUntilIdle()
        assertEquals(listOf(2, 3), vm.state.value.modalities.map { it.id })
        vm.selectModality(3)
        advanceUntilIdle()
        assertEquals("Quina", vm.state.value.selectedModalityName)
        assertEquals(listOf(saved), vm.state.value.favorites)
        vm.requestAdd(saved)
        advanceUntilIdle()
        assertNull(vm.state.value.addCandidate)
        assertEquals("Modalidade não disponível no momento.", vm.state.value.errorMessage)
        assertNull(added)
        vm.requestDelete(saved)
        assertEquals(saved, vm.state.value.deleteCandidate)
    }

    private fun cartBet() = CartItem(
        4,
        CartItemKind.INDIVIDUAL_BET,
        "Mega-Sena",
        "",
        BigDecimal("6.00"),
        modalityCode = "MEGA_SENA",
        selectedNumbers = listOf(listOf(6, 5, 4, 3, 2, 1))
    )

    private fun model() = FavoriteBetsViewModel(
        getFavorites = favorites,
        getParameters = { AppResult.Success(BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 10, games)) },
        deleteFavorite = { AppResult.Success(Unit) },
        addFavoriteToCart = { _, options ->
            added = options
            AppResult.Success(cart)
        },
        dispatchers = FixedCoroutineDispatchers(UnconfinedTestDispatcher()),
        getModalities = catalog?.let { values ->
            br.gov.caixa.loterias.feature.betting.domain.usecase.GetLotteryModalitiesUseCase { AppResult.Success(values) }
        },
        getCurrentCart = {
            cartReads++
            cartResult ?: AppResult.Success(cart)
        },
        verifyAccountAccess = { accountResult ?: AppResult.Success(AccountAccess(PostLoginDestination.HOME, suspended, BigDecimal.TEN)) },
        isSessionCurrent = { current }
    )
}
