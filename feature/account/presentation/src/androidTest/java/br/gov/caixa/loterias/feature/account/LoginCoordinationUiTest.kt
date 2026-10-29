package br.gov.caixa.loterias.feature.account

import androidx.compose.ui.test.*
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.core.app.ActivityOptionsCompat
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.BiometricAuthenticationGateway
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import br.gov.caixa.loterias.feature.account.presentation.route.LoginRoute
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

class LoginCoordinationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun automaticLaunchFailureIsNotRepeatedByRecompositionAndManualRetryKeepsDestination() {
        val authentication = Authentication().apply { failBrowserCreation.set(true) }
        val registry = SuccessfulRegistry()
        val destinations = CopyOnWriteArrayList<String>()
        val automatic = mutableStateOf(true)
        val version = mutableStateOf("first")
        val viewModel = LoginViewModel(authentication, verifyAccess = allowed())
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registry) {
                CaixaDSTheme(darkTheme = false) {
                    LoginRoute(viewModel, authentication, version.value, { destinations += it }, {}, {},
                        authorizeOnEnter = automatic.value)
                }
            }
        }
        compose.onNodeWithText("Não foi possível abrir o navegador para entrar. Tente novamente.").assertIsDisplayed()
        compose.runOnIdle { automatic.value = false; version.value = "second" }
        compose.waitForIdle()
        compose.runOnIdle { automatic.value = true }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, authentication.browserCreations.get())
            assertEquals(0, registry.launches.get())
            assertEquals(1, authentication.cancellations.get())
            authentication.failBrowserCreation.set(false)
        }
        compose.onNodeWithText("Faça login/cadastre-se").performClick()
        compose.waitUntil { destinations.size == 1 }
        compose.runOnIdle {
            assertEquals(listOf("support/terms-acceptance"), destinations)
            assertEquals(2, authentication.browserCreations.get())
            assertEquals(1, registry.launches.get())
            assertEquals(1, authentication.browserExchanges.get())
            assertFalse(viewModel.state.value.authorizationPending)
        }
    }

    @Test fun activePromptSurvivesRecompositionAndIsCancelledOnceWhenRouteLeavesComposition() {
        val authentication = Authentication(biometric = true)
        val viewModel = LoginViewModel(authentication, verifyAccess = allowed())
        val visible = mutableStateOf(true)
        val version = mutableStateOf("first")
        val prompts = AtomicInteger()
        val disposals = AtomicInteger()
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                if (visible.value) {
                    LoginRoute(viewModel, authentication, version.value, {}, {}, {}, authorizeOnEnter = true,
                        biometricPrompt = { _, _, _ ->
                            prompts.incrementAndGet()
                            val cancel: () -> Unit = { disposals.incrementAndGet() }
                            cancel
                        })
                } else Text("Fora do login")
            }
        }
        compose.waitUntil { prompts.get() == 1 }
        compose.runOnIdle { version.value = "second" }
        compose.waitForIdle()
        compose.runOnIdle {
            assertEquals(1, prompts.get())
            assertEquals(0, disposals.get())
            assertTrue(viewModel.state.value.authorizationPending)
            visible.value = false
        }
        compose.onNodeWithText("Fora do login").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(1, disposals.get())
            assertEquals(0, authentication.browserCreations.get())
            assertEquals(0, authentication.biometricExchanges.get())
        }
    }

    @Test fun promptExceptionFallsBackToBrowserExactlyOnceAndCompletesThroughAccountValidation() {
        val authentication = Authentication(biometric = true)
        val registry = SuccessfulRegistry()
        val prompts = AtomicInteger()
        val destinations = CopyOnWriteArrayList<String>()
        val viewModel = LoginViewModel(authentication, verifyAccess = allowed())
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides registry) {
                CaixaDSTheme(darkTheme = false) {
                    LoginRoute(viewModel, authentication, "test", { destinations += it }, {}, {},
                        authorizeOnEnter = true, biometricPrompt = { _, _, _ ->
                            prompts.incrementAndGet()
                            error("Synthetic prompt launch failure")
                        })
                }
            }
        }
        compose.waitUntil { destinations.size == 1 }
        compose.runOnIdle {
            assertEquals(1, prompts.get())
            assertEquals(1, authentication.cancellations.get())
            assertEquals(1, authentication.browserCreations.get())
            assertEquals(1, registry.launches.get())
            assertEquals(1, authentication.browserExchanges.get())
            assertEquals(0, authentication.biometricExchanges.get())
            assertEquals(listOf("support/terms-acceptance"), destinations)
            assertFalse(viewModel.state.value.browserFallbackPending)
        }
    }

    @Test fun enrollmentDialogPrecedesBlockedNoticeAndOnlyBlockedAcknowledgmentInvalidatesSession() {
        val authentication = Authentication(available = false)
        val invalidations = AtomicInteger()
        val viewModel = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase {
            PostLoginAccessResult.BlockedAccountCheckFailed
        })
        viewModel.setBiometricEnabled(true)
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", { error("Must not authenticate") }, {},
                    { invalidations.incrementAndGet() })
            }
        }
        compose.runOnIdle { viewModel.complete { authentication.completeAuthorization(null) } }
        compose.waitUntil { viewModel.state.value.blockedAccountCheckFailed }
        compose.onNodeWithText("Necessário habilitar biometria").assertIsDisplayed()
        compose.onNodeWithText("Não foi possível concluir esta operação. Por favor, tente novamente mais tarde.").assertDoesNotExist()
        compose.onNodeWithText("Cancelar").performClick()
        compose.onNodeWithText("Não foi possível concluir esta operação. Por favor, tente novamente mais tarde.").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, invalidations.get()) }
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle {
            assertEquals(1, invalidations.get())
            assertEquals(1, authentication.logouts.get())
            assertEquals(0, authentication.browserCreations.get())
            assertFalse(viewModel.state.value.blockedAccountCheckFailed)
        }
    }

    private fun allowed() = VerifyPostLoginAccessUseCase {
        PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.TERMS, false, BigDecimal.ZERO))
    }

    private class SuccessfulRegistry : ActivityResultRegistryOwner {
        val launches = AtomicInteger()
        override val activityResultRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(requestCode: Int, contract: ActivityResultContract<I, O>, input: I,
                options: ActivityOptionsCompat?,
            ) {
                launches.incrementAndGet()
                dispatchResult(requestCode, Activity.RESULT_OK, Intent("login.test.result"))
            }
        }
    }

    private class Authentication(biometric: Boolean = false, private val available: Boolean = true) :
        AuthenticationGateway,
        BiometricAuthenticationGateway {
        private var enabled = biometric
        val failBrowserCreation = AtomicBoolean()
        val browserCreations = AtomicInteger()
        val browserExchanges = AtomicInteger()
        val biometricExchanges = AtomicInteger()
        val cancellations = AtomicInteger()
        val logouts = AtomicInteger()
        override val biometricEnabled get() = enabled
        override val biometricAvailable get() = available
        override val biometricCredentialAvailable = true
        override fun setBiometricEnabled(enabled: Boolean) {
            this.enabled = enabled
        }
        override fun createBiometricAuthorizationIntent() = Intent("login.test.biometric")
        override suspend fun completeBiometricAuthorization(result: Intent?) {
            biometricExchanges.incrementAndGet()
        }
        override fun createAuthorizationIntent(): Intent {
            browserCreations.incrementAndGet()
            check(!failBrowserCreation.get())
            return Intent("login.test.browser")
        }
        override suspend fun completeAuthorization(result: Intent?) {
            browserExchanges.incrementAndGet()
        }
        override fun cancelAuthorization() {
            cancellations.incrementAndGet()
        }
        override fun logout() {
            logouts.incrementAndGet()
        }
    }
}
