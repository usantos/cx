package br.gov.caixa.loterias.feature.account

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.app.ActivityOptionsCompat
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.BiometricAuthenticationGateway
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AccountStatus
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.route.LoginRoute
import br.gov.caixa.loterias.feature.account.presentation.screen.AccountTokenScreen
import br.gov.caixa.loterias.feature.account.presentation.screen.LoginScreen
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AuthenticationReturnScreenTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun cancelledAutomaticBiometricLoginAttemptsBrowserBeforeReturningToOrigin() =
        verifyCancelledAutomaticLogin(biometric = true, cancellationIntent = null)

    @Test
    fun appAuthBrowserCancellationIntentNotifiesOriginAndClearsPendingAttempt() = verifyCancelledAutomaticLogin(
        biometric = false,
        cancellationIntent = net.openid.appauth.AuthorizationException.GeneralErrors.USER_CANCELED_AUTH_FLOW.toIntent()
    )

    @Test fun revokedBiometricRefreshAttemptsBrowserBeforeReturningToOrigin() =
        verifyCancelledAutomaticLogin(biometric = true, cancellationIntent = null, refreshFailure = true)

    private fun verifyCancelledAutomaticLogin(biometric: Boolean, cancellationIntent: Intent?, refreshFailure: Boolean = false) {
        val cancellations = AtomicInteger()
        val returnsToOrigin = AtomicInteger()
        val refreshes = AtomicInteger()
        val launches = AtomicInteger()
        val authentication = object : AuthenticationGateway, BiometricAuthenticationGateway {
            override val biometricEnabled = biometric
            override val biometricAvailable = true
            override val biometricCredentialAvailable = true
            override fun setBiometricEnabled(enabled: Boolean) = Unit
            override fun createBiometricAuthorizationIntent(): Intent = Intent()
            override suspend fun completeBiometricAuthorization(result: Intent?) {
                refreshes.incrementAndGet()
                check(!refreshFailure) { "synthetic revoked refresh" }
            }
            override fun createAuthorizationIntent(): Intent = Intent()
            override suspend fun completeAuthorization(result: Intent?) = error("Cancelled prompt must not authenticate")
            override fun cancelAuthorization() {
                cancellations.incrementAndGet()
            }
            override fun logout() = Unit
        }
        val registryOwner = object : ActivityResultRegistryOwner {
            override val activityResultRegistry = object : ActivityResultRegistry() {
                override fun <I, O> onLaunch(
                    requestCode: Int,
                    contract: ActivityResultContract<I, O>,
                    input: I,
                    options: ActivityOptionsCompat?,
                ) {
                    if (biometric) assertEquals(0, returnsToOrigin.get())
                    launches.incrementAndGet()
                    dispatchResult(requestCode, Activity.RESULT_CANCELED, cancellationIntent)
                }
            }
        }
        val viewModel = LoginViewModel(
            authentication,
            verifyAccess = VerifyPostLoginAccessUseCase {
                error("Cancelled prompt must not verify account access")
            }
        )
        composeRule.setContent {
            CompositionLocalProvider(
                LocalActivityResultRegistryOwner provides registryOwner
            ) {
                CaixaDSTheme(darkTheme = false) {
                    LoginRoute(
                        viewModel, authentication, "test", { error("Must not navigate authenticated") }, {}, {},
                        authorizeOnEnter = true, onAuthorizationCancelled = { returnsToOrigin.incrementAndGet() },
                        biometricPrompt = { _, _, onResult ->
                            launches.incrementAndGet()
                            onResult(if (refreshFailure) Intent() else null)
                            val cancel: () -> Unit = {}
                            cancel
                        }
                    )
                }
            }
        }
        composeRule.waitUntil { launches.get() == (if (biometric) 2 else 1) && !viewModel.state.value.authorizationPending }
        assertEquals(if (biometric) 2 else 1, cancellations.get())
        assertEquals(1, returnsToOrigin.get())
        assertEquals(if (refreshFailure) 1 else 0, refreshes.get())
        assertEquals(false, viewModel.state.value.authenticated)
    }

    @Test fun disablingBiometricAsksConfirmationAndNoPreservesRememberedLogin() {
        val authentication = PreferenceAuthentication(enabled = true, available = true)
        val vm = preferenceRoute(authentication)
        composeRule.onNodeWithText("Acessar com biometria").performClick()
        composeRule.onNodeWithText("Desativar biometria").assertIsDisplayed()
        assertEquals(true, authentication.biometricEnabled)
        composeRule.onNodeWithText("Não").performClick()
        assertEquals(true, authentication.biometricEnabled)
        assertEquals(true, vm.state.value.biometricEnabled)
        assertEquals(true, authentication.biometricCredentialAvailable)
        composeRule.onNodeWithText("Acessar com biometria").performClick()
        composeRule.onNodeWithText("Sim").performClick()
        assertEquals(false, authentication.biometricEnabled)
        assertEquals(false, authentication.biometricCredentialAvailable)
        assertEquals(false, vm.state.value.biometricEnabled)
    }

    @Test fun enablingWithoutEnrollmentShowsDialogAndCancelPreservesDisabledPreference() {
        val authentication = PreferenceAuthentication(enabled = false, available = false)
        val vm = preferenceRoute(authentication)
        composeRule.onNodeWithText("Acessar com biometria").performClick()
        composeRule.onNodeWithText("Necessário habilitar biometria").assertIsDisplayed()
        composeRule.onNodeWithText("Cancelar").performClick()
        assertEquals(false, authentication.biometricEnabled)
        assertEquals(false, vm.state.value.biometricEnabled)
    }

    @Test fun enrollmentPositiveActionLaunchesSettingsOnceAndWaitsForEnrollment() {
        val launches = AtomicInteger()
        val authentication = PreferenceAuthentication(enabled = false, available = false)
        val vm = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase { error("Must not verify") })
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(vm, authentication, "test", {}, {}, {}, biometricEnrollment = { launches.incrementAndGet() })
            }
        }
        composeRule.onNodeWithText("Acessar com biometria").performClick()
        composeRule.onNodeWithText("Habilitar").performClick()
        composeRule.onNodeWithText("Necessário habilitar biometria").assertDoesNotExist()
        assertEquals(1, launches.get())
        assertEquals(false, authentication.biometricEnabled)
        assertEquals(false, vm.state.value.biometricEnabled)
    }

    private fun preferenceRoute(authentication: PreferenceAuthentication): LoginViewModel {
        val vm = LoginViewModel(authentication, verifyAccess = VerifyPostLoginAccessUseCase { error("Preference must not log in") })
        composeRule.setContent { CaixaDSTheme(darkTheme = false) { LoginRoute(vm, authentication, "test", {}, {}, {}) } }
        return vm
    }

    @Test fun absentHardwareHidesBiometricRowWhileNoEnrollmentKeepsIt() {
        preferenceRoute(PreferenceAuthentication(enabled = false, available = false, hardware = false))
        composeRule.onNodeWithText("Acessar com biometria").assertDoesNotExist()
        composeRule.onNodeWithText("Faça login/cadastre-se").assertIsDisplayed()
    }

    private class PreferenceAuthentication(
        private var enabled: Boolean,
        private val available: Boolean,
        private val hardware: Boolean = true,
    ) : AuthenticationGateway,
        BiometricAuthenticationGateway {
        override val biometricEnabled get() = enabled
        override val biometricAvailable get() = available
        override val biometricHardwareAvailable get() = hardware
        override val biometricCredentialAvailable get() = enabled
        override fun setBiometricEnabled(enabled: Boolean) {
            check(!enabled || available)
            this.enabled = enabled
        }
        override fun createBiometricAuthorizationIntent(): Intent = error("not used")
        override suspend fun completeBiometricAuthorization(result: Intent?) = error("not used")
        override fun createAuthorizationIntent(): Intent = error("not used")
        override suspend fun completeAuthorization(result: Intent?) = error("not used")
        override fun cancelAuthorization() = Unit
        override fun logout() = Unit
    }

    @Test
    fun trialAfterFailedAccountValidationClearsCredentialBeforeReturningToHome() {
        val activeCredential = AtomicBoolean(false)
        val trialNavigation = AtomicInteger()
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("not used")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) {
                activeCredential.set(true)
            }
            override fun logout() {
                activeCredential.set(false)
            }
        }
        val viewModel = LoginViewModel(
            authentication,
            verifyAccess = VerifyPostLoginAccessUseCase {
                PostLoginAccessResult.AccountCheckFailed(AppError.Network)
            }
        )
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", { error("Unverified access must not authenticate") }, {
                    assertEquals(false, activeCredential.get())
                    trialNavigation.incrementAndGet()
                }, {})
            }
        }
        composeRule.runOnIdle { viewModel.complete { authentication.completeAuthorization(null) } }
        composeRule.waitUntil { viewModel.state.value.errorMessage != null }
        assertEquals(false, activeCredential.get())
        composeRule.onNodeWithText("Experimente").performClick()
        assertEquals(1, trialNavigation.get())
        assertEquals(false, activeCredential.get())
        assertEquals(false, viewModel.state.value.authenticated)
    }

    @Test
    fun failedTrialCredentialCleanupKeepsLoginScreenAndAllowsRetry() {
        val cleanupFails = AtomicBoolean(true)
        val trialNavigation = AtomicInteger()
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("not used")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) = Unit
            override fun logout() {
                check(!cleanupFails.get())
            }
        }
        val viewModel = LoginViewModel(
            authentication,
            verifyAccess = VerifyPostLoginAccessUseCase {
                error("Trial entry must not verify account")
            }
        )
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
        composeRule.runOnIdle {
            state.value = LoginUiState(isLoading = true)
            Snapshot.sendApplyNotifications()
        }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeBy(900)
        composeRule.onNodeWithText("Experimente").assertDoesNotExist()
        composeRule.onNodeWithText("Entrar").assertDoesNotExist()
        composeRule.onNodeWithText("Carregando…").assertIsDisplayed()
        composeRule.onNodeWithText("Aguarde").assertDoesNotExist()
        composeRule.runOnIdle {
            state.value = LoginUiState(errorMessage = "Falha de validação")
            Snapshot.sendApplyNotifications()
        }
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
            override fun logout() {
                logouts.incrementAndGet()
            }
        }
        val viewModel = LoginViewModel(
            authentication,
            verifyAccess = VerifyPostLoginAccessUseCase {
                checks.incrementAndGet()
                PostLoginAccessResult.BlockedAccountCheckFailed
            }
        )
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", {
                    navigation.incrementAndGet()
                }, { navigation.incrementAndGet() }, { invalidations.incrementAndGet() })
            }
        }
        composeRule.runOnIdle { viewModel.complete { authentication.completeAuthorization(null) } }
        composeRule.waitUntil { viewModel.state.value.blockedAccountCheckFailed }
        composeRule.onNodeWithText("Atenção").assertIsDisplayed()
        composeRule.onNodeWithText("Não foi possível concluir esta operação. Por favor, tente novamente mais tarde.").assertIsDisplayed()
        composeRule.onNodeWithText("Entendi").performClick()
        composeRule.onNodeWithText("Entendi").assertDoesNotExist()
        assertEquals(1, logouts.get())
        assertEquals(1, checks.get())
        assertEquals(0, navigation.get())
        assertEquals(1, invalidations.get()) // Delega ao hospedeiro compartilhado de logout, sem passar pela navegação autenticada.
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
            override fun logout() {
                logouts.incrementAndGet()
            }
        }
        val viewModel = LoginViewModel(
            authentication,
            verifyAccess = VerifyPostLoginAccessUseCase {
                checks.incrementAndGet()
                PostLoginAccessResult.Verified(
                    AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN, AccountStatus.PARTIALLY_BLOCKED)
                )
            }
        )
        composeRule.setContent {
            CaixaDSTheme(darkTheme = false) {
                LoginRoute(viewModel, authentication, "test", {
                    assertEquals("home", it)
                    navigation.incrementAndGet()
                }, { error("Must not start trial") }, { error("Consultation must not end the session") })
            }
        }
        composeRule.runOnIdle { viewModel.complete { authentication.completeAuthorization(null) } }
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
