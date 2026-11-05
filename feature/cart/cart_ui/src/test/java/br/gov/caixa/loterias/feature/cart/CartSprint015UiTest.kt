package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartLotecaSelection
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.cart.presentation.components.CartLotecaContent
import br.gov.caixa.loterias.feature.cart.presentation.screen.CurrentCartRowDelivered
import br.gov.caixa.loterias.feature.cart.presentation.screen.currentCartModalityColor
import java.math.BigDecimal
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class CartSprint015UiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun longLotecaTeamsStayOnOneLineInBalancedColumns() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                Box(Modifier.width(336.dp)) {
                    CartLotecaContent(listOf(CartLotecaSelection(1, "MANCHESTER CITY", "BOURNEMOUTH", true, false, false)))
                }
            }
        }
        for (label in listOf("MANCHESTER CITY", "Empate", "BOURNEMOUTH")) {
            val layouts = mutableListOf<TextLayoutResult>()
            compose.onNodeWithText(label, useUnmergedTree = true)
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            assertEquals("$label stays on one line", 1, layouts.single().lineCount)
            assertTrue("$label uses the compact team font", layouts.single().layoutInput.style.fontSize.value <= MAXIMUM_TEAM_FONT_SIZE)
            assertTrue("$label remains complete", !layouts.single().isLineEllipsized(0))
        }
        val columns = listOf("Jogo 01, MANCHESTER CITY", "Jogo 01, Empate", "Jogo 01, BOURNEMOUTH")
            .map { compose.onNodeWithContentDescription(it).fetchSemanticsNode().boundsInRoot }
        assertEquals(columns.first().width, columns.last().width, 1f)
        assertTrue(columns[1].width < columns.first().width)
        for (column in columns.drop(1)) {
            assertEquals(columns.first().center.y, column.center.y, CENTER_TOLERANCE_PIXELS)
        }
    }

    @Test fun lotecaWholeBlockReadoutKeepsAllSelectionsAndClosableLegend() {
        val item = CartItem(
            1,
            CartItemKind.INDIVIDUAL_BET,
            "Loteca",
            "1 aposta(s)",
            BigDecimal.TEN,
            modalityCode = "LOTECA",
            lotecaSelections = listOf(CartLotecaSelection(1, "Casa/SP", "Fora/RJ", true, true, false))
        )
        compose.setFeatureContent { LoteriasCaixaDSTheme { CurrentCartRowDelivered(item, Instant.EPOCH, {}, null) } }
        compose.onNodeWithContentDescription(
            "loteca. Jogo um: Casa/SP selecionado, empate selecionado, Fora/RJ não selecionado. Valor: dez reais."
        ).assertExists()
        compose.onNodeWithContentDescription("Legenda da Loteca").performClick()
        compose.onNodeWithText("Palpite escolhido").assertExists()
        compose.onNodeWithText("Palpite não escolhido").assertExists()
        compose.onNodeWithText("Fechar").performClick()
        compose.onNodeWithText("Palpite escolhido").assertDoesNotExist()
    }

    @Test fun cartTitlesUseNativeListColors() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                assertEquals(LoteriasCaixaDSColorTokens.SuperSeteLight, currentCartModalityColor("SUPER_7"))
                assertEquals(LoteriasCaixaDSColorTokens.DiaDeSorteLight, currentCartModalityColor("DIA_DE_SORTE"))
                assertEquals(LoteriasCaixaDSColorTokens.TimemaniaLight, currentCartModalityColor("TIMEMANIA"))
                LoteriasCaixaDSCampaignTheme(LoteriasCaixaDSCampaign.OCTOBER_ROSE, LoteriasCaixaDSCampaign.OCTOBER_ROSE) {
                    assertEquals(LoteriasCaixaDSColorTokens.OctoberRose, currentCartModalityColor("MEGA_SENA"))
                }
            }
        }
    }
}

private const val CENTER_TOLERANCE_PIXELS = 0.5f
private const val MAXIMUM_TEAM_FONT_SIZE = 12f
