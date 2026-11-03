package br.gov.caixa.loterias.feature.marketplacefilter

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.components.MarketplaceFilterModalities
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterModalityUi
import br.gov.caixa.loterias.feature.marketplacefilter.presentation.state.MarketplaceFilterUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class MarketplaceFilterAccessibilityRegressionTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun selectedModalityPublishesSelectedState() {
        val mega = MarketplaceFilterModalityUi(2, "Mega-Sena", contestType = 1, numberOptions = emptyList(), maximumNumber = 60)
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                MarketplaceFilterModalities(MarketplaceFilterUiState(modalities = listOf(mega), selectedModality = mega)) {}
            }
        }
        compose.onNodeWithText("Mega-Sena").assertIsSelected()
    }
}
