package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

class MarketplaceFilterScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun screen_preservesMainFilterSections() {
        compose.setContent {
            MarketplaceFilterScreen(
                state = MarketplaceFilterUiState(
                    modalities = listOf(
                        allModalities,
                        MarketplaceFilterModalityUi(2, "Mega-Sena", "Mega-Sena", 1, listOf(6), 60),
                    ),
                    betOptions = listOf(1, 2, 3),
                    numberOptions = listOf(6, 7, 8),
                ),
                onAction = {},
            )
        }

        compose.onNodeWithText("Filtros").assertIsDisplayed()
        compose.onNodeWithText("Modalidades").assertIsDisplayed()
        compose.onNodeWithText("Quantidade de Apostas").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Quantidade de Dezenas").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Números que quero").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Números que não quero").performScrollTo().assertIsDisplayed()
        compose.onAllNodesWithText("Todas as\nmodalidades").assertCountEquals(1)
    }

    @Test
    fun numberQuantity_opensNumericModal() {
        compose.setContent {
            MarketplaceFilterScreen(
                state = MarketplaceFilterUiState(numberOptions = listOf(6, 7, 8)),
                onAction = {},
            )
        }

        compose.onNodeWithTag("number_quantity_selector").performClick()
        compose.onNodeWithText("Confirmar").assertIsDisplayed()
        compose.onNodeWithText("Cancelar").assertIsDisplayed()
    }
}
