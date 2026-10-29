package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.usecase.GetBettingParametersUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.GetMarketplaceSpecialContestsUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceFilter
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePage
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.AddMarketplacePoolToCartUseCase
import br.gov.caixa.loterias.feature.marketplace.domain.usecase.SearchMarketplaceUseCase
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen
import br.gov.caixa.loterias.feature.marketplace.presentation.viewmodel.MarketplaceViewModel
import br.gov.caixa.loterias.feature.retail.domain.usecase.GetFavoriteRetailersUseCase
import br.gov.caixa.loterias.feature.retail.domain.usecase.SetRetailerFavoriteUseCase
import java.math.BigDecimal
import org.junit.Rule
import org.junit.Test

class MarketplaceModalityLabelsUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun retailerCardsUseConfiguredSpecialContestNameInCarouselAndList() {
        withRetailerCards(MarketplaceFilter.empty().withLottery("101")) {
            compose.onNode(hasText("mega 30 anos") and hasAnyAncestor(hasTestTag("marketplace-carousel"))).assertIsDisplayed()
            compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasTestTag("pool-list-B5"))
            compose.onNode(hasText("mega 30 anos") and hasAnyAncestor(hasTestTag("pool-list-B5"))).assertIsDisplayed()
        }
    }

    @Test fun selectingOneModalityKeepsRetailerDataInListWhileCarouselShowsModality() {
        withRetailerCards(MarketplaceFilter.forModality(2, 2).withLottery("101")) {
            compose.onNode(hasText("mega 30 anos") and hasAnyAncestor(hasTestTag("marketplace-carousel"))).assertIsDisplayed()
            compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasTestTag("pool-list-B5"))
            compose.onNode(hasText("Centro") and hasAnyAncestor(hasTestTag("pool-list-B5"))).assertIsDisplayed()
            compose.onNode(hasText("mega 30 anos") and hasAnyAncestor(hasTestTag("pool-list-B5"))).assertDoesNotExist()
        }
    }

    @Test fun configuredContestNumberOverridesBackendNameAcrossCarouselAndList() {
        withRetailerCards(MarketplaceFilter.empty().withLottery("101"), MarketplaceSpecialContests(megaThirtyYearsContest = 5000)) {
            compose.onNode(hasText("mega-sena\n30 anos") and hasAnyAncestor(hasTestTag("marketplace-carousel"))).assertIsDisplayed()
            compose.onNodeWithTag("marketplace-pools-list").performScrollToNode(hasTestTag("pool-list-B5"))
            compose.onNode(hasText("mega-sena\n30 anos") and hasAnyAncestor(hasTestTag("pool-list-B5"))).assertIsDisplayed()
        }
    }

    private fun withRetailerCards(filter: MarketplaceFilter, specialContests: MarketplaceSpecialContests? = null, assertions: () -> Unit) {
        val pool = MarketplacePool("B1", "MEGA_SENA", 5000, "Centro", "31/12/2026",
            "10", 10, 2, 6, false, 101, "Brasília - DF", 20, contestType = 2)
        val parameters = BettingParameters(BigDecimal.ZERO, BigDecimal.ZERO, 0,
            listOf(LotteryGameParameters(LotteryModality(2, "Mega-Sena", "MEGA_SENA", "Mega 30 Anos"),
                5000, "31/12/2026", 60, 6, 20, 1, emptyList(), emptyList(), contestType = "ESPECIAL")))
        val model = MarketplaceViewModel(
            SearchMarketplaceUseCase { _, _ -> AppResult.Success(MarketplacePage((1..5).map {
                pool.copy(code = "B$it", modality = if (it == 1 || it == 5) "MEGA_SENA" else "QUINA")
            }, 0, 0, 5)) },
            filter,
            AddMarketplacePoolToCartUseCase { AppResult.Failure(AppError.Network) },
            GetFavoriteRetailersUseCase { AppResult.Success(emptyList()) },
            SetRetailerFavoriteUseCase { _, _ -> AppResult.Success(Unit) },
            getBettingParameters = GetBettingParametersUseCase { AppResult.Success(parameters) },
            getSpecialContests = specialContests?.let { configured -> GetMarketplaceSpecialContestsUseCase { AppResult.Success(configured) } },
        )
        val store = ViewModelStore().apply { put("labels", model) }
        try {
            compose.setContent {
                val state by model.state.collectAsState()
                CaixaDSTheme {
                    MarketplaceScreen(state, onBack = {}, onFilter = {}, onHelp = {}, onRetailerSelected = {},
                        onRetry = {}, onLoadMore = {}, onPoolSelected = {}, onToggleExpanded = {},
                        onDecrease = {}, onIncrease = {}, onFavorite = {}, onAddToCart = {},
                        onSelectTab = {}, onRemoveRetailer = {}, retailerView = false)
                }
            }
            // Busca e configuração do cabeçalho são tarefas de IO independentes. Aguarde ambos os
            // contratos antes de verificar qual nome a tela real renderiza.
            compose.waitUntil(5_000) {
                val ready = model.state.value
                !ready.isLoading && ready.pools.size == 5 &&
                    ready.modalityNames["MEGA_SENA" to 2] == "mega 30 anos" &&
                    ready.specialContests == (specialContests ?: MarketplaceSpecialContests())
            }
            compose.waitForIdle()
            assertions()
        } finally {
            store.clear()
        }
    }
}
