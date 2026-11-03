// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.cart.presentation.components.FavoriteConfirmDialog
import br.gov.caixa.loterias.feature.cart.presentation.screen.CartError
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36], qualifiers = "w480dp-h900dp")
class CartBaseDialogsUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun cartErrorAllowsRetryWithExplicitMessage() {
        var retries = 0
        show { CartError("Conexão perdida", PaddingValues()) { retries++ } }
        compose.onNodeWithText("Conexão perdida").assertExists()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun favoriteConfirmationOffersBothCallbacks() {
        var confirm = 0
        var dismiss = 0
        show { FavoriteConfirmDialog("Excluir carrinho?", { confirm++ }, { dismiss++ }) }
        compose.onNodeWithText("Excluir carrinho?").assertExists()
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertEquals(1, confirm)
            assertEquals(1, dismiss)
        }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { content() } }
    }
}
