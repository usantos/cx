// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.tickets.presentation.components.PurchaseStatusDialogView
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseDialogAction
import br.gov.caixa.loterias.feature.tickets.presentation.state.PurchaseStatusDialog
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

/** Transições de estado são renderizadas com semântica real do Compose e ações do usuário. */
@ResourcesMode(ResourcesMode.Mode.NATIVE)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36])
class TicketsHistoryAndStatusUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun purchaseDialogDispatchesEachConfiguredAction() {
        val actions = mutableListOf<PurchaseDialogAction>()
        show {
            PurchaseStatusDialogView(
                PurchaseStatusDialog(
                    "Compra",
                    "Escolha uma ação",
                    listOf(
                        PurchaseDialogAction.NEW_PIX to "Gerar Pix",
                        PurchaseDialogAction.CHANGE_PAYMENT to "Trocar pagamento",
                        PurchaseDialogAction.HOME to "Início"
                    )
                ),
                actions::add
            )
        }
        compose.onNodeWithText("Gerar Pix").performClick()
        compose.onNodeWithText("Trocar pagamento").performClick()
        compose.onNodeWithText("Início").performClick()
        compose.runOnIdle {
            assertEquals(listOf(PurchaseDialogAction.NEW_PIX, PurchaseDialogAction.CHANGE_PAYMENT, PurchaseDialogAction.HOME), actions)
        }
    }

    @Test fun purchaseDialogWithNoActionsDoesNotRender() {
        show { PurchaseStatusDialogView(PurchaseStatusDialog("Compra ausente", "Mensagem", emptyList()), {}) }
        compose.onNodeWithText("Compra ausente").assertDoesNotExist()
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { Column { content() } } }
    }
}
