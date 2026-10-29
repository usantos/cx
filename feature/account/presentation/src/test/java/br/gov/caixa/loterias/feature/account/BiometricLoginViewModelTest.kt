package br.gov.caixa.loterias.feature.account

import org.junit.Assert.*
import android.content.Intent
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.gateway.BiometricAuthenticationGateway
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AccountStatus
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

class BiometricLoginViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()
    @Test fun `remembered login still blocks until backend validation and partial notice acknowledgment`() = runTest(dispatcherRule.dispatcher) {
        var checks = 0
        val auth = FakeBiometricAuthentication()
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase {
            checks++
            PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN, AccountStatus.PARTIALLY_BLOCKED))
        })
        assertTrue(vm.state.value.biometricEnabled)
        assertTrue(vm.canUseBiometricLogin())
        vm.completeBiometric { auth.completeBiometricAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, checks)
        assertEquals(1, auth.refreshes)
        assertFalse(vm.state.value.authenticated)
        assertTrue(vm.state.value.consultationOnlyNoticePending)
        assertTrue(vm.acknowledgeConsultationOnlyNotice())
        assertTrue(vm.state.value.authenticated)
    }
    @Test fun `failed refresh does not verify or authenticate`() = runTest(dispatcherRule.dispatcher) {
        var checks = 0
        val auth = FakeBiometricAuthentication().apply { refreshFailure = true }
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase {
            checks++
            PostLoginAccessResult.BlockedAccountCheckFailed
        })
        vm.completeBiometric { auth.completeBiometricAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, checks)
        assertFalse(vm.state.value.authenticated)
        assertNull(vm.state.value.errorMessage)
        assertTrue(vm.consumeBrowserFallback())
        assertFalse(vm.consumeBrowserFallback())
        assertTrue(vm.beginAuthorization())
    }
    @Test fun `disable waits for confirmation and preserves credential until confirmed`() {
        val auth = FakeBiometricAuthentication()
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { PostLoginAccessResult.BlockedAccountCheckFailed })
        vm.setBiometricEnabled(false)
        assertTrue(vm.state.value.biometricEnabled)
        assertTrue(auth.biometricEnabled)
    }
    @Test fun `unavailable biometric preference offers enrollment without an error`() {
        val auth = FakeBiometricAuthentication().apply { setBiometricEnabled(false); biometricAvailable = false }
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { PostLoginAccessResult.BlockedAccountCheckFailed })
        vm.setBiometricEnabled(true)
        assertFalse(vm.state.value.biometricEnabled)
        assertNull(vm.state.value.errorMessage)
    }
    @Test fun `cancelled prompt emits exactly one SSO fallback without authentication`() {
        val auth = FakeBiometricAuthentication()
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { error("Must not verify") })
        assertTrue(vm.beginAuthorization())
        vm.biometricAuthorizationFailed()
        assertFalse(vm.state.value.authorizationPending)
        assertFalse(vm.state.value.authenticated)
        assertTrue(vm.consumeBrowserFallback())
        assertFalse(vm.consumeBrowserFallback())
    }
    @Test fun `confirmation No preserves biometric preference and Yes clears it once`() {
        val auth = FakeBiometricAuthentication()
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { error("Must not verify") })
        vm.confirmBiometricDisable(true)
        assertTrue(auth.biometricEnabled)
        vm.setBiometricEnabled(false)
        vm.confirmBiometricDisable(false)
        assertTrue(auth.biometricEnabled)
        vm.setBiometricEnabled(false)
        vm.confirmBiometricDisable(true)
        assertFalse(auth.biometricEnabled)
        assertFalse(vm.state.value.biometricEnabled)
        vm.confirmBiometricDisable(true)
        assertFalse(vm.state.value.biometricDisablePending)
    }
    @Test fun `enrollment cancellation keeps disabled preference and then allows enabling`() {
        val auth = FakeBiometricAuthentication().apply { setBiometricEnabled(false); biometricAvailable = false }
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { error("Must not verify") })
        vm.setBiometricEnabled(true)
        assertTrue(vm.state.value.biometricEnrollmentPending)
        vm.setBiometricEnabled(false)
        assertTrue(vm.state.value.biometricEnrollmentPending)
        vm.dismissBiometricEnrollment()
        assertFalse(vm.state.value.biometricEnabled)
        auth.biometricAvailable = true
        vm.setBiometricEnabled(true)
        assertTrue(vm.state.value.biometricEnabled)
    }
    @Test fun `requested enabling resumes only after Settings enrollment succeeds`() {
        val auth = FakeBiometricAuthentication().apply { setBiometricEnabled(false); biometricAvailable = false }
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { error("Must not verify") })
        vm.setBiometricEnabled(true)
        vm.startBiometricEnrollment()
        vm.refreshBiometricAvailability()
        assertFalse(vm.state.value.biometricEnabled)
        auth.biometricAvailable = true
        vm.refreshBiometricAvailability()
        assertTrue(vm.state.value.biometricEnabled)
        assertTrue(auth.biometricEnabled)
    }

    @Test fun `remembered name is shown without granting authenticated access and disappears after disable`() {
        val auth = FakeBiometricAuthentication()
        val vm =
            LoginViewModel(
                auth,
                FixedCoroutineDispatchers(dispatcherRule.dispatcher),
                VerifyPostLoginAccessUseCase {
                    error("Must not verify")
                }
            )
        assertEquals("João", vm.state.value.rememberedFirstName)
        assertTrue(vm.state.value.rememberedLoginAvailable)
        assertFalse(vm.state.value.authenticated)
        vm.setBiometricEnabled(false)
        vm.confirmBiometricDisable(true)
        assertNull(vm.state.value.rememberedFirstName)
        assertFalse(vm.state.value.rememberedLoginAvailable)
    }

    @Test fun `resume removes remembered identity when credential or biometric is unavailable`() {
        val auth = FakeBiometricAuthentication()
        val vm =
            LoginViewModel(
                auth,
                FixedCoroutineDispatchers(dispatcherRule.dispatcher),
                VerifyPostLoginAccessUseCase {
                    error("Must not verify")
                }
            )
        auth.biometricCredentialAvailable = false
        vm.refreshBiometricAvailability()
        assertNull(vm.state.value.rememberedFirstName)
        assertFalse(vm.state.value.rememberedLoginAvailable)
        auth.biometricCredentialAvailable = true
        vm.refreshBiometricAvailability()
        assertEquals("João", vm.state.value.rememberedFirstName)
        auth.biometricAvailable = false
        vm.refreshBiometricAvailability()
        assertNull(vm.state.value.rememberedFirstName)
    }
    private class FakeBiometricAuthentication :
        AuthenticationGateway,
        BiometricAuthenticationGateway {
        private var enabled = true
        override val biometricEnabled get() = enabled
        override var biometricAvailable = true
        override var biometricCredentialAvailable = true
        override val biometricFirstName = "João"
        var refreshes = 0
        var refreshFailure = false
        override fun setBiometricEnabled(enabled: Boolean) {
            check(biometricAvailable)
            this.enabled = enabled
        }
        override fun createBiometricAuthorizationIntent(): Intent = error("not used")
        override suspend fun completeBiometricAuthorization(result: Intent?) {
            refreshes++
            check(!refreshFailure)
        }
        override fun createAuthorizationIntent(): Intent = error("not used")
        override suspend fun completeAuthorization(result: Intent?) = Unit
        override fun cancelAuthorization() = Unit
        override fun logout() = Unit
    }
}
