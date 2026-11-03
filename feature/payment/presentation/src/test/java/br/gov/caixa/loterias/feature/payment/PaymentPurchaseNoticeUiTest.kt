package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseNoticeDialog
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
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
class PaymentPurchaseNoticeUiTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun missingNoticeDoesNotCreateDialog() {
        show { PurchaseNoticeDialog(null, { _, _ -> true }, {}, {}) }
        compose.onNodeWithText("Atenção").assertDoesNotExist()
    }

    @Test fun informationalNoticeConsumesWithoutOfferingMoreBets() {
        val consumed = mutableListOf<Boolean>()
        var more = 0
        show {
            PurchaseNoticeDialog(PurchaseNoticeUiState("Processamento pendente", false), { _, accepted ->
                consumed.add(accepted)
                true
            }, { more++ }, {})
        }
        compose.onNodeWithText("Processamento pendente").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(listOf(false), consumed)
            assertEquals(0, more)
        }
    }

    @Test fun moreBetsNoticeOnlyNavigatesAfterSuccessfulConsumption() {
        var allowed = false
        var more = 0
        val consumed = mutableListOf<Boolean>()
        show {
            PurchaseNoticeDialog(PurchaseNoticeUiState("Compra concluída", true), { _, accepted ->
                consumed.add(accepted)
                allowed
            }, { more++ }, {})
        }
        compose.onNodeWithText("Apostar mais").performClick()
        compose.runOnIdle {
            assertEquals(0, more)
            allowed = true
        }
        compose.onNodeWithText("Apostar mais").performClick()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(listOf(true, true, false), consumed)
            assertEquals(1, more)
        }
    }

    @Test fun registrationNoticeRoutesRegistrationAndNegativeOnlyConsumes() {
        val notice = PurchaseNoticeUiState("Completar cadastro?", false, true)
        val registrations = mutableListOf<PurchaseNoticeUiState>()
        val consumed = mutableListOf<Boolean>()
        show {
            PurchaseNoticeDialog(notice, { _, accepted ->
                consumed.add(accepted)
                true
            }, {}, registrations::add)
        }
        compose.onNodeWithText("Sim").performClick()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertEquals(listOf(notice), registrations)
            assertEquals(listOf(false), consumed)
        }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setFeatureContent { LoteriasCaixaDSTheme { content() } }
    }
}
