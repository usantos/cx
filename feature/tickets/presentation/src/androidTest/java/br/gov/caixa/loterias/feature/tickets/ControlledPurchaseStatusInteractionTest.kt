package br.gov.caixa.loterias.feature.tickets

import androidx.compose.ui.test.*
import org.junit.Assert.*
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.v2.createComposeRule
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPurchaseProgressUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.PurchaseProgress
import br.gov.caixa.loterias.feature.tickets.domain.usecase.CheckTicketUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetConfirmedBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseBetsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchaseDetailsUseCase
import br.gov.caixa.loterias.feature.tickets.domain.usecase.GetPurchasesUseCase
import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseDetails
import br.gov.caixa.loterias.feature.tickets.domain.usecase.SavePurchaseAsFavoriteUseCase
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import br.gov.caixa.loterias.feature.tickets.presentation.route.PurchaseDetailDataRoute
import br.gov.caixa.loterias.feature.tickets.presentation.viewmodel.TicketsViewModel

/** Seis estados sintéticos de teste, callbacks reais da interface, nenhum servidor ou POST de compra. */
@RunWith(Parameterized::class)
class ControlledPurchaseStatusInteractionTest(private val statusId: Long) {
    @get:Rule val compose = createComposeRule()
    private lateinit var vm: TicketsViewModel
    private val opened = mutableListOf<String>()
    private val queried = mutableListOf<Long>()
    private var detailsCalls = 0
    private var favoriteCalls = 0
    private lateinit var detail: Purchase

    @Test fun statusActionsRequireExplicitInteractionAndPendingPixQueriesOnlyItsExistingPurchase() {
        val status = when (statusId) {
            3L -> "Débito não autorizado"; 4L -> "Estornada"; 5L -> "Cancelada"
            8L -> "Finalizada"; 10L -> "Aguardando pagamento"; else -> "Cancelada - pagamento não identificado"
        }
        detail = Purchase(9003, "01/10/2026", status, "Pix", BigDecimal.TEN, statusId = statusId,
            paymentMethodId = 6, pixCopyPaste = "CONTROLLED-CODE-9003-DO-NOT-PAY",
            paymentExpiresAt = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(10).toString())
        compose.setContent {
            val model = remember {
                TicketsViewModel(
                    getConfirmedBets = GetConfirmedBetsUseCase { AppResult.Success(emptyList()) },
                    getPurchases = GetPurchasesUseCase { AppResult.Success(emptyList()) },
                    getPurchaseBets = GetPurchaseBetsUseCase { AppResult.Success(emptyList()) },
                    checkTicket = CheckTicketUseCase { AppResult.Success(emptyList()) },
                    getPurchaseDetails = GetPurchaseDetailsUseCase { detailsCalls++; AppResult.Success(PurchaseDetails(detail, emptyList())) },
                    savePurchaseAsFavorite = SavePurchaseAsFavoriteUseCase { _, _, _ -> favoriteCalls++; AppResult.Success(Unit) },
                    getPixPurchaseStatus = GetPurchaseProgressUseCase { id ->
                        queried.add(id); detail = detail.copy(statusId = 8, status = "Finalizada")
                        AppResult.Success(PurchaseProgress(id, 8))
                    },
                    dispatchers = FixedCoroutineDispatchers(Dispatchers.Main.immediate),
                ).also { vm = it }
            }
            CaixaDSTheme(darkTheme = false) {
                PurchaseDetailDataRoute(model, detail, onBack = {}, onOpen = { opened.add(it) })
            }
        }
        when (statusId) {
            3L -> {
                compose.onNodeWithText("Pagamento não realizado").assertIsDisplayed()
                compose.onNodeWithText("Gerar novo Código").performClick()
                compose.runOnIdle { assertEquals(listOf("payment/pix-guidance"), opened) }
            }
            4L, 5L, 12L -> {
                compose.onNodeWithText("Salvar Carrinho").assertIsDisplayed()
                compose.onNodeWithText("Cancelar").performClick()
                compose.runOnIdle { assertNull(vm.state.value.purchaseStatusDialog); assertTrue(opened.isEmpty()) }
            }
            8L -> {
                compose.onNodeWithText("Situação: Finalizada").assertIsDisplayed()
                compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
                compose.runOnIdle { assertNull(vm.state.value.purchaseStatusDialog) }
            }
            10L -> {
                compose.onNodeWithText("Verificar Pagamento").performScrollTo().performClick()
                compose.onNodeWithText("Situação: Finalizada").assertIsDisplayed()
                compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
                compose.runOnIdle {
                    assertTrue(vm.state.value.pixPurchaseCompleted)
                    assertEquals(listOf(9003L), queried); assertEquals(2, detailsCalls)
                    vm.checkPendingPixStatus(); vm.resumePixPurchaseAfterRedirect()
                }
            }
        }
        compose.runOnIdle {
            assertEquals(0, favoriteCalls)
            assertEquals(if (statusId == 10L) listOf(9003L) else emptyList<Long>(), queried)
        }
    }

    companion object {
        @JvmStatic @Parameterized.Parameters(name = "status={0}")
        fun scenarios(): Collection<Array<Any>> = listOf(3L, 4L, 5L, 8L, 10L, 12L).map { arrayOf<Any>(it) }
    }
}
