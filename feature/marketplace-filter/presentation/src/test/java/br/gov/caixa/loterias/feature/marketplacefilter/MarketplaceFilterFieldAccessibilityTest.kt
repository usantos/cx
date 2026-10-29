package br.gov.caixa.loterias.feature.marketplacefilter

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.components.MarketplaceFilterValueFields
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.components.MarketplaceRetailerSearch
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterAction
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MarketplaceFilterFieldAccessibilityTest {
    @get:Rule val compose = createComposeRule()
    private val context get() = ApplicationProvider.getApplicationContext<Context>()

    @Test fun filledValueAndQuantityInputsRetainTheirDistinctNames() {
        compose.setContent {
            CaixaDSTheme {
                MarketplaceFilterValueFields(
                    MarketplaceFilterUiState(
                        minimumValue = "10",
                        maximumValue = "20",
                        minimumShares = "2",
                        maximumShares = "4"
                    )
                ) {}
            }
        }
        listOf(
            R.string.ui_valor_minimo_cota_ff3499,
            R.string.ui_valor_maximo_cota_9a12ae,
            R.string.ui_quant_de_cotas_minima_c081e3,
            R.string.ui_quant_de_cotas_maxima_31401a
        ).forEach {
            compose.onNode(hasContentDescription(context.getString(it)) and hasSetTextAction()).assertExists()
        }
    }

    @Test fun shareErrorBelongsToEditableOwnerAndClearsOnCorrection() {
        val state = mutableStateOf(MarketplaceFilterUiState(minimumShares = "4", minimumSharesError = "Mínimo inválido"))
        compose.setContent {
            CaixaDSTheme {
                MarketplaceFilterValueFields(state.value) { action ->
                    if (action is MarketplaceFilterAction.ChangeMinimumShares) {
                        state.value = state.value.copy(minimumShares = action.value, minimumSharesError = null)
                    }
                }
            }
        }
        val input = compose.onNode(
            hasContentDescription(context.getString(R.string.ui_quant_de_cotas_minima_c081e3)) and hasSetTextAction()
        )
        input.assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Mínimo inválido"))
        input.performTextReplacement("2")
        input.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
        compose.runOnIdle { assertEquals("2", state.value.minimumShares) }
    }

    @Test fun filledRetailerSearchKeepsItsSubjectAndEditAction() {
        var query = ""
        compose.setContent {
            CaixaDSTheme {
                MarketplaceRetailerSearch(MarketplaceFilterUiState(lottery = "Lotérica Centro")) { action ->
                    if (action is MarketplaceFilterAction.ChangeLottery) query = action.value
                }
            }
        }
        compose.onNode(hasContentDescription(context.getString(R.string.ui_lotericas_8c948f)) and hasSetTextAction())
            .performTextReplacement("Outra")
        compose.runOnIdle { assertEquals("Outra", query) }
    }
}
