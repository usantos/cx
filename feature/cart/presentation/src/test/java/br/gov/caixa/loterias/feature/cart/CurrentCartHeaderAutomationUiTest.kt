package br.gov.caixa.loterias.feature.cart

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItem
import br.gov.caixa.loterias.core.domain.contract.cart.model.CartItemKind
import br.gov.caixa.loterias.core.domain.contract.cart.model.ShoppingCart
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartActions
import br.gov.caixa.loterias.feature.cart.presentation.components.CurrentCartScaffold
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36], qualifiers = "pt-rBR-w480dp-h900dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class CurrentCartHeaderAutomationUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun publishedHeaderIdsReachTheirOwnCallbacks() {
        val events = mutableListOf<String>()
        val cart = ShoppingCart(
            1,
            BigDecimal.TEN,
            listOf(CartItem(1, CartItemKind.INDIVIDUAL_BET, "Mega-Sena", "Aposta", BigDecimal.TEN))
        )
        compose.setFeatureContent {
            LoteriasCaixaDSTheme(darkTheme = false) {
                CurrentCartScaffold(
                    CartUiState(loading = false, authenticated = true),
                    cart,
                    {},
                    {},
                    CurrentCartActions({ events.add("clear") }, { events.add("favorite") }, {}, {})
                ) {}
            }
        }
        compose.onNodeWithTag("simlo.cart.0143").performClick()
        compose.onNodeWithTag("simlo.cart.0144").performClick()
        compose.runOnIdle { assertEquals(listOf("clear", "favorite"), events) }
    }
}
