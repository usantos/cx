package br.gov.caixa.loterias.feature.payment

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.network.ConnectionAvailability
import br.gov.caixa.loterias.core.common.network.NetworkAvailability
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistration
import br.gov.caixa.loterias.feature.payment.domain.model.MercadoPagoRegistrationResult
import br.gov.caixa.loterias.feature.payment.domain.usecase.PrepareMercadoPagoRegistrationUseCase
import br.gov.caixa.loterias.feature.payment.presentation.components.MercadoPagoRegistrationOverlay
import br.gov.caixa.loterias.feature.payment.presentation.viewmodel.MercadoPagoRegistrationViewModel
import java.time.Clock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class MercadoPagoOverlayUiTest {
    @get:Rule val compose = createComposeRule()
    private var finishes = 0
    private var calls = 0
    private val redirects = mutableListOf<ServiceRedirect>()

    @Test fun simulatedRegistrationRequiresAcknowledgmentAndFinishesOnce() {
        val model = show(AppResult.Success(MercadoPagoRegistrationResult.Ready(MercadoPagoRegistration.fromToken("fixture"))))
        compose.onNodeWithText("Simulação").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.onNodeWithText("Simulação").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(1, finishes)
            assertEquals(1, calls)
            assertEquals(false, model.browserReturned())
        }
    }

    @Test fun transportFailureStaysUntilAcknowledgmentWithoutLaunchingBrowser() {
        show(AppResult.Failure(AppError.Network))
        compose.onNodeWithText("Não foi possível iniciar o cadastro Mercado Pago. Verifique sua conexão.").assertIsDisplayed()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(1, finishes)
            assertEquals(emptyList<ServiceRedirect>(), redirects)
        }
    }

    @Test fun termsRedirectRequiresAcknowledgment() {
        show(AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.RedirectRequired(ServiceRedirect.ACCEPT_TERMS))))
        compose.runOnIdle { assertEquals(emptyList<ServiceRedirect>(), redirects) }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(listOf(ServiceRedirect.ACCEPT_TERMS), redirects)
            assertEquals(0, finishes)
        }
    }

    @Test fun directHomeRedirectIsConsumedExactlyOnce() {
        show(AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.RedirectRequired(ServiceRedirect.HOME))))
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(listOf(ServiceRedirect.HOME), redirects) }
    }

    @Test fun repeatRegistrationDeclineFinishesWithoutSecondRequest() {
        show(AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.RegistrationRequired("Repetir cadastro?"))))
        compose.onNodeWithText("Repetir cadastro?").assertIsDisplayed()
        compose.onNodeWithText("Não").performClick()
        compose.runOnIdle {
            assertEquals(1, calls)
            assertEquals(1, finishes)
        }
    }

    @Test fun repeatRegistrationAcceptMakesExactlyOneNewPreparation() {
        show(AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.RegistrationRequired("Repetir cadastro?"))))
        compose.onNodeWithText("Sim").performClick()
        compose.runOnIdle {
            assertEquals(2, calls)
            assertEquals(0, finishes)
        }
    }

    @Test fun unchangedResultFinishesWithoutUiAction() {
        show(AppResult.Success(MercadoPagoRegistrationResult.Rejected(ServiceFailure.Unchanged)))
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, finishes)
            assertEquals(1, calls)
        }
    }

    @Test fun loadingDialogDisappearsAfterDeferredFailure() {
        val response = CompletableDeferred<AppResult<MercadoPagoRegistrationResult>>()
        val model = model(
            PrepareMercadoPagoRegistrationUseCase {
                calls++
                response.await()
            }
        )
        compose.setContent { CaixaDSTheme { MercadoPagoRegistrationOverlay(model, true, redirects::add, { finishes++ }) } }
        compose.runOnIdle { model.start("fixture@example.com") }
        compose.onNodeWithText("Carregando…").assertExists()
        compose.runOnIdle { response.complete(AppResult.Failure(AppError.Network)) }
        compose.onNodeWithText("Carregando…").assertDoesNotExist()
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(1, finishes) }
    }

    private fun show(result: AppResult<MercadoPagoRegistrationResult>): MercadoPagoRegistrationViewModel {
        val model = model(
            PrepareMercadoPagoRegistrationUseCase {
                calls++
                result
            }
        )
        compose.setContent { CaixaDSTheme { MercadoPagoRegistrationOverlay(model, true, redirects::add, { finishes++ }) } }
        compose.runOnIdle { model.start("fixture@example.com") }
        compose.waitForIdle()
        return model
    }

    private fun model(prepare: PrepareMercadoPagoRegistrationUseCase) = MercadoPagoRegistrationViewModel(
        GetAccountProfileUseCase { error("Explicit email must skip profile reads") },
        prepare,
        FixedCoroutineDispatchers(Dispatchers.Main.immediate),
        Clock.systemUTC(),
        NetworkAvailability { ConnectionAvailability.CONNECTED }
    )
}
