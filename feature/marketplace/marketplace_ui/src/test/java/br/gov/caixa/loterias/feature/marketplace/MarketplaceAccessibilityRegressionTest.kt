package br.gov.caixa.loterias.feature.marketplace

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.presentation.components.MarketplaceCarousel
import br.gov.caixa.loterias.feature.marketplace.presentation.state.MarketplaceUiState
import org.junit.Assert.assertTrue
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
class MarketplaceAccessibilityRegressionTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun carouselQuantityActionsSayWhatTheyChange() {
        val pool = MarketplacePool("B1", "MEGA_SENA", 1, "Lotérica", "27/10/2026", "10", AVAILABLE_SHARES, 1, NUMBERS_PER_BET, false)
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                MarketplaceCarousel(MarketplaceUiState(pools = listOf(pool)), {}, {}, {}, {}, {})
            }
        }
        val increment = compose.onNodeWithTag("carousel-increase-B1").fetchSemanticsNode()
        val descriptions = if (increment.config.contains(
                SemanticsProperties.ContentDescription
            )
        ) {
            increment.config[SemanticsProperties.ContentDescription]
        } else {
            emptyList()
        }
        assertTrue("Quantity action needs a meaningful name", descriptions.any { it.contains("cota", ignoreCase = true) })
    }
}

private const val AVAILABLE_SHARES = 5
private const val NUMBERS_PER_BET = 6
