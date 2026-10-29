// Literais numéricos descrevem entradas explícitas de teste e valores esperados dos dados de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.apostas

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.components.base.CaixaDSText
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.navigation.action.AppNavigationActions
import br.gov.caixa.loterias.core.navigation.action.SessionNavigationActions
import br.gov.caixa.loterias.core.navigation.state.rememberAppSessionState
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetPaymentMethodsUseCase
import br.gov.caixa.loterias.feature.payment.domain.usecase.GetSavedCardsUseCase
import br.gov.caixa.loterias.feature.payment.navigation.PaymentNavigationDependencies
import br.gov.caixa.loterias.feature.payment.navigation.paymentGraph
import java.lang.reflect.Proxy
import java.time.Clock
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class)
class PaymentGraphFlowUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private lateinit var nav: NavHostController
    private var reads = 0

    @Test fun realExpiredPixAliasReturnsToPurchaseHistoryWithoutGeneratingCharge() {
        show("payment/pix-expired")
        compose.onNodeWithText("PURCHASES FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    @Test fun guestCardsDoNotReadSavedPrivateCardsAndCanReturn() {
        show("payment/cards?methodId=7")
        compose.onNodeWithText("Meus Cartões").assertIsDisplayed()
        compose.onNode(
            androidx.compose.ui.test.hasContentDescription("Voltar") and
                androidx.compose.ui.test.SemanticsMatcher.expectValue(
                    androidx.compose.ui.semantics.SemanticsProperties.Role,
                    androidx.compose.ui.semantics.Role.Button
                )
        ).performClick()
        compose.onNodeWithText("ORIGIN FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    @Test fun methodReadFailureDoesNotStartAnyPayment() {
        show("payment/method")
        compose.onNodeWithText("Forma de pagamento").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, reads) }
        compose.onNode(
            androidx.compose.ui.test.hasContentDescription("Voltar") and
                androidx.compose.ui.test.SemanticsMatcher.expectValue(
                    androidx.compose.ui.semantics.SemanticsProperties.Role,
                    androidx.compose.ui.semantics.Role.Button
                )
        ).performClick()
        compose.onNodeWithText("ORIGIN FIXTURE").assertIsDisplayed()
    }

    @Test fun confirmationOpensPurchaseHistoryWithoutSubmittingAgain() {
        show("payment/confirmation")
        compose.onNodeWithText("Acompanhe suas compras").performClick()
        compose.onNodeWithText("PURCHASES FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    @Test fun successReturnsToHomeWithoutAnotherPayment() {
        show("payment/success")
        compose.onNodeWithText("Voltar ao início").performClick()
        compose.onNodeWithText("HOME FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    @Test fun failedPaymentRetryReturnsToMethods() {
        show("payment/failure")
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Forma de pagamento").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, reads) }
    }

    @Test fun explicitCardFormDoesNotSubmitBeforeConsent() {
        show("payment/card-flow/7")
        compose.onNodeWithText("Nome igual ao cartão").assertExists()
        compose.onNodeWithContentDescription("Selecionar Mês de validade").performScrollTo().performClick()
        compose.onNodeWithText("12").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Selecionar Ano de validade").performScrollTo().performClick()
        compose.onNodeWithText("2027").performScrollTo().performClick()
        compose.onNodeWithText("12").assertExists()
        compose.onNodeWithText("2027").assertExists()
        compose.onNode(
            androidx.compose.ui.test.hasContentDescription("Voltar") and
                androidx.compose.ui.test.SemanticsMatcher.expectValue(
                    androidx.compose.ui.semantics.SemanticsProperties.Role,
                    androidx.compose.ui.semantics.Role.Button
                )
        ).performClick()
        compose.onNodeWithText("ORIGIN FIXTURE").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, reads) }
    }

    @Test fun compatibilityCardDestinationsKeepTheirStageAndNeverSubmitAutomatically() {
        show("payment/add-card", restoreCard = true)
        compose.onNodeWithText("Nome igual ao cartão").assertExists()
        for ((route, heading) in listOf(
            "payment/card-preview" to "Confirme os dados do cartão escolhido",
            "payment/cvv" to "Insira o código de segurança do seu cartão de crédito",
            "payment/confirm-card" to "Confirme os dados digitados"
        )) {
            compose.runOnIdle { nav.navigate(route) }
            compose.onNodeWithText(heading).assertIsDisplayed()
            compose.runOnIdle {
                assertEquals(route, nav.currentDestination?.route)
                assertEquals(listOf("payment/card-preview", "payment/cvv", "payment/confirm-card").indexOf(route) + 1, reads)
            }
        }
    }

    private fun show(route: String, restoreCard: Boolean = false) {
        val dependencies = dependencies(restoreCard)
        compose.setContent {
            nav = rememberNavController()
            val state = rememberAppSessionState()
            val session = proxy<SessionNavigationActions> { name, _ ->
                when (name) {
                    "getState" -> state
                    "getIdentity" -> null
                    "getPrivateEntryKey" -> "visitor"
                    "isPrivateSessionCurrent", "getIsPrivateSessionCurrent" -> ({ true })
                    else -> error("Unexpected session action $name")
                }
            }
            val actions = AppNavigationActions(
                { _, _ -> error("Unexpected betting") },
                { error("Unexpected prize") },
                { error("Unexpected redirect") },
                { error("Unexpected ticket") },
                { error("Unexpected payment") },
                { nav.navigate("home") }
            )
            CaixaDSTheme {
                NavHost(nav, startDestination = "origin") {
                    composable("origin") { CaixaDSText("ORIGIN FIXTURE") }
                    composable("home") { CaixaDSText("HOME FIXTURE") }
                    composable("tickets/purchases") { CaixaDSText("PURCHASES FIXTURE") }
                    paymentGraph(dependencies, nav, session, actions)
                }
            }
        }
        compose.runOnIdle { nav.navigate(route) }
        compose.waitForIdle()
    }

    private fun dependencies(restoreCard: Boolean) = proxy<PaymentNavigationDependencies> { name, type ->
        when (name) {
            "getDispatchers" -> FixedCoroutineDispatchers(Dispatchers.Main.immediate)

            "getLocalClock" -> Clock.systemUTC()

            "getNetworkAvailability" -> NetworkAvailability { ConnectionAvailability.CONNECTED }

            "getMockBackendEnabled" -> false

            "getPaymentMethods" -> GetPaymentMethodsUseCase {
                reads++
                AppResult.Failure(AppError.Network)
            }

            "getSavedCards" -> GetSavedCardsUseCase {
                reads++
                AppResult.Success(
                    if (restoreCard) {
                        listOf(
                            br.gov.caixa.loterias.feature.payment.domain.model.SavedPaymentCard(
                                "fixture", 1, "411111", "1111", 12, 2030, 3, "VISA", "fixture"
                            )
                        )
                    } else {
                        emptyList()
                    }
                )
            }

            "getAuthenticatedSessionIdentity" -> proxy<AuthenticatedSessionIdentityProvider> {
                    _,
                    _,
                ->
                null
            }

            else -> {
                check(type.isInterface) { "Unconfigured dependency $name" }
                Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, _ ->
                    error("Unexpected payment/private read $name.${method.name}")
                }
            }
        }
    }
    private inline fun <reified T> proxy(crossinline answer: (String, Class<*>) -> Any?): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            answer(method.name, method.returnType)
        } as T
}
