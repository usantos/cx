package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceBet
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePoolDetail
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceDetailUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceDetailViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceDetailNativeRecoveryTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `missing first millionaria clovers retries at most four GETs then presents final payload`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val vm = model { calls++; AppResult.Success(detail()) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(4, calls)
        assertEquals(false, vm.state.value.detail?.bets?.first()?.cloversLoaded)
        assertFalse(vm.state.value.isLoading)
        assertNull(vm.state.value.errorMessage)
    }

    @Test fun `recovery stops immediately once clovers are loaded`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val vm = model { calls++; AppResult.Success(detail(loaded = calls >= 2)) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
        assertEquals(true, vm.state.value.detail?.bets?.first()?.cloversLoaded)
    }

    @Test fun `resume refresh does not reopen the native three retry budget`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val vm = model { calls++; AppResult.Success(detail()) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.onResume()
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(5, calls)
    }

    @Test fun `empty loaded clovers other modalities and missing later clovers do not retry`() = runTest(dispatcherRule.dispatcher) {
        for (payload in listOf(detail(loaded = true), detail().copy(modality = "MEGA_SENA"),
            detail(loaded = true).copy(bets = listOf(bet(true), bet(false))), detail().copy(bets = emptyList()))) {
            var calls = 0
            model { calls++; AppResult.Success(payload) }
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, calls)
        }
    }

    @Test fun `failure during clover recovery stops retrying and exposes load failure`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val vm = model { calls++; if (calls == 1) AppResult.Success(detail()) else AppResult.Failure(AppError.Network) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, calls)
        assertNull(vm.state.value.detail)
        assertEquals("Não foi possível carregar os detalhes do bolão. Tente novamente.", vm.state.value.errorMessage)
    }

    @Test fun `failed resume refresh keeps the last rendered detail and quantity like native`() = runTest(dispatcherRule.dispatcher) {
        var calls = 0
        val previous = detail(loaded = true)
        val vm = model { calls++; if (calls == 1) AppResult.Success(previous) else AppResult.Failure(AppError.Network) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.increaseShares()
        vm.onResume()
        vm.onResume()
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(previous, vm.state.value.detail)
        assertEquals(2, vm.state.value.selectedShares)
        assertNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.isLoading)
    }

    @Test fun `sold out purchase context presents native notice but reading context does not`() = runTest(dispatcherRule.dispatcher) {
        val soldOut = detail(loaded = true).copy(availableShares = 0)
        val buying = model { AppResult.Success(soldOut) }
        val reading = model(purchaseEnabled = false) { AppResult.Success(soldOut) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Apostas sem quantidade minina necessaria, vamos voltar para a área principal do aposta bolão", buying.state.value.soldOutNotice)
        assertEquals(0, buying.state.value.selectedShares)
        assertNull(reading.state.value.soldOutNotice)
    }

    @Test fun `temporary special name follows the detail contest identity with unavailable configuration fallback`() = runTest(dispatcherRule.dispatcher) {
        val mega = detail(loaded = true).copy(modality = "MEGA_SENA", contest = 3000, contestType = 2)
        val configured = model(specials = GetMarketplaceSpecialContestsUseCase { AppResult.Success(MarketplaceSpecialContests(3000)) }) { AppResult.Success(mega) }
        val otherContest = model(specials = GetMarketplaceSpecialContestsUseCase { AppResult.Success(MarketplaceSpecialContests(3001)) }) { AppResult.Success(mega) }
        val unavailable = model(specials = GetMarketplaceSpecialContestsUseCase { AppResult.Failure(AppError.Network) }) { AppResult.Success(mega) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("mega-sena\n30 anos", configured.state.value.specialName)
        assertNull(otherContest.state.value.specialName)
        assertNull(unavailable.state.value.specialName)
        assertEquals(mega, unavailable.state.value.detail)
    }

    private fun model(purchaseEnabled: Boolean = true, specials: GetMarketplaceSpecialContestsUseCase? = null,
        load: suspend () -> AppResult<MarketplacePoolDetail>,
    ) = MarketplaceDetailViewModel(
        "B1",
        GetMarketplaceDetailUseCase { load() },
        AddMarketplacePoolToCartUseCase { error("Mutation forbidden") },
        FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        purchaseEnabled = purchaseEnabled,
        getSpecialContests = specials
    )

    private fun bet(loaded: Boolean) = MarketplaceBet(emptyList(), emptyList(), false, cloversLoaded = loaded)
    private fun detail(loaded: Boolean = false) = MarketplacePoolDetail(
        code = "B1", modality = "MAIS_MILIONARIA", contest = 400, lotteryId = 1, lotteryName = "Lotérica",
        lotteryNumber = "1", cityAndState = "Brasília - DF", drawDateAndTime = "01/01/2026",
        estimatedPrize = "1000", availableShares = 2, totalShares = 10, betQuantity = 1, numberQuantity = 6,
        sharePriceWithoutFee = "10", lastShareFee = "1", lastSharePriceWithFee = "11", bets = listOf(bet(loaded))
    )
}
