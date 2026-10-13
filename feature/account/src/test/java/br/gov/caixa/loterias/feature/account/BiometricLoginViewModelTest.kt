package br.gov.caixa.loterias.feature.account

import android.content.Intent
import br.gov.caixa.loterias.core.auth.AuthenticationGateway
import br.gov.caixa.loterias.core.auth.BiometricAuthenticationGateway
import br.gov.caixa.loterias.core.common.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.account.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

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
        vm.completeBiometric(null)
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
        vm.completeBiometric(null)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, checks)
        assertFalse(vm.state.value.authenticated)
        assertNotNull(vm.state.value.errorMessage)
    }
    @Test fun `unavailable biometric preference fails closed and displays enrollment guidance`() {
        val auth = FakeBiometricAuthentication().apply { biometricAvailable = false }
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { PostLoginAccessResult.BlockedAccountCheckFailed })
        vm.setBiometricEnabled(true)
        assertFalse(vm.state.value.biometricEnabled)
        assertNotNull(vm.state.value.errorMessage)
    }
    private class FakeBiometricAuthentication : AuthenticationGateway, BiometricAuthenticationGateway {
        private var enabled = true
        override val biometricEnabled get() = enabled
        override var biometricAvailable = true
        override val biometricCredentialAvailable = true
        var refreshes = 0
        var refreshFailure = false
        override fun setBiometricEnabled(enabled: Boolean) { check(biometricAvailable); this.enabled = enabled }
        override fun createBiometricAuthorizationIntent(): Intent = error("not used")
        override suspend fun completeBiometricAuthorization(result: Intent?) { refreshes++; check(!refreshFailure) }
        override fun createAuthorizationIntent(): Intent = error("not used")
        override suspend fun completeAuthorization(result: Intent?) = Unit
        override fun cancelAuthorization() = Unit
        override fun logout() = Unit
    }
}
