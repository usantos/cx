package br.gov.caixa.loterias.feature.payment

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import br.gov.caixa.loterias.core.designsystem.testing.capturePreviewEvidence
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.core.testing.runner.DynamicFeatureRobolectricTestRunner
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardFront
import br.gov.caixa.loterias.feature.payment.presentation.components.CvvEntry
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyCardActionButton
import br.gov.caixa.loterias.feature.payment.presentation.components.LegacyPaymentInput
import br.gov.caixa.loterias.feature.payment.presentation.components.PaymentProviderCard
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentCardActions
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentCardFlowScreenDelivered
import br.gov.caixa.loterias.feature.payment.presentation.screen.SavedCardsContentDelivered
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardStage
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentCardUiState
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.ResourcesMode

@ResourcesMode(ResourcesMode.Mode.NATIVE)
@RunWith(DynamicFeatureRobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [36], qualifiers = "w360dp-h640dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PaymentProviderLargeFontTest {
    @get:Rule val compose = createAndroidComposeRule<br.gov.caixa.loterias.apostas.HomeDrawerTestActivity>()

    @Test fun normalSavedPreviewKeepsLegacyMaskAndExpiration() {
        showCard(PaymentCardStage.PREVIEW)
        compose.onNodeWithText("**** **** ****  0000").assertExists()
        compose.onNodeWithText("01/2030").assertExists()
        compose.onNodeWithText("-").assertExists()
        capturePreviewEvidence(compose, "card-preview-legacy-normal")
    }

    @Test fun securityCodeEditsTheControlledValueThroughTheInput() {
        val value = mutableStateOf("")
        var changes = 0
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                CvvEntry(value.value, SECURITY_DIGITS + 1, {
                    value.value = it
                    changes++
                })
            }
        }
        compose.onNodeWithTag("simlo.pay.0008").performClick().performTextInput("1234")
        assertEquals("1234", value.value)
        assertEquals(1, changes)
    }

    @Test fun cardFormInputKeepsItsLineVisibleAtLargeFont() = withLargeFont {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme { Box(Modifier.size(320.dp, 640.dp)) { LegacyPaymentInput("Nome", "TITULAR", {}) } }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag("simlo.pay.0006", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertFalse("Campo deve crescer para a linha ampliada", layouts.single().didOverflowHeight)
    }

    @Test fun savedCardsKeepExpirationWithinTheirOwnCardAtLargeFont() = withLargeFont {
        val method = PaymentMethod(PaymentMethod.MERCADO_PAGO_ID, "Mercado Pago")
        val card = SavedPaymentCard("controlled", method.id, "000000", "0000", 1, EXPIRATION_YEAR, SECURITY_DIGITS, "visa", "visa")
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                Box(Modifier.size(320.dp, 640.dp)) {
                    SavedCardsContentDelivered(
                        PaymentUiState(
                            loading = false,
                            methods = listOf(method),
                            cards = mapOf(method.id to listOf(card))
                        ),
                        Modifier,
                        {
                        },
                        {}
                    )
                }
            }
        }
        compose.onNodeWithTag("simlo.pay.0124").performScrollToNode(hasTestTag("simlo.pay.0129"))
        val cardBounds = compose.onNodeWithTag("simlo.pay.0129").fetchSemanticsNode().boundsInRoot
        val date = compose.onNodeWithTag("simlo.pay.0134", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val density = ApplicationProvider.getApplicationContext<Context>().resources.displayMetrics.density
        assertTrue("Validade deve respeitar a margem inferior do cartão", date.bottom <= cardBounds.bottom - SAVED_CARD_INSET * density + 1)
        capturePreviewEvidence(compose, "saved-cards-legacy-large")
    }

    @Test fun savedCardPreviewKeepsItsCompleteActionAtLargeFont() = withLargeFont {
        var continuations = 0
        showCard(PaymentCardStage.PREVIEW, onContinue = { continuations++ })
        val action = compose.onNodeWithTag("simlo.pay.0075").performScrollTo().assertIsDisplayed()
        assertWholeText("simlo.pay.0028")
        action.performClick()
        assertEquals(1, continuations)
        capturePreviewEvidence(compose, "card-preview-legacy-large")
    }

    @Test fun savedCardFrontNeverOverlapsNumberHolderAndExpiration() = withLargeFont {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme {
                Box(Modifier.size(320.dp, 640.dp).verticalScroll(rememberScrollState())) {
                    CreditCardFront("VISA", "0000 0000 0000 0000", "TITULAR DE TESTE COM NOME COMPLETO", "01/2030")
                }
            }
        }
        val number = compose.onNodeWithTag("simlo.pay.0012", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        val holder = compose.onNodeWithTag("simlo.pay.0013", useUnmergedTree = true).fetchSemanticsNode().boundsInRoot
        assertTrue("Nome não pode sobrepor o número", holder.top >= number.bottom)
        capturePreviewEvidence(compose, "card-front-legacy-large")
    }

    @Test fun unsupportedBrandUsesLegacyArtworkInsteadOfInventingATextLogo() {
        compose.setFeatureContent { LoteriasCaixaDSTheme { CreditCardFront("VISA PREPAID", "0000", "-", "01/2030") } }
        compose.onNodeWithText("VISA PREPAID").assertDoesNotExist()
        compose.onNodeWithContentDescription("VISA PREPAID").assertExists()
    }

    @Test fun securityCodeKeepsEveryDigitVisibleAtLargeFont() = withLargeFont {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme { Box(Modifier.size(320.dp, 640.dp)) { CvvEntry("1234", SECURITY_DIGITS + 1, {}) } }
        }
        val digits = compose.onAllNodesWithTag("simlo.pay.0009", useUnmergedTree = true).fetchSemanticsNodes()
        assertEquals(SECURITY_DIGITS + 1, digits.size)
        digits.forEach { node ->
            val layouts = mutableListOf<TextLayoutResult>()
            node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
            val layout = layouts.single()
            assertFalse(
                "O dígito do CVV deve caber na célula",
                layout.hasVisualOverflow
            )
        }
        capturePreviewEvidence(compose, "card-cvv-legacy-large")
    }

    @Test fun normalActionKeepsLegacyHeightAndCenteredText() {
        compose.setFeatureContent {
            LoteriasCaixaDSTheme { LegacyCardActionButton("Confirmar", {}, Modifier.size(width = 280.dp, height = 50.dp)) }
        }
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag("simlo.pay.0028", useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertEquals(androidx.compose.ui.text.style.TextAlign.Center, layouts.single().layoutInput.style.textAlign)
    }

    private fun showCard(stage: PaymentCardStage, onContinue: () -> Unit = {}) {
        val state = PaymentCardUiState(
            stage = stage,
            savedCard = SavedPaymentCard(
                "controlled", PaymentMethod.MERCADO_PAGO_ID,
                "000000", "0000", 1, EXPIRATION_YEAR, SECURITY_DIGITS, "visa", "visa"
            )
        )
        val actions = PaymentCardActions({}, {}, {}, {}, {}, onContinue, {}, {}, {}, {})
        compose.setFeatureContent {
            LoteriasCaixaDSTheme { Box(Modifier.size(320.dp, 640.dp)) { PaymentCardFlowScreenDelivered(state, actions, {}) } }
        }
    }

    private fun assertWholeText(tag: String) {
        val layouts = mutableListOf<TextLayoutResult>()
        compose.onNodeWithTag(tag, useUnmergedTree = true).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertFalse("O texto inteiro deve caber no controle", layouts.single().hasVisualOverflow)
    }

    private fun withLargeFont(block: () -> Unit) {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            block()
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }

    @Test fun providerActionsKeepWholeWordsAndTheirOwnCallbacksAtDoubleFont() {
        val original = RuntimeEnvironment.getFontScale()
        RuntimeEnvironment.setFontScale(2f)
        try {
            var additions = 0
            var expansions = 0
            val method = PaymentMethod(PaymentMethod.MERCADO_PAGO_ID, "Mercado Pago")
            val cards = listOf("first", "second", "third").map {
                SavedPaymentCard(it, method.id, "000000", "1111", 1, EXPIRATION_YEAR, SECURITY_DIGITS, "visa", "visa")
            }
            compose.setFeatureContent {
                LoteriasCaixaDSTheme {
                    Box(Modifier.size(360.dp, 640.dp)) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            PaymentProviderCard(
                                method, cards, cards.take(2), false, PaymentVisualAssets(), {},
                                { additions++ }, {}, { expansions++ }
                            )
                        }
                    }
                }
            }
            listOf("Adicionar Cartão", "Listar Mais").forEach { label ->
                val action = compose.onNodeWithText(label, ignoreCase = true).performScrollTo().assertIsDisplayed()
                val layouts = mutableListOf<TextLayoutResult>()
                action.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
                val layout = layouts.single()
                assertFalse("Complete provider action must fit", layout.hasVisualOverflow)
                assertTrue("Words must not break letter by letter: $label", layout.lineCount <= MAX_ACTION_LINES)
                action.performClick()
            }
            compose.runOnIdle {
                assertEquals(1, additions)
                assertEquals(1, expansions)
            }
        } finally {
            RuntimeEnvironment.setFontScale(original)
        }
    }
}

private const val EXPIRATION_YEAR = 2030
private const val SECURITY_DIGITS = 3
private const val MAX_ACTION_LINES = 2

private const val SAVED_CARD_INSET = 17
