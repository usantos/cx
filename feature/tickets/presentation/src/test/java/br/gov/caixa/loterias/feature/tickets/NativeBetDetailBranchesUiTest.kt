// Literais numéricos descrevem seleções de loteria, identificadores e dados de teste de limites.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.tickets

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.tickets.domain.model.CheckedTicketAward
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetReceipt
import br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailRoute
import br.gov.caixa.loterias.feature.tickets.presentation.screen.ConfirmedBetSelection
import br.gov.caixa.loterias.feature.tickets.presentation.screen.NativeBetDetailFooter
import br.gov.caixa.loterias.feature.tickets.presentation.screen.NativeBetDetailHeader
import br.gov.caixa.loterias.feature.tickets.presentation.screen.NativeBetDetailSelections
import br.gov.caixa.loterias.feature.tickets.presentation.screen.NativeBetTypeBanner
import br.gov.caixa.loterias.feature.tickets.presentation.screen.NativeDetailButton
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class NativeBetDetailBranchesUiTest {
    @get:Rule val compose = createComposeRule()
    private val style = resolveCaixaDSLotteryStyle(2, "Mega-Sena", CaixaDSCampaign.DEFAULT)

    @Test fun headerUsesReceiptNetPrizeAndRecurringRangeInsteadOfOriginalPrize() {
        show {
            NativeBetDetailHeader(
                bet().copy(
                    recurringDraws = 3,
                    time = "14:15",
                    prize = BigDecimal("100"),
                    receipt = ConfirmedBetReceipt(netPrize = BigDecimal("70"))
                ),
                style
            )
        }
        compose.onNodeWithText("Concursos").assertExists()
        compose.onNodeWithText("123 - 125 (3 apostas)").assertExists()
        compose.onNodeWithText("14:15").assertExists()
        compose.onNodeWithText("70,00", substring = true).assertExists()
        compose.onNodeWithText("100,00", substring = true).assertDoesNotExist()
    }

    @Test fun headerFormatsUnfinishedAndUnawardedStatusAndHidesZeroPrize() {
        val current = mutableStateOf(bet().copy(statusId = 101))
        show { NativeBetDetailHeader(current.value, style) }
        compose.onNodeWithText("Concurso\nnão apurado").assertExists()
        compose.onNodeWithText("Valor do prêmio:").assertDoesNotExist()
        compose.runOnIdle { current.value = bet().copy(status = "Aposta não premiada") }
        compose.onNodeWithText("Aposta\nnão premiada").assertExists()
        compose.runOnIdle { current.value = bet().copy(status = "Prêmio pago") }
        compose.onNodeWithText("Prêmio pago").assertExists()
    }

    @Test fun typeBannerShowsEveryApplicableBadgeAndOmitsOrdinaryBet() {
        val current =
            mutableStateOf(bet().copy(exchange = true, poolShare = true, combo = true, mirror = true, surprise = true, recurringDraws = 2))
        show { NativeBetTypeBanner(current.value, style) }
        listOf("Troca", "Bolão", "Combo", "Espelho", "Surpresinha", "Teimosinha").forEach { compose.onNodeWithText(it).assertExists() }
        compose.runOnIdle { current.value = bet() }
        listOf("Troca", "Bolão", "Combo", "Espelho", "Surpresinha", "Teimosinha").forEach {
            compose.onNodeWithText(it).assertDoesNotExist()
        }
    }

    @Test fun detailButtonSupportsDisabledThenDownloadCallback() {
        val enabled = mutableStateOf(false)
        var downloads = 0
        show { NativeDetailButton("Comprovante", style, if (enabled.value) ({ downloads++ }) else null, download = true) }
        compose.onNodeWithContentDescription("Comprovante").assertIsNotEnabled()
        compose.runOnIdle { enabled.value = true }
        compose.onNodeWithContentDescription("Comprovante").assertIsEnabled().performClick()
        compose.runOnIdle { assertEquals(1, downloads) }
    }

    @Test fun footerRepeatActionsRetainCurrentFavoriteLabelAndCallbacks() {
        val calls = mutableListOf<String>()
        val saved = mutableStateOf(false)
        show {
            NativeBetDetailFooter(bet(), style, true, {
                calls.add("edit")
            }, { calls.add("cart") }, { calls.add("add") }, { calls.add("favorite") }, saved.value, BigDecimal.TEN, 3)
        }
        compose.onNodeWithText("Favoritar aposta").performClick()
        compose.onNodeWithText("Editar aposta").performClick()
        compose.onNodeWithText("Adicionar ao carrinho").performClick()
        compose.onNodeWithContentDescription("Carrinho de apostas, 3 itens").performClick()
        compose.runOnIdle {
            assertEquals(listOf("favorite", "edit", "add", "cart"), calls)
            saved.value = true
        }
        compose.onNodeWithText("Aposta favorita").assertExists()
        compose.onNodeWithText("Favoritar aposta").assertDoesNotExist()
    }

    @Test fun footerWithoutRepeatRetainsOnlyCartAndReadOnlyRepeatControlsAreDisabled() {
        val repeat = mutableStateOf(false)
        show { NativeBetDetailFooter(bet(), style, repeat.value, null, {}, null, {}, false, BigDecimal.ZERO, 0) }
        compose.onNodeWithText("Editar aposta").assertDoesNotExist()
        compose.onNodeWithText("Favoritar aposta").assertDoesNotExist()
        compose.onNodeWithContentDescription("Carrinho de apostas, 0 itens").assertIsEnabled()
        compose.runOnIdle { repeat.value = true }
        compose.onNodeWithText("Editar aposta").assertIsNotEnabled()
        compose.onNodeWithText("Adicionar ao carrinho").assertIsNotEnabled()
    }

    @Test fun recurringResultsExpandIndependentlyAndCollapseWithoutLeakingNumbers() {
        val bet = bet().copy(
            recurringDraws = 2,
            receipt = ConfirmedBetReceipt(awards = listOf(CheckedTicketAward(123, "Premiada", value = BigDecimal.TEN)))
        )
        show { NativeBetDetailSelections(bet, listOf(result(123), result(124)), style) }
        compose.onNodeWithText("Situação: Premiada").assertExists()
        compose.onNodeWithText("Situação: Não premiado").assertExists()
        compose.onNodeWithText("Meus números").assertDoesNotExist()
        compose.onNodeWithText("Resultado do Concurso 123").performClick()
        compose.onNodeWithText("Meus números").assertExists()
        compose.onNodeWithText("Resultado do Concurso 124").performClick()
        compose.onAllNodesWithText("Meus números").assertCountEquals(2)
        compose.onNodeWithText("Resultado do Concurso 123").performClick()
        compose.onAllNodesWithText("Meus números").assertCountEquals(1)
    }

    @Test fun selectionReportsActualHitsWithoutCountingDuplicateNumbers() {
        show {
            ConfirmedBetSelection(bet().copy(numbers = listOf(1, 1, 2)), Color.Black, result(123).copy(firstDrawNumbers = listOf(1, 3)))
        }
        compose.onNodeWithText("Acertos:").assertExists()
        compose.onNodeWithText("1 números").assertExists()
    }

    @Test fun superSevenHitsCountMatchingColumnsAndPreserveSelectionType() {
        val bet = bet().copy(
            modalityId = 7,
            numberColumns = listOf(listOf(8), listOf(9), listOf(0), listOf(1), listOf(2), listOf(3), listOf(4))
        )
        show {
            ConfirmedBetSelection(
                bet,
                Color.Black,
                result(
                    123
                ).copy(
                    firstDrawNumbers = listOf(8, 7, 0, 6, 2, 5, 4),
                    firstDrawNumberColumns = listOf(listOf(8), listOf(7), listOf(0), listOf(6), listOf(2), listOf(5), listOf(4))
                )
            )
        }
        compose.onNodeWithText("Suas colunas").assertExists()
        compose.onNodeWithText("4 números").assertExists()
        compose.onNodeWithText("Meus números").assertDoesNotExist()
    }

    @Test fun routeHidesResultButtonForUnfinishedContestButRetainsReceiptAction() {
        show { BetDetailRoute(bet().copy(statusId = 4), {}, {}) }
        compose.onNodeWithText("Ver resultado").assertDoesNotExist()
        compose.onNodeWithContentDescription("Gerar comprovante").performScrollTo().assertIsNotEnabled()
    }

    @Test fun routeUnredeemedPrizeExposesDeadlineAndRedemptionCallback() {
        var redemptions = 0
        show { BetDetailRoute(bet().copy(prize = BigDecimal.TEN), {}, {}, onRedeemPrize = { redemptions++ }) }
        compose.onNodeWithText("Prazo para recebimento de prêmio:\n90 dias após a data do sorteio").performScrollTo().assertExists()
        compose.onNodeWithContentDescription("Resgatar prêmio").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, redemptions) }
    }

    @Test fun routeRedeemedPrizeHidesRedemptionAndAllowsReceiptAndResultCallbacks() {
        val calls = mutableListOf<String>()
        show {
            BetDetailRoute(bet().copy(prize = BigDecimal.TEN, redeemedPrize = true), {
            }, { calls.add("result") }, onDownloadReceipt = { calls.add("receipt") })
        }
        compose.onNodeWithText("Resgatar prêmio").assertDoesNotExist()
        compose.onNodeWithContentDescription("Ver resultado").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Gerar comprovante").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(listOf("result", "receipt"), calls) }
    }

    @Test fun poolRouteDoesNotOfferRepeatingOrFavoritingButRetainsHelpAndCart() {
        val calls = mutableListOf<String>()
        show { BetDetailRoute(bet().copy(poolShare = true), {}, {}, onHelp = { calls.add("help") }, onCart = { calls.add("cart") }) }
        compose.onNodeWithText("Favoritar aposta").assertDoesNotExist()
        compose.onNodeWithText("Editar aposta").assertDoesNotExist()
        compose.onNodeWithContentDescription("Dúvidas").performClick()
        compose.onNodeWithContentDescription("Carrinho de apostas, 0 itens").performClick()
        compose.runOnIdle { assertEquals(listOf("help", "cart"), calls) }
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { Column { content() } } }
    }
    private fun bet() = ConfirmedBet(1, "Mega-Sena", 123, "22/10/2026", "Efetivada", listOf(1, 2), modalityId = 2)
    private fun result(contest: Int) =
        DrawResult("megasena", contest, "22/10/2026", "", listOf(1, 3), emptyList(), emptyList(), false, "", "", "", emptyList())
}
