package br.gov.caixa.loterias.feature.marketplace

import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.core.domain.configuration.repository.AppConfigurationRepository
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.marketplace.di.MarketplaceModule
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilterParameters
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.repository.MarketplaceRepository
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplace
import br.gov.caixa.loterias.feature.marketplace.presentation.mapper.marketplacePoolModalityLabel
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketplaceSpecialConfigurationTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()

    @Test fun `provider uses latest cached home configuration without refreshing`() = runTest {
        var cached = HomeFeatureConfiguration(specialContests = HomeSpecialContests(3000, 1234))
        var refreshes = 0
        val repository = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun marketplaceFilterParameters() = MarketplaceFilterParameters(1, 15)
            override suspend fun homeFeatures() = cached
            override suspend fun refreshHomeFeatures(): HomeFeatureConfiguration {
                refreshes++
                error("Marketplace must use already loaded configuration")
            }
        }
        val provider = MarketplaceModule.marketplaceSpecialContests(repository)
        assertEquals(MarketplaceSpecialContests(3000, 1234), (provider() as AppResult.Success).value)
        cached = HomeFeatureConfiguration(specialContests = HomeSpecialContests(3100, 1300))
        assertEquals(MarketplaceSpecialContests(3100, 1300), (provider() as AppResult.Success).value)
        assertEquals(0, refreshes)
    }

    @Test fun `cached configuration failure returns optional failure`() = runTest {
        val repository = object : AppConfigurationRepository {
            override suspend fun isVersionSupported(versionName: String) = true
            override suspend fun marketplaceFilterParameters() = MarketplaceFilterParameters(1, 15)
            override suspend fun homeFeatures(): HomeFeatureConfiguration = error("Cache unavailable")
        }
        assertTrue(MarketplaceModule.marketplaceSpecialContests(repository)() is AppResult.Failure)
    }

    @Test fun `list header metadata and temporary label use simulation identity instead of available contest`() = runTest(dispatcherRule.dispatcher) {
        val model = model(GetMarketplaceSpecialContestsUseCase { AppResult.Success(MarketplaceSpecialContests(3000, 1234)) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(MarketplaceSpecialContests(3000, 1234), model.state.value.specialContests)
        assertNull(model.state.value.contestSpecialName)
        assertEquals(9999, model.state.value.contestHeader?.contestNumber)
        assertEquals("2026-12-31", model.state.value.contestHeader?.drawDate)
        assertEquals(BigDecimal("123.45"), model.state.value.contestHeader?.estimatedPrize)
        assertEquals(1, model.state.value.pools.size)
    }

    @Test fun `loteca fathers day list header follows simulation contest despite available mismatch`() = runTest(dispatcherRule.dispatcher) {
        val model = model(
            GetMarketplaceSpecialContestsUseCase { AppResult.Success(MarketplaceSpecialContests(3000, 1234)) },
            modalityId = 19, modalityCode = "LOTECA", gameContest = 1234, availableContest = 9999)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("loteca dos pais", model.state.value.contestSpecialName)
        assertEquals(1234, model.state.value.contestHeader?.contestNumber)
    }

    @Test fun `mega anniversary list header preserves native annual special title`() = runTest(dispatcherRule.dispatcher) {
        val model = model(
            GetMarketplaceSpecialContestsUseCase { AppResult.Success(MarketplaceSpecialContests(3000, 1234)) },
            gameContest = 3000, availableContest = 3000)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull("CX list header does not override Mega title for the anniversary", model.state.value.contestSpecialName)
        assertEquals("Mega da Virada", model.state.value.contestHeader?.modality?.specialName)
    }

    @Test fun `missing simulation game keeps pool results and does not invent header metadata`() = runTest(dispatcherRule.dispatcher) {
        val model = model(
            GetMarketplaceSpecialContestsUseCase { AppResult.Success(MarketplaceSpecialContests(3000, 1234)) },
            includeGame = false)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(model.state.value.contestHeader)
        assertEquals(1, model.state.value.pools.size)
    }

    @Test fun `optional configuration failure keeps list content and default header`() = runTest(dispatcherRule.dispatcher) {
        val model = model(GetMarketplaceSpecialContestsUseCase { AppResult.Failure(AppError.Network) })
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNull(model.state.value.contestSpecialName)
        assertEquals(1, model.state.value.pools.size)
        assertNull(model.state.value.errorMessage)
    }

    @Test fun `pool labels override only matching contest even for same modality and type`() {
        val config = MarketplaceSpecialContests(3000, 1234)
        assertEquals("mega-sena\n30 anos", marketplacePoolModalityLabel(pool(3000), emptyMap(), config))
        assertEquals("mega da \nvirada", marketplacePoolModalityLabel(pool(3001), emptyMap(), config))
        assertEquals("mega sena", marketplacePoolModalityLabel(pool(3000).copy(contestType = 1), emptyMap(), config))
        assertEquals("loteca\ndos pais", marketplacePoolModalityLabel(pool(1234).copy(modality = "LOTECA"), emptyMap(), config))
    }

    private fun model(
        specials: GetMarketplaceSpecialContestsUseCase,
        modalityId: Int = 2,
        modalityCode: String = "MEGA_SENA",
        gameContest: Int = 9999,
        availableContest: Int = 3000,
        includeGame: Boolean = true,
    ) = MarketplaceViewModel(
        searchMarketplace = SearchMarketplace(MarketplaceRepository { _, _ -> MarketplacePage(listOf(pool(3000)), 0, 0, 1) }),
        filter = MarketplaceFilter.forModality(modalityId, 2),
        addMarketplacePoolToCart = AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Unexpected) },
        getFavoriteRetailers = GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) },
        setRetailerFavorite = SetRetailerFavoriteUseCase { _, _ -> AppResult.Failure(AppError.Unexpected) },
        dispatchers = FixedCoroutineDispatchers(dispatcherRule.dispatcher),
        getBettingParameters = GetBettingParametersUseCase { AppResult.Success(BettingParameters(
            BigDecimal.ZERO, BigDecimal.ZERO, 0,
            games = if (includeGame) listOf(LotteryGameParameters(
                LotteryModality(modalityId, if (modalityId == 19) "Loteca" else "Mega-Sena", modalityCode,
                    if (modalityId == 19) "Loteca Especial" else "Mega da Virada"),
                gameContest, "2026-12-31", 60, 6, 20, 0, emptyList(), emptyList(),
                contestType = "ESPECIAL", estimatedPrize = BigDecimal("123.45"))) else emptyList(),
            availablePoolModalities = listOf(PoolModalityAvailability(modalityId, modalityCode, "ESPECIAL", contestNumber = availableContest)),
        )) },
        getSpecialContests = specials,
    )

    private fun pool(contest: Int) = MarketplacePool(
        code = "P$contest", modality = "MEGA_SENA", contest = contest, lotteryName = "Centro", drawDate = "",
        sharePrice = "10.00", availableShares = 3, betQuantity = 2, numberQuantity = 6, favoriteLottery = false,
        lotteryId = 1, cityAndState = "", totalShares = 10, contestType = 2,
    )
}
