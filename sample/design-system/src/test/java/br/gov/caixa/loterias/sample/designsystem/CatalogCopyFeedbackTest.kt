package br.gov.caixa.loterias.sample.designsystem

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CatalogCopyFeedbackTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun copyFeedbackExpiresAfterThreeSecondsAndRepeatedClickRestartsTheTimer() {
        compose.mainClock.autoAdvance = false
        compose.setContent {
            LoteriasCaixaDSTheme {
                CatalogCodePanel("Exemplo", "exemplo()", "catalog.copy.usage", "Copiar exemplo")
            }
        }
        val button = compose.onNodeWithTag("simlo.catalog.copy.usage")
        button.performClick()
        compose.mainClock.advanceTimeBy(SETTLE_FRAMES_MILLIS)
        button.assertTextEquals("Copiado")
        compose.mainClock.advanceTimeBy(BEFORE_EXPIRY_MILLIS)
        button.assertTextEquals("Copiado")
        button.performClick()
        compose.mainClock.advanceTimeBy(SETTLE_FRAMES_MILLIS)
        compose.mainClock.advanceTimeBy(BEFORE_EXPIRY_MILLIS)
        button.assertTextEquals("Copiado")
        compose.mainClock.advanceTimeBy(AFTER_EXPIRY_MILLIS)
        button.assertTextEquals("Copiar exemplo")
    }
}

private const val BEFORE_EXPIRY_MILLIS = 2_900L
private const val AFTER_EXPIRY_MILLIS = 200L

private const val SETTLE_FRAMES_MILLIS = 64L
