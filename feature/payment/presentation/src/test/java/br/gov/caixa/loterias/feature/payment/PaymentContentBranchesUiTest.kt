// Literais numéricos descrevem entradas explícitas de teste e valores esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.payment.domain.model.PaymentMethod
import br.gov.caixa.loterias.feature.payment.domain.model.PixCharge
import br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard
import br.gov.caixa.loterias.feature.payment.presentation.components.CreditCardBack
import br.gov.caixa.loterias.feature.payment.presentation.components.ProviderHeader
import br.gov.caixa.loterias.feature.payment.presentation.components.PurchaseNoticeDialog
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_EXPIRED_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PIX_RECOVERY_GUIDANCE
import br.gov.caixa.loterias.feature.payment.presentation.mapper.PurchaseNoticeUiState
import br.gov.caixa.loterias.feature.payment.presentation.route.PaymentVisualAssets
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataActions
import br.gov.caixa.loterias.feature.payment.presentation.screen.PaymentDataContent
import br.gov.caixa.loterias.feature.payment.presentation.screen.PixContent
import br.gov.caixa.loterias.feature.payment.presentation.screen.SavedCardsContent
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentMode
import br.gov.caixa.loterias.feature.payment.presentation.state.PaymentUiState
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w480dp-h900dp")
class PaymentContentBranchesUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun loadingPrecedesRecoveryAndRecoveryCanOpenPurchases() {
        var purchases = 0
        val state = mutableStateOf(PaymentUiState(pixRecoveryRequired = true))
        show { DataContent(state.value, purchases = { purchases++ }) }
        compose.onNodeWithText(recovery).assertDoesNotExist()
        compose.runOnIdle { state.value = state.value.copy(loading = false) }
        compose.onNodeWithText(recovery).assertExists()
        compose.onNodeWithText("Minhas Compras").performClick()
        compose.runOnIdle { assertEquals(1, purchases) }
    }

    @Test fun sessionMismatchUsesRecoveryBeforeShowingAvailablePaymentMethods() {
        show { DataContent(PaymentUiState(loading = false, pixSessionMismatch = true, methods = listOf(PaymentMethod(6, "Pix")))) }
        compose.onNodeWithText(recovery).assertExists()
        compose.onNodeWithText("Gere um código Pix Copia e Cola para pagar suas apostas.").assertDoesNotExist()
    }

    @Test fun cardsModeOmitsHelpAndVisitorEmptyStateRetainsHeading() {
        show { DataContent(PaymentUiState(loading = false), mode = PaymentMode.CARDS, privateData = false) }
        compose.onNodeWithText("Meus Cartões").assertExists()
        compose.onNodeWithText("Cartões cadastrados").assertExists()
        compose.onNodeWithContentDescription("Ajuda").assertDoesNotExist()
        compose.onNodeWithText("Você ainda não possui um cartão de crédito cadastrado.", substring = true).assertDoesNotExist()
    }

    @Test fun methodsModeExposesHelpWithoutRequiringLoadedMethods() {
        var helps = 0
        show { DataContent(PaymentUiState(loading = false), help = { helps++ }) }
        compose.onNodeWithText("Forma de pagamento").assertExists()
        compose.onNodeWithContentDescription("Ajuda").performClick()
        compose.runOnIdle { assertEquals(1, helps) }
    }

    @Test fun savedCardsEmptyStateExplainsWhereToRegisterCards() {
        show { SavedCardsContent(PaymentUiState(loading = false), Modifier, {}, {}) }
        compose.onNodeWithText(
            "Você ainda não possui um cartão de crédito cadastrado. " +
                "A inclusão de um ou mais cartões de crédito deve ser feita ao realizar uma compra."
        ).assertExists()
        compose.onNodeWithText("Cartões cadastrados").assertDoesNotExist()
    }

    @Test fun savedCardsSupportEachBrandAndForwardExactCardForOpenAndDelete() {
        val brand = mutableStateOf("visa")
        val opened = mutableListOf<SavedPaymentCard>()
        val removed = mutableListOf<SavedPaymentCard>()
        show {
            SavedCardsContent(
                PaymentUiState(
                    loading = false,
                    methods = listOf(PaymentMethod(1, "Mercado Pago")),
                    cards = mapOf(1L to listOf(card().copy(brand = brand.value)))
                ),
                Modifier,
                removed::add,
                opened::add
            )
        }
        listOf("visa", "mastercard", "hipercard", "diners", "elo", "american express", "unknown").forEach { name ->
            compose.runOnIdle { brand.value = name }
            compose.onNodeWithContentDescription(name).assertExists()
        }
        compose.onNodeWithText("**** **** ****  4589").performClick()
        compose.onNodeWithContentDescription("Remover cartão").performClick()
        compose.runOnIdle {
            assertEquals(listOf(card().copy(brand = "unknown")), opened)
            assertEquals(listOf(card().copy(brand = "unknown")), removed)
        }
    }

    @Test fun savedCardsRecargaProviderUsesOwnGuidanceAndExpiration() {
        show {
            SavedCardsContent(
                PaymentUiState(
                    loading = false,
                    methods = listOf(PaymentMethod(5, "RecargaPay")),
                    cards = mapOf(5L to listOf(card().copy(paymentMethodId = 5)))
                ),
                Modifier,
                {
                },
                {}
            )
        }
        compose.onNodeWithContentDescription("RecargaPay").assertExists()
        compose.onNodeWithText("Confira aqui sua lista de cartões salvos no RecargaPay").assertExists()
        compose.onNodeWithText("Venc.:10/2030").assertExists()
    }

    @Test fun nullPixDoesNotExposeAnInventedPaymentCode() {
        show { PixContent(null, false, Modifier, {}, {}) }
        compose.onNodeWithText("Pix Copia e Cola").assertDoesNotExist()
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
    }

    @Test fun expiredPixShowsRecoveryGuidanceAndPurchasesInsteadOfVerification() {
        var purchases = 0
        show { PixContent(pix("2000-01-01T00:00:00Z"), false, Modifier, {}, { purchases++ }) }
        compose.onNodeWithText("Código Expirado").assertExists()
        compose.onNodeWithText("00:00").assertExists()
        compose.onNodeWithText(PIX_EXPIRED_GUIDANCE).assertExists()
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
        compose.onNodeWithText("Ir para as\nMinhas Compras").performScrollTo().performClick()
        compose.runOnIdle { assertEquals(1, purchases) }
    }

    @Test fun activePixCanCheckAndCheckingStateSuppressesRepeatedCheck() {
        var checks = 0
        val checking = mutableStateOf(false)
        show { PixContent(pix("2099-01-01T00:00:00Z"), checking.value, Modifier, { checks++ }, {}) }
        compose.onNodeWithText(PIX_RECOVERY_GUIDANCE).assertExists()
        compose.onNodeWithText("Verificar Pagamento").performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(1, checks)
            checking.value = true
        }
        compose.onNodeWithText("Verificar Pagamento").assertDoesNotExist()
        compose.onNodeWithText("Ir para as\nMinhas Compras").assertDoesNotExist()
    }

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

    @Test fun providerHeaderCanChangeFromEitherBackOrChangeTextAndCanBeReadOnly() {
        var changes = 0
        val editable = mutableStateOf(true)
        show { ProviderHeader("RecargaPay", if (editable.value) ({ changes++ }) else null) }
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.onNodeWithText("Alterar").performClick()
        compose.runOnIdle {
            assertEquals(2, changes)
            editable.value = false
        }
        compose.onNodeWithText("Alterar").assertHasNoClickAction()
        compose.onNodeWithContentDescription("RecargaPay").assertExists()
    }

    @Test fun creditCardBackMagnifierAppearsOnlyWhenRequested() {
        val magnifier = mutableStateOf(false)
        show { CreditCardBack(magnifier.value, "mastercard") }
        compose.onNodeWithText("XXX").assertDoesNotExist()
        compose.runOnIdle { magnifier.value = true }
        compose.onNodeWithText("XXX").assertExists()
    }

    private fun show(content: @Composable () -> Unit) {
        compose.setContent { CaixaDSTheme { content() } }
    }

    @Composable private fun DataContent(
        state: PaymentUiState,
        mode: PaymentMode = PaymentMode.METHODS,
        purchases: () -> Unit = {},
        help: () -> Unit = {},
        privateData: Boolean = true,
    ) {
        PaymentDataContent(mode, state, PaymentVisualAssets(), {}, help, {}, {}, purchases, privateData, PaymentDataActions({}, {}, {}))
    }
    private fun card() = SavedPaymentCard("saved", 1, "123456", "4589", 10, 2030, 3, "visa", "visa")
    private fun pix(expiration: String) = PixCharge("CODE-TEST", "transaction", "", "", expiration, 42, BigDecimal.TEN)
    private val recovery = "Não foi possível recuperar o código Pix. Confira suas compras antes de gerar outro código."
}
