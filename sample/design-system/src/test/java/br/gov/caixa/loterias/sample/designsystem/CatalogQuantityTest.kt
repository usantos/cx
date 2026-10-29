package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogQuantityTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun quantityCanIncreaseAndDecreaseWithoutCrossingOneAndCountsEveryInteraction() {
        val state = PreviewState()
        val key = "br.gov.caixa.loterias.core.designsystem.components.CaixaDSMarketplacePoolExpansion"
        compose.setContent { CaixaDSTheme { catalogPreviews.getValue(key)(state) } }
        compose.onNodeWithText("-").assertIsNotEnabled()
        compose.onNodeWithText("+").performClick()
        compose.runOnIdle {
            assertEquals(2, state.shares)
            assertEquals(1, state.count)
        }
        compose.onNodeWithText("-").performClick()
        compose.runOnIdle {
            assertEquals(1, state.shares)
            assertEquals(2, state.count)
            state.decreaseShares()
            assertEquals(1, state.shares)
            assertEquals(THREE_INTERACTIONS, state.count)
        }
        compose.onNodeWithText("-").assertIsNotEnabled()
    }
}

private const val THREE_INTERACTIONS = 3
