package br.gov.caixa.loterias.feature.cart

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.Density
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.cart.presentation.mapper.favoriteNameSize
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CartAccessibilityRegressionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun favoriteNameKeepsAtLeastFourteenSpAtDoubleFontScale() {
        var measuredSp = 0f
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                CaixaDSTheme { measuredSp = favoriteNameSize("Meu jogo").value }
            }
        }
        compose.runOnIdle { assertTrue("User font scale must not be inversely compensated: $measuredSp", measuredSp >= BASE_NAME_SIZE_SP) }
    }
}

private const val BASE_NAME_SIZE_SP = 14f
