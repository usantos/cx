package br.gov.caixa.loterias.feature.tickets

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.tickets.presentation.screen.BetDetailRoute
import br.gov.caixa.loterias.feature.tickets.presentation.route.PurchaseDetailDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

class ReceiptRecurringContractTest {
    @get:Rule val compose = createComposeRule()
    private val bet = ConfirmedBet(41, "Quina", 7000, "01/10/2026", "Não premiada", listOf(1,2,3,4,5),
        statusId = 9, recurringDraws = 3, exchange = false)
    @Test fun settledRecurringExplainsFirstContestOnceAfterDismissal() {
        val restoration = androidx.compose.ui.test.junit4.StateRestorationTester(compose)
        restoration.setContent { CaixaDSTheme { BetDetailRoute(bet, {}, {}) } }
        compose.onNodeWithText("A conferência dos jogos da Teimosinha é realizada para todos os concursos apostados, no entanto, é exibida apenas a do primeiro concurso.").assertExists()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Entendi").assertDoesNotExist()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Entendi").assertDoesNotExist()
    }
    @Test fun actualPaidOriginalRecurringShowsExchangeGuidance() {
        compose.setContent { CaixaDSTheme { BetDetailRoute(bet.copy(statusId = 11, status = "Prêmio pago"), {}, {}) } }
        compose.onNodeWithText("Esta aposta com Teimosinha não é mais válida para acompanhamento.", substring = true).assertExists()
    }
    @Test fun purchasePixRecreationUsesCapturedServerDeadlineAndExpiresMonotonically() {
        var elapsed = 10_000L
        val purchase = br.gov.caixa.loterias.feature.tickets.domain.model.Purchase(9, "", "Aguardando pagamento", "Pix", java.math.BigDecimal.TEN,
            statusId = 10, paymentMethodId = 6, pixCopyPaste = "CONTROLLED-NOT-PAYABLE", paymentExpiresAt = "2020-01-01T15:10:00")
        var queries = 0
        val model = TicketsViewModel(
            GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
            GetPurchasesUseCase { AppResult.Success(emptyList()) },
            GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
            CheckTicketUseCase { AppResult.Success(emptyList()) },
            FixedCoroutineDispatchers(kotlinx.coroutines.Dispatchers.Main.immediate),
            getPurchaseDetails = GetPurchaseDetailsUseCase { AppResult.Success(PurchaseDetails(purchase, emptyList(), "2020-01-01T15:00:00")) },
            getPixPurchaseStatus = br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase {
                queries++; AppResult.Success(br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress(it, 10))
            },
            pixNoticeClock = java.time.Clock.fixed(java.time.Instant.parse("2020-01-01T18:15:00Z"), java.time.ZoneId.of("America/Sao_Paulo")),
            elapsedMillis = { elapsed },
        )
        val restoration = androidx.compose.ui.test.junit4.StateRestorationTester(compose)
        restoration.setContent { CaixaDSTheme { PurchaseDetailDataRoute(model, purchase, {}) } }
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("10:00"))
        compose.onNodeWithText("10:00").assertExists()
        compose.onNodeWithText("Verificar Pagamento").performClick()
        compose.runOnIdle { org.junit.Assert.assertEquals(1, queries) }
        compose.runOnIdle { elapsed += 120_000 }
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("08:00").assertExists()
        compose.runOnIdle { elapsed += 480_000 }
        restoration.emulateSavedInstanceStateRestore()
        compose.onNode(hasScrollAction()).performScrollToNode(hasText("00:00"))
        compose.onNodeWithText("00:00").assertExists()
        compose.runOnIdle { model.checkPendingPixStatus(); org.junit.Assert.assertEquals(1, queries) }
    }
    @Test fun pendingAndAwardedRecurringRespectCxExclusion() {
        val current = androidx.compose.runtime.mutableStateOf(bet.copy(statusId = 4, status = "Concurso não apurado"))
        compose.setContent { CaixaDSTheme { BetDetailRoute(current.value, {}, {}) } }
        compose.onNodeWithText("Entendi").assertDoesNotExist()
        compose.runOnIdle { current.value = bet.copy(statusId = 100, status = "Premiada") }
        compose.onNodeWithText("Entendi").assertDoesNotExist()
    }
}
