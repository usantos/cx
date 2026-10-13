package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
        compose.onNodeWithText("Modalidade").assertIsDisplayed()
        compose.onNodeWithText("Quantidade de apostas").assertIsDisplayed()
        compose.onNodeWithText("Quantidade de dezenas").assertIsDisplayed()
        compose.onNodeWithText("Números que quero").assertIsDisplayed()
        compose.onNodeWithText("Números que não quero").assertIsDisplayed()
        compose.onAllNodesWithText("Todas").assertCountEquals(1)
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
