package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplace.presentation.screen.MarketplaceScreen
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
        compose.setContent { CaixaDSTheme { MarketplaceScreen(state, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false) } }
    }
}
