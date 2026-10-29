// Literais numéricos descrevem dados sintéticos, níveis de API da plataforma e limites esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.retail

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeAward
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeLotteryDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizeOptionsScreen
import br.gov.caixa.loterias.feature.retail.presentation.screen.PrizePixDataScreen
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixActions
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizePixKeySource
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStage
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PrizeChannelScreensUiTest {
    @get:Rule val compose = createComposeRule()
    private val details = PrizeDetails(9, BigDecimal("125.50"), "synthetic-nsbi", emptyList())
    private fun flush() {
        compose.runOnIdle { Snapshot.sendApplyNotifications() }
        compose.waitForIdle()
    }

    @Test fun `Pix key editing validates ownership then confirmation exposes only beneficiary display values`() {
        val state =
            mutableStateOf(
                PrizeRedemptionUiState(stage = PrizeRedemptionStage.PIX_ENTRY, details = details, cpf = "synthetic-cpf", loading = false)
            )
        val calls = mutableListOf<String>()
        val actions = PrizePixActions(
            { calls += "cancel" },
            { calls += "consult" },
            { calls += "confirm" },
            { state.value = state.value.copy(pixKeySource = PrizePixKeySource.CPF) },
            { state.value = state.value.copy(pixKeySource = PrizePixKeySource.OTHER) },
            { state.value = state.value.copy(pixKey = it) },
            { state.value = state.value.copy(pixOwnershipAccepted = !state.value.pixOwnershipAccepted) }
        )
        compose.setContent { CaixaDSTheme { PrizePixDataScreen(state.value, { calls += "back" }, { calls += "help" }, actions) } }
        compose.onNodeWithText("synthetic-cpf").assertExists()
        compose.onNodeWithText("Continuar").performScrollTo().assertIsEnabled()
        compose.onNodeWithText("Outra Chave").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Celular / e-mail / Chave aleatória").assertExists()
        compose.onNode(hasSetTextAction()).performTextReplacement("invalid")
        flush()
        compose.onNodeWithText("Chave inválida!").assertExists()
        compose.onNodeWithText("Continuar").performScrollTo().assertIsNotEnabled()
        compose.onNode(hasSetTextAction()).performScrollTo().performTextReplacement("fixture@example.test")
        flush()
        compose.onNodeWithText("Chave inválida!").assertDoesNotExist()
        compose.onNodeWithText("Continuar").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText(
            "Estou ciente que o resgate de prêmio só poderá ser realizado para uma conta de titularidade do apostador."
        ).performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Continuar").performScrollTo().assertIsEnabled().performClick()
        compose.runOnIdle {
            assertEquals(listOf("consult"), calls)
            state.value = state.value.copy(
                stage = PrizeRedemptionStage.PIX_CONFIRM,
                pixBeneficiary = PixBeneficiary("***.111.***-**", "Titular de teste", "fixture@example.test", "Banco teste")
            )
        }
        flush()
        compose.onNodeWithText("Titular de teste").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("***.111.***-**").assertExists()
        compose.onNodeWithText("Chave: fixture@example.test").assertExists()
        compose.onNodeWithText("Resgatar").performScrollTo().performClick()
        compose.onNodeWithText("Cancelar").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf("consult", "confirm", "cancel"), calls) }
    }

    @Test fun `Pix CPF tab and entry cancellation route locally without confirming a transfer`() {
        val state =
            mutableStateOf(
                PrizeRedemptionUiState(
                    stage = PrizeRedemptionStage.PIX_ENTRY,
                    details = details,
                    pixKeySource = PrizePixKeySource.OTHER,
                    loading = false
                )
            )
        var back = 0
        var help = 0
        var toggles = 0
        val actions =
            PrizePixActions(
                {
                    error("entry must not cancel a submitted Pix")
                },
                { error("invalid entry must not consult") },
                { error("must not confirm") },
                { state.value = state.value.copy(pixKeySource = PrizePixKeySource.CPF) },
                {},
                {},
                { toggles++ }
            )
        compose.setContent { CaixaDSTheme { PrizePixDataScreen(state.value, { back++ }, { help++ }, actions) } }
        compose.onNode(isToggleable()).performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, toggles) }
        compose.onNodeWithText("CPF").performScrollTo().performClick()
        flush()
        compose.onNodeWithText("Outra Chave").assertExists()
        compose.onNodeWithText("Continuar").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Cancelar").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.runOnIdle {
            assertEquals(1, back)
            assertEquals(1, help)
        }
    }

    @Test fun `lottery QR countdown urgency expires into explicit refresh and produces one haptic at final seconds`() {
        val state = mutableStateOf(
            PrizeRedemptionUiState(
                stage = PrizeRedemptionStage.LOTTERY,
                details = details,
                lotteryCode = LotteryRedemptionCode("", "code", "nsb", 15),
                remainingSeconds = 120,
                loading = false
            )
        )
        val haptics = mutableListOf<HapticFeedbackType>()
        var refresh = 0
        var back = 0
        var help = 0
        val feedback = object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
                haptics +=
                    hapticFeedbackType
            }
        }
        compose.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides feedback) {
                CaixaDSTheme {
                    PrizeLotteryDataScreen(state.value, { back++ }, { help++ }, { refresh++ })
                }
            }
        }
        compose.onNodeWithText("02:00").assertExists()
        compose.onNodeWithText("Escolha uma Lotérica mais próxima").assertExists()
        compose.onNodeWithText("Mostre o QR Code ao atendente").assertExists()
        compose.onNodeWithText("Aproveite seu prêmio!").performScrollTo().assertIsDisplayed()
        for (remaining in listOf(45, 8, 3)) {
            compose.runOnIdle { state.value = state.value.copy(remainingSeconds = remaining) }
            flush()
        }
        compose.runOnIdle {
            assertEquals(listOf(HapticFeedbackType.LongPress), haptics)
            state.value =
                state.value.copy(remainingSeconds = 0)
        }
        flush()
        compose.onNodeWithText("O QR Code expirou").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Gerar\nnovamente").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, refresh)
            assertEquals(1, help)
            assertEquals(1, back)
        }
    }

    @Test fun `result header preserves recurring contest awards and every bet type label`() {
        val state = mutableStateOf(
            PrizeRedemptionUiState(
                stage = PrizeRedemptionStage.OPTIONS,
                loading = false,
                details = details.copy(
                    initialContest = 3000, targetContest = 3001, recurringDraws = 3, isPool = true,
                    exchange = true, combo = true, mirror = true, surprise = true,
                    awards = listOf(PrizeAward(3000, "Sena de teste", BigDecimal.TEN), PrizeAward(3001, "Faixa sem valor", null))
                )
            )
        )
        compose.setContent { CaixaDSTheme { PrizeOptionsScreen(state.value, {}, {}, {}, {}, {}) } }
        compose.onNodeWithText("Concursos").assertExists()
        compose.onNodeWithText("3000 - 3001").assertExists()
        compose.onNodeWithText("Concurso: 3000").assertExists()
        compose.onNodeWithText("Sena de teste").assertExists()
        compose.onNodeWithText("Faixa sem valor").assertExists()
        for (label in listOf(
            "Aposta de Troca",
            "Bolão",
            "Combo",
            "Espelho",
            "Surpresinha",
            "Teimosinha"
        )) {
            compose.onNodeWithText(label).assertExists()
        }
        compose.runOnIdle { state.value = state.value.copy(details = details.copy(netValue = BigDecimal.ZERO)) }
        flush()
        compose.onNodeWithText("Concurso").assertExists()
        compose.onNodeWithText("você foi premiado!").assertExists()
        compose.runOnIdle { state.value = state.value.copy(details = null) }
        flush()
        compose.onNodeWithText("você foi premiado!").assertExists()
    }
}
