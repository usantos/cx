// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSWheel
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButton
import br.gov.caixa.loterias.core.designsystem.components.button.CaixaDSBettingButtonDefaults
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSWheelAndBettingJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun wheelClampsExternalIndexCentersChosenOptionAndHidesEmptyOptions() {
        val options = mutableStateOf(listOf("Uma", "Duas", "Três"))
        val selected = mutableStateOf(-10)
        val choices = mutableListOf<Int>()
        compose.setContent {
            CaixaDSTheme {
                CaixaDSWheel(options.value, selected.value, { choices.add(it) }, Modifier.size(180.dp, 96.dp).testTag("wheel"))
            }
        }
        compose.onNodeWithText("Uma").assertIsDisplayed()
        compose.runOnIdle { assertTrue(choices.isEmpty()) }
        compose.onNodeWithText("Duas").performClick()
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, choices.last()) }
        compose.runOnIdle { selected.value = 99 }
        compose.onNodeWithText("Três").assertIsDisplayed()
        compose.runOnIdle { options.value = emptyList() }
        compose.onNodeWithTag("wheel").assertDoesNotExist()
    }

    @Test
    fun bettingButtonRetainsFullTouchHeightAndDisablesActionsForBothIconPlacements() {
        val enabled = mutableStateOf(true)
        val actions = mutableListOf<String>()
        compose.setContent {
            CaixaDSTheme {
                Column {
                    CaixaDSBettingButton(
                        "Apostar",
                        { actions.add("bet") },
                        Modifier.size(180.dp, 48.dp),
                        Color.Blue,
                        Color.White,
                        enabled = enabled.value,
                        semiBold = true,
                        verticalInset = CaixaDSBettingButtonDefaults.NativeVerticalInset
                    )
                    CaixaDSBettingButton(
                        "Opções",
                        { actions.add("options") },
                        Modifier.size(180.dp, 48.dp),
                        Color.White,
                        Color.Blue,
                        icon = R.drawable.legacy_marketplace_list_arrow,
                        textSizeInDp = true
                    )
                    CaixaDSBettingButton(
                        "Ajuda",
                        { actions.add("help") },
                        Modifier.size(180.dp, 48.dp),
                        Color.Blue,
                        Color.White,
                        icon = R.drawable.legacy_marketplace_help,
                        iconAtStart = true
                    )
                }
            }
        }
        val button = compose.onNodeWithTag("betting-button-Apostar")
        assertEquals(with(compose.density) { 48.dp.toPx() }, button.fetchSemanticsNode().boundsInRoot.height, 1f)
        button.performClick()
        compose.onNodeWithText("Opções").performClick()
        compose.onNodeWithText("Ajuda").performClick()
        compose.onNodeWithTag("betting-dropdown-icon", useUnmergedTree = true).assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(listOf("bet", "options", "help"), actions)
            enabled.value = false
        }
        button.assertIsNotEnabled()
    }
}
