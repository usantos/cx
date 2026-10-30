// Literais numéricos descrevem geometria fixa de teste, IDs das fixtures, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.core.designsystem

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.text.AnnotatedString
import br.gov.caixa.loterias.core.designsystem.components.CaixaDSPixPanel
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CaixaDSPixPanelJvmTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun copyTrimsClipboardDebouncesFeedbackAndExpiresOnlyTheCurrentCode() {
        val seconds = mutableStateOf(125L)
        val code = "  00020101021234567890123456789  "
        var copies = 0
        lateinit var clipboard: ClipboardManager
        compose.setContent {
            clipboard = LocalClipboardManager.current
            CaixaDSTheme {
                CaixaDSPixPanel(
                    1, code, seconds.value, false, {}, { copies++ }, {},
                    modifier = Modifier.verticalScroll(rememberScrollState()), showIntro = false, showHeader = false
                )
            }
        }
        compose.onNodeWithText("02:05").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Copiar código Pix").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Pix copiado").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(code.trim(), clipboard.getText()?.text)
            assertEquals(1, copies)
        }
        compose.mainClock.advanceTimeBy(2_100)
        compose.onNodeWithContentDescription("Copiar código Pix").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(2, copies)
            seconds.value = 0
        }
        compose.onNodeWithText("Código Expirado").assertIsDisplayed()
        compose.onNodeWithText("00:00").assertIsDisplayed()
        compose.onNodeWithContentDescription("Pix copiado").assertDoesNotExist()
        compose.runOnIdle { assertEquals("Código Expirado", clipboard.getText()?.text) }
    }

    @Test
    fun checkingHidesVerifyAndExpiryShowsPurchasesWithoutOverwritingAnotherClipboard() {
        val checking = mutableStateOf(false)
        val seconds = mutableStateOf(90L)
        var checks = 0
        var purchases = 0
        lateinit var clipboard: ClipboardManager
        compose.setContent {
            clipboard = LocalClipboardManager.current
            CaixaDSTheme {
                CaixaDSPixPanel(
                    2, "pix-code", seconds.value, checking.value, { checks++ }, {}, { purchases++ },
                    modifier = Modifier.verticalScroll(rememberScrollState()), formattedAmount = "R$ 12,00"
                )
            }
        }
        compose.onNodeWithText("R$ 12,00").assertIsDisplayed()
        compose.onNodeWithText("01:30").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Verificar Pagamento").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, checks)
            checking.value = true
            clipboard.setText(AnnotatedString("outro conteúdo"))
        }
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
        val progress = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)
        compose.onNode(progress).performScrollTo().assertIsDisplayed()
        compose.runOnIdle {
            checking.value = false
            seconds.value = -1
        }
        compose.onNode(progress).assertDoesNotExist()
        compose.onNodeWithText("Ir para as\nMinhas Compras").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, purchases)
            assertEquals("outro conteúdo", clipboard.getText()?.text)
        }
    }

    @Test
    fun expiredPanelWithoutRecoveryGuidanceDoesNotOfferPaymentOrPurchases() {
        compose.setContent {
            CaixaDSTheme {
                CaixaDSPixPanel(3, "old", 0, false, {}, {}, {}, showIntro = false, showHeader = false, showRecoveryGuidance = false)
            }
        }
        compose.onNodeWithText("Código Expirado").assertIsDisplayed()
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
        compose.onNodeWithText("Ir para as\nMinhas Compras").assertDoesNotExist()
        compose.onNodeWithText("Atenção:").assertDoesNotExist()
        compose.onNodeWithContentDescription("Copiar código Pix").assertDoesNotExist()
    }
}
