package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.test.*
import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedUser
import br.gov.caixa.loterias.feature.account.domain.port.CurrentUserIdentityProvider
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile
import br.gov.caixa.loterias.feature.account.domain.model.Gender
import br.gov.caixa.loterias.feature.account.domain.usecase.GetAccountProfileUseCase
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.usecase.RegisterAccountUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.ResolveZipCodeUseCase
import br.gov.caixa.loterias.feature.account.domain.usecase.UpdatePersonalDataUseCase
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation
import java.io.File
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.account.presentation.route.PersonalDataRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.PersonalDataViewModel
import br.gov.caixa.loterias.feature.account.presentation.route.RegistrationRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.RegistrationViewModel

/** Rotas completas, apenas identidade sintética. As capturas são candidatas, nunca uma declaração de paridade com a referência. */
class AccountRemainingParityCaptureTest {
    @get:Rule val compose = createComposeRule()
    private val writes = AtomicInteger()

    @Test fun personalCompleteFormFocusLimitsNotificationsAndFailureWithoutSaving() {
        val model = PersonalDataViewModel(
            getProfile = GetAccountProfileUseCase { AppResult.Success(profile()) },
            resolveZipCode = ResolveZipCodeUseCase { AppResult.Failure(AppError.Network) },
            updatePersonalData = UpdatePersonalDataUseCase { _, _ -> writes.incrementAndGet(); error("No saving allowed") },
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            PersonalDataRoute(model, {}, { error("No completion allowed") })
        } }
        compose.waitUntil { !model.state.value.loading }
        compose.onNodeWithText("CPF").assertIsDisplayed()
        capture("personal-top")
        compose.onNode(hasSetTextAction() and hasText("00000-000")).performScrollTo().performClick().assertIsFocused()
        capture("personal-cep-focus-ime")
        compose.onNode(hasSetTextAction() and hasText("R$ 100,00")).performScrollTo().performClick().assertIsFocused()
        capture("personal-limit-focus-ime")
        compose.onNode(hasSetTextAction() and hasText("R$ 100,00")).performTextReplacement("9,99")
        compose.onNodeWithText("O valor mínimo é R$ 10,00").performScrollTo().assertIsDisplayed()
        capture("personal-limit-minimum-ime")
        compose.onNode(hasSetTextAction() and hasText("R$ 9,99")).performTextReplacement("200,01")
        compose.onNodeWithText("O valor máximo é R$ 200,00").performScrollTo().assertIsDisplayed()
        capture("personal-limit-maximum-ime")
        compose.onNode(hasSetTextAction() and hasText("R$ 200,01")).performTextReplacement("150,00")
        compose.onNodeWithText("R$ 10,00 até R$ 200,00").performScrollTo().assertIsDisplayed()
        InstrumentationRegistry.getInstrumentation().uiAutomation.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
        compose.waitForIdle()
        compose.onNodeWithText("Aceito receber notificações de Loterias.").performScrollTo()
        capture("personal-bottom-valid")
        compose.onNodeWithText("Aceito receber notificações de Loterias.").performClick()
        compose.onNodeWithText("Entendi").assertIsDisplayed()
        capture("personal-notifications-dialog")
        compose.onNodeWithText("Entendi").performTouchInput { click(center) }
        compose.runOnIdle { model.changeZipCode("00000001") }
        compose.waitUntil { !model.state.value.loading && model.state.value.errorMessage != null }
        compose.onNodeWithText("Entendi").assertIsDisplayed()
        capture("personal-cep-failure")
        compose.runOnIdle { assertEquals(0, writes.get()); assertEquals(false, model.state.value.saved) }
    }

    @Test fun registrationCompleteFormConsentFocusAndLookupFailureWithoutRegistering() {
        val model = RegistrationViewModel(
            identityProvider = CurrentUserIdentityProvider { AuthenticatedUser("Pessoa de Teste", "00000000000", "teste@example.invalid", "19900101") },
            resolveZipCode = ResolveZipCodeUseCase { value ->
                if (value == "00000000") AppResult.Success(ZipCodeLocation(MunicipalityId(1, 1, 1), "DF", "Município de Teste"))
                else AppResult.Failure(AppError.Network)
            },
            registerAccount = RegisterAccountUseCase { writes.incrementAndGet(); error("No registration allowed") },
        )
        compose.setContent { CaixaDSTheme(darkTheme = false) {
            RegistrationRoute(model, {}, { error("No completion allowed") }, { error("No external terms navigation allowed") })
        } }
        compose.onNodeWithText("CPF").assertIsDisplayed()
        capture("registration-top")
        compose.onNode(hasSetTextAction() and isEnabled()).performScrollTo().performClick().assertIsFocused().performTextReplacement("00000000")
        compose.waitUntil { !model.state.value.loading && model.state.value.city.isNotBlank() }
        capture("registration-cep-focus-ime")
        InstrumentationRegistry.getInstrumentation().uiAutomation.performGlobalAction(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
        compose.waitForIdle()
        compose.onNodeWithText("Aceito receber notificações de Loterias.").performScrollTo()
        compose.onNodeWithText("Finalizar cadastro").assertIsNotEnabled()
        capture("registration-bottom-unchecked")
        compose.runOnIdle { model.toggleTerms() }
        compose.onNodeWithText("Finalizar cadastro").assertIsEnabled()
        capture("registration-bottom-checked")
        compose.runOnIdle { model.changeZipCode("00000001") }
        compose.waitUntil { !model.state.value.loading && model.state.value.errorMessage != null }
        compose.onNodeWithText("Não foi possível conectar. Verifique sua internet e tente novamente.").performScrollTo().assertIsDisplayed()
        capture("registration-cep-failure")
        compose.runOnIdle { assertEquals(0, writes.get()); assertEquals(false, model.state.value.registered) }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val target = instrumentation.targetContext
        val directory = File(requireNotNull(target.externalCacheDir), "remaining-account-parity").apply { mkdirs() }
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        File(directory, "$name.png").outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        bitmap.recycle()
        File(directory, "$name.txt").writeText("Synthetic fixture; no requests to production.\nDensity=${target.resources.displayMetrics.densityDpi}\nfontScale=${target.resources.configuration.fontScale}\nIME screenshots require physical same-state comparison.\n")
    }

    private fun profile() = AccountProfile("00000000000", "Pessoa de Teste", "teste@example.invalid", "00000000",
        Gender.MALE, MunicipalityId(1, 1, 1), "DF", "Município de Teste", BigDecimal("100"), BigDecimal("10"), BigDecimal("200"), false)
}
