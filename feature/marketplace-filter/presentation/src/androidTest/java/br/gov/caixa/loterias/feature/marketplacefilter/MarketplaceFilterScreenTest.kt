package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextClearance
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.toDomain
import br.gov.caixa.loterias.feature.marketplace.domain.rule.MarketplaceFilterRules
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.screen.MarketplaceFilterScreen
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.mapper.allModalities

class MarketplaceFilterScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun currencyDigits_applyAsCentsLikeNativeMask() {
        val state = mutableStateOf(MarketplaceFilterUiState(lotteryEnabled = false))
        var appliedQuery: Map<String, String>? = null
        compose.setContent {
            MarketplaceFilterScreen(state = state.value, onAction = { action ->
                when (action) {
                    is MarketplaceFilterAction.ChangeMinimumValue -> state.value = state.value.copy(minimumValue = action.value)
                    is MarketplaceFilterAction.ChangeMaximumValue -> state.value = state.value.copy(maximumValue = action.value)
                    MarketplaceFilterAction.Apply -> appliedQuery = MarketplaceFilterRules.toQuery(state.value.toDomain())
                    else -> Unit
                }
            })
        }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("1")
        compose.runOnIdle { assertEquals("0,01", state.value.minimumValue) }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("2")
        compose.runOnIdle { assertEquals("0,12", state.value.minimumValue) }
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("3")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("5000")
        compose.onNodeWithText("Aplicar").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals("1.23", appliedQuery?.get("valorMinimoCota"))
            assertEquals("50.00", appliedQuery?.get("valorMaximoCota"))
        }
        compose.onAllNodes(hasSetTextAction())[0].performScrollTo().performTextClearance()
        compose.onAllNodes(hasSetTextAction())[0].performTextInput("1")
        compose.onNodeWithText("Aplicar").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("0.01", appliedQuery?.get("valorMinimoCota")) }
    }

    @Test
    fun restoredCurrency_keepsItsAmountUntilEditedAndZeroRemovesConstraint() {
        val state = mutableStateOf(MarketplaceFilterUiState(
            lotteryEnabled = false, minimumValue = "10,50", maximumValue = "20,00"))
        var appliedQuery: Map<String, String>? = null
        compose.setContent {
            MarketplaceFilterScreen(state = state.value, onAction = { action ->
                when (action) {
                    is MarketplaceFilterAction.ChangeMinimumValue -> state.value = state.value.copy(minimumValue = action.value)
                    is MarketplaceFilterAction.ChangeMaximumValue -> state.value = state.value.copy(maximumValue = action.value)
                    MarketplaceFilterAction.Apply -> appliedQuery = MarketplaceFilterRules.toQuery(state.value.toDomain())
                    else -> Unit
                }
            })
        }
        compose.onNodeWithText("Aplicar").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals("10.50", appliedQuery?.get("valorMinimoCota"))
            assertEquals("20.00", appliedQuery?.get("valorMaximoCota"))
        }
        compose.onAllNodes(hasSetTextAction())[0].performScrollTo().performTextInput("1")
        compose.onAllNodes(hasSetTextAction())[1].performScrollTo().performTextClearance()
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("0")
        compose.onNodeWithText("Aplicar").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals("105.01", appliedQuery?.get("valorMinimoCota"))
            assertEquals(null, appliedQuery?.get("valorMaximoCota"))
        }
    }

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

    @Test
    fun quantityOfBets_preservesLargerLegacyCellsThanNumberDialog() {
        compose.setContent {
            MarketplaceFilterScreen(
                state = MarketplaceFilterUiState(
                    betOptions = (1..15).toList(),
                    numberOptions = listOf(6, 7, 8),
                ),
                onAction = {},
            )
        }

        // item_dezena.xml usa 45dp, enquanto item_dezena_quero_nao_quero_cartela.xml usa 29dp.
        compose.onNodeWithText("Quantidade de Apostas").performScrollTo()
        compose.onNodeWithText("01").assertWidthIsEqualTo(45.dp).assertHeightIsEqualTo(45.dp)
        compose.onNodeWithTag("number_quantity_selector").performScrollTo().performClick()
        compose.onNode(hasText("06") and hasAnyAncestor(isDialog())).assertWidthIsEqualTo(29.dp).assertHeightIsEqualTo(29.dp)
    }

    @Test
    fun betGrid_alignsNativeFixedWidthCellsAtStartOfEachSpan() {
        compose.setContent {
            MarketplaceFilterScreen(
                state = MarketplaceFilterUiState(betOptions = (1..15).toList()),
                onAction = {},
            )
        }
        compose.onNodeWithText("Quantidade de Apostas").performScrollTo()
        val first = compose.onNodeWithText("01").fetchSemanticsNode().boundsInRoot
        val seventh = compose.onNodeWithText("07").fetchSemanticsNode().boundsInRoot
        val expectedLeft = with(compose.density) { 14.dp.toPx() }
        assertEquals(expectedLeft, first.left, 1f)
        assertEquals(first.left, seventh.left, 1f)
        assertEquals(with(compose.density) { 53.dp.toPx() }, seventh.top - first.top, 1f)
    }

    @Test
    fun filterCanvas_preservesNativeWhiteBackground() {
        compose.setContent {
            MarketplaceFilterScreen(state = MarketplaceFilterUiState(), onAction = {})
        }
        val canvas = compose.onNodeWithTag("marketplace_filter_screen").captureToImage().toPixelMap()
        assertEquals(Color.White, canvas[0, canvas.height / 2])
    }

    @Test
    fun numberDialog_usesNativeDarkNumberInkInsideUnselectedCircle() {
        compose.setContent {
            MarketplaceFilterScreen(
                state = MarketplaceFilterUiState(numberOptions = listOf(6, 7, 8)),
                onAction = {},
            )
        }
        compose.onNodeWithTag("number_quantity_selector").performScrollTo().performClick()
        val pixels = compose.onNode(hasText("06") and hasAnyAncestor(isDialog()))
            .captureToImage().toPixelMap()
        // Amostra a área do glifo, excluindo a borda circular. ToggleButton nativo usa cinza110.
        val nativeInk = Color(0xFF404B52)
        var nativeInkPixels = 0
        for (y in pixels.height / 4 until pixels.height * 3 / 4) {
            for (x in pixels.width / 4 until pixels.width * 3 / 4) {
                if (pixels[x, y] == nativeInk) nativeInkPixels++
            }
        }
        assertTrue("Expected native #404B52 number ink, got $nativeInkPixels pixels", nativeInkPixels > 4)
    }

    @Test
    fun selectedQuantity_preservesTwoDigitsAndRemoveAction() {
        var action: MarketplaceFilterAction? = null
        compose.setContent {
            MarketplaceFilterScreen(
                state = MarketplaceFilterUiState(betOptions = listOf(1), selectedNumberQuantity = 6),
                onAction = { action = it },
            )
        }
        compose.onNodeWithText("06").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Remover 06").performClick()
        compose.runOnIdle { assertEquals(MarketplaceFilterAction.SelectNumberQuantity(null), action) }
    }

    @Test
    fun selectingCurrentNumberQuantity_keepsItSelectedLikeLegacy() {
        var action: MarketplaceFilterAction? = null
        compose.setContent {
            MarketplaceFilterScreen(
                state = MarketplaceFilterUiState(
                    numberOptions = listOf(6, 7, 8),
                    selectedNumberQuantity = 6,
                ),
                onAction = { action = it },
            )
        }

        compose.onNodeWithTag("number_quantity_selector").performScrollTo().performClick()
        compose.onNode(hasText("06") and hasAnyAncestor(isDialog())).performClick()
        compose.onNodeWithText("Confirmar").performClick()
        compose.runOnIdle { assertEquals(MarketplaceFilterAction.SelectNumberQuantity(6), action) }
    }
}
