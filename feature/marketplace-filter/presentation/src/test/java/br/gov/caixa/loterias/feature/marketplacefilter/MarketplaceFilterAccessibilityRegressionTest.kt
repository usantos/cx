package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.components.MarketplaceFilterModalities
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MarketplaceFilterAccessibilityRegressionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun selectedModalityPublishesSelectedState() {
        val mega = MarketplaceFilterModalityUi(2, "Mega-Sena", contestType = 1, numberOptions = emptyList(), maximumNumber = 60)
        compose.setContent {
            CaixaDSTheme {
                MarketplaceFilterModalities(MarketplaceFilterUiState(modalities = listOf(mega), selectedModality = mega)) {}
            }
        }
        compose.onNodeWithText("Mega-Sena").assertIsSelected()
    }
}
