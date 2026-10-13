package br.gov.caixa.loterias.feature.account

import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.domain.account.PostLoginAccessResult
import br.gov.caixa.loterias.core.domain.account.AccountAccess
import br.gov.caixa.loterias.core.domain.account.AccountStatus
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.math.BigDecimal

class AuthenticationReturnScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun cancelledAutomaticBiometricLoginNotifiesOriginAndClearsPendingAttempt() =
        verifyCancelledAutomaticLogin(biometric = true, cancellationIntent = null)

    @Test
    fun appAuthBrowserCancellationIntentNotifiesOriginAndClearsPendingAttempt() =
        verifyCancelledAutomaticLogin(biometric = false,
            cancellationIntent = net.openid.appauth.AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.toIntent())

    private fun verifyCancelledAutomaticLogin(biometric: Boolean, cancellationIntent: Intent?) {
        val cancellations = AtomicInteger()
        val returnsToOrigin = AtomicInteger()
        val refreshes = AtomicInteger()
        val launches = AtomicInteger()
        val authentication = object : AuthenticationGateway, br.gov.caixa.loterias.core.auth.BiometricAuthenticationGateway {
            override val biometricEnabled = biometric
            override val biometricAvailable = true
            override val biometricCredentialAvailable = true
            override fun setBiometricEnabled(enabled: Boolean) = Unit
            override fun createBiometricAuthorizationIntent(): Intent = Intent()
            override suspend fun completeBiometricAuthorization(result: Intent?) { refreshes.incrementAndGet() }
            override fun createAuthorizationIntent(): Intent { check(!biometric); return Intent() }
            override suspend fun completeAuthorization(result: Intent?) = error("Cancelled prompt must not authenticate")
            override fun cancelAuthorization() { cancellations.incrementAndGet() }
            override fun logout() = Unit
        }
        val registryOwner = object : androidx.activity.result.ActivityResultRegistryOwner {
            override val activityResultRegistry = object : androidx.activity.result.ActivityResultRegistry() {
                override fun <I, O> onLaunch(requestCode: Int,
                    contract: androidx.activity.result.contract.ActivityResultContract<I, O>, input: I,
                    options: androidx.core.app.ActivityOptionsCompat?) {
                    launches.incrementAndGet()
                    dispatchResult(requestCode, android.app.Activity.RESULT_CANCELED, cancellationIntent)
                }
            }
        }
        val viewModel = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase {
            error("Cancelled prompt must not verify account access")
        })
        composeRule.setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.activity.compose.LocalActivityResultRegistryOwner provides registryOwner,
            ) {
                CaixaDSTheme(darkTheme = false) {
                    LoginRoute(viewModel, authentication, "test", { error("Must not navigate authenticated") }, {}, {},
                        authorizeOnEnter = true, onAuthorizationCancelled = { returnsToOrigin.incrementAndGet() },
                        biometricPrompt = { _, _, onResult ->
                            launches.incrementAndGet()
                            onResult(null)
                            val cancel: () -> Unit = {}
                            cancel
                        })
                }
            }
        }
        composeRule.waitUntil { launches.get() == 1 && !viewModel.state.value.authorizationPending }
        assertEquals(1, cancellations.get())
        assertEquals(1, returnsToOrigin.get())
        assertEquals(0, refreshes.get())
        assertEquals(false, viewModel.state.value.authenticated)
    }

    @Test
    fun trialAfterFailedAccountValidationClearsCredentialBeforeReturningToHome() {
        val activeCredential = java.util.concurrent.atomic.AtomicBoolean(false)
        val trialNavigation = AtomicInteger()
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("not used")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) { activeCredential.set(true) }
            override fun logout() { activeCredential.set(false) }
        }
        val viewModel = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase {
            PostLoginAccessResult.AccountCheckFailed(br.gov.caixa.loterias.core.common.AppError.Network)
        })
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", { error("Unverified access must not authenticate") }, {
                    assertEquals(false, activeCredential.get())
                    trialNavigation.incrementAndGet()
                }, {})
            }
        }
        composeRule.runOnIdle { viewModel.complete(null) }
        composeRule.waitUntil { viewModel.state.value.errorMessage != null }
        assertEquals(false, activeCredential.get())
        composeRule.onNodeWithText("Experimente").performClick()
        assertEquals(1, trialNavigation.get())
        assertEquals(false, activeCredential.get())
        assertEquals(false, viewModel.state.value.authenticated)
    }

    @Test
    fun failedTrialCredentialCleanupKeepsLoginScreenAndAllowsRetry() {
        val cleanupFails = java.util.concurrent.atomic.AtomicBoolean(true)
        val trialNavigation = AtomicInteger()
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("not used")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) = Unit
            override fun logout() { check(!cleanupFails.get()) }
        }
        val viewModel = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase {
            error("Trial entry must not verify account")
        })
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", {}, { trialNavigation.incrementAndGet() }, {})
            }
        }
        composeRule.onNodeWithText("Experimente").performClick()
        composeRule.onNodeWithText("Não foi possível encerrar a sessão. Tente novamente.").assertIsDisplayed()
        assertEquals(0, trialNavigation.get())
        cleanupFails.set(false)
        composeRule.onNodeWithText("Experimente").performClick()
        assertEquals(1, trialNavigation.get())
    }

    @Test
    fun tokenExchangeReplacesLoginContentAndFailureRestoresItWithoutNewActions() {
        val state = mutableStateOf(LoginUiState())
        var loginActions = 0
        var trialActions = 0
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginScreen(state.value, "test", { trialActions++ }, { loginActions++ }, {})
            }
        }
        composeRule.onNodeWithText("Experimente").assertIsDisplayed()
        composeRule.runOnIdle { state.value = LoginUiState(isLoading = true) }
        composeRule.mainClock.advanceTimeBy(900)
        composeRule.onNodeWithText("Experimente").assertDoesNotExist()
        composeRule.onNodeWithText("Entrar").assertDoesNotExist()
        composeRule.onNodeWithText("Carregando…").assertIsDisplayed()
        composeRule.onNodeWithText("Aguarde").assertDoesNotExist()
        composeRule.runOnIdle { state.value = LoginUiState(errorMessage = "Falha de validação") }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.onNodeWithText("Carregando…").assertDoesNotExist()
        composeRule.onNodeWithText("Experimente").assertIsDisplayed()
        composeRule.onNodeWithText("Falha de validação").assertIsDisplayed()
        assertEquals(0, loginActions)
        assertEquals(0, trialActions)
    }

    @Test
    fun pendingBrowserDoesNotPretendTokenExchangeHasStarted() {
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginScreen(LoginUiState(authorizationPending = true), "test", {}, {}, {})
            }
        }
        composeRule.onNodeWithText("Experimente").assertIsDisplayed()
        composeRule.onNodeWithText("Carregando…").assertDoesNotExist()
    }

    @Test
    fun catalogUsesTheSameTokenLayoutAndLiteralLabel() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { CaixaDSTheme(darkTheme = false) { AccountTokenScreen() } }
        composeRule.mainClock.advanceTimeBy(900)
        composeRule.onNodeWithText("Carregando…").assertIsDisplayed()
        composeRule.onNodeWithText("Aguarde").assertDoesNotExist()
        composeRule.onNodeWithText("Experimente").assertDoesNotExist()
    }

    @Test
    fun blockedCheckFailureShowsLegacyNoticeAndAcknowledgmentDoesNotRestartAuthorization() {
        val logouts = AtomicInteger()
        val checks = AtomicInteger()
        val navigation = AtomicInteger()
        val invalidations = AtomicInteger()
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("Must not launch browser")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) = Unit
            override fun logout() { logouts.incrementAndGet() }
        }
        val viewModel = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase {
            checks.incrementAndGet()
            PostLoginAccessResult.BlockedAccountCheckFailed
        })
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", { navigation.incrementAndGet() }, { navigation.incrementAndGet() }, { invalidations.incrementAndGet() })
            }
        }
        composeRule.runOnIdle { viewModel.complete(null) }
        composeRule.waitUntil { viewModel.state.value.blockedAccountCheckFailed }
        composeRule.onNodeWithText("Atenção").assertIsDisplayed()
        composeRule.onNodeWithText("Não foi possível concluir esta operação. Por favor, tente novamente mais tarde.").assertIsDisplayed()
        composeRule.onNodeWithText("Entendi").performClick()
        composeRule.onNodeWithText("Entendi").assertDoesNotExist()
        assertEquals(1, logouts.get())
        assertEquals(1, checks.get())
        assertEquals(0, navigation.get())
        assertEquals(1, invalidations.get()) // Delegates to the shared logout host, not authenticated navigation.
    }

    @Test
    fun partialBlockingKeepsTokenSurfaceAndOnlyContinuesAfterAcknowledgmentWithoutLogout() {
        val logouts = AtomicInteger()
        val navigation = AtomicInteger()
        val checks = AtomicInteger()
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("Must not launch browser")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) = Unit
            override fun logout() { logouts.incrementAndGet() }
        }
        val viewModel = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase {
            checks.incrementAndGet()
            PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN, AccountStatus.PARTIALLY_BLOCKED))
        })
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", {
                    assertEquals("home", it)
                    navigation.incrementAndGet()
                }, { error("Must not start trial") }, { error("Consultation must not end the session") })
            }
        }
        composeRule.runOnIdle { viewModel.complete(null) }
        composeRule.waitUntil { viewModel.state.value.consultationOnlyNoticePending }
        composeRule.onNodeWithText("Usuário habilitado apenas para consultas.").assertIsDisplayed()
        composeRule.onNodeWithText("Experimente").assertDoesNotExist()
        assertEquals(0, navigation.get())
        composeRule.onNodeWithText("Entendi").performClick()
        composeRule.waitUntil { navigation.get() == 1 }
        composeRule.onNodeWithText("Usuário habilitado apenas para consultas.").assertDoesNotExist()
        composeRule.runOnIdle { viewModel.setBiometricEnabled(true) }
        assertEquals(0, logouts.get())
        assertEquals(1, checks.get())
        assertEquals(1, navigation.get())
    }
}
