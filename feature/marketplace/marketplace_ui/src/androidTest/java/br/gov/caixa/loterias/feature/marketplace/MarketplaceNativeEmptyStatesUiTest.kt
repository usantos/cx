package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenActions
import br.gov.caixa.loterias.feature.marketplace.presentation.delivery.MarketplaceScreenNavigation
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreenDelivered
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import org.junit.Rule
import org.junit.Test

class MarketplaceNativeEmptyStatesUiTest {
    @get:Rule val compose = createComposeRule()
    private val empty = "Não foram encontrados bolões disponíveis para os filtros informados. Limpe os filtros e tente novamente."

    @Test fun unfilteredFailureUsesNativeEmptyCardAndNoInventedRetryAction() {
        show(MarketplaceUiState(errorMessage = empty))
        compose.onNodeWithText("Ops!").assertIsDisplayed()
        compose.onNodeWithText(empty).assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test fun filteredFailureUsesNativeErrorCardAndKeepsFilterNavigationAvailable() {
        show(MarketplaceUiState(errorMessage = "Ocorreu um erro.\nTente novamente"))
        compose.onNodeWithText("Ops!").assertIsDisplayed()
        compose.onNodeWithText("Ocorreu um erro.\nTente novamente").assertIsDisplayed()
        compose.onNodeWithContentDescription("Filtrar bolões").assertIsEnabled()
        compose.onNodeWithText("Tentar novamente").assertDoesNotExist()
    }

    @Test fun favoriteRetailerWithoutPoolsUsesNativeRetailerMessage() {
        show(MarketplaceUiState(lotteryFiltered = true))
        compose.onNodeWithText("Esta lotérica não possui bolões disponíveis no momento.").assertIsDisplayed()
    }
    private fun show(state: MarketplaceUiState) {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                MarketplaceScreenDelivered(
                    state = state,
                    navigation = MarketplaceScreenNavigation(
                        onBack = {},
                        onFilter = {},
                        onHelp = {},
                        onRetailerSelected = {},
                        onPoolSelected = {},
                        onCart = null,
                        onRetailerSearch = {}
                    ),
                    actions = MarketplaceScreenActions(
                        onRetry = {},
                        onLoadMore = {},
                        onToggleExpanded = {},
                        onDecrease = {},
                        onIncrease = {},
                        onFavorite = {},
                        onAddToCart = {},
                        onSelectTab = {},
                        onRemoveRetailer = {}
                    ),
                    retailerView = false,
                    cartTotal = java.math.BigDecimal.ZERO,
                    cartBetCount = 0
                )
            }
        }
    }
}
