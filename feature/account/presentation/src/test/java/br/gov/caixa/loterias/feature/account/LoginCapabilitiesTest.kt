package br.gov.caixa.loterias.feature.account

import org.junit.Assert.*
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.port.BiometricLoginPreferences
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.port.SessionAuthentication
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginCapabilitiesTest {
    @get:Rule val main = MainDispatcherRule()

    @Test fun `separate domain capabilities retain biometric cancellation and visitor state`() {
        val calls = mutableListOf<String>()
        val session = session(calls)
        val preferences = object : BiometricLoginPreferences {
            private var enabled = true
            override val biometricEnabled get() = enabled
            override val biometricAvailable = true
            override val biometricCredentialAvailable = true
            override fun setBiometricEnabled(enabled: Boolean) { this.enabled = enabled; calls += "biometric:$enabled" }
        }
        val vm = LoginViewModel(session, FixedCoroutineDispatchers(main.dispatcher),
            VerifyPostLoginAccessUseCase { error("Cancellation must not verify access") }, preferences)
        assertTrue(vm.state.value.biometricEnabled)
        assertTrue(vm.state.value.biometricRowVisible)
        assertTrue(vm.canUseBiometricLogin())
        assertTrue(vm.beginAuthorization())
        vm.authorizationCancelled()
        assertFalse(vm.state.value.authorizationPending)
        vm.setBiometricEnabled(false)
        assertTrue(vm.state.value.biometricDisablePending)
        vm.confirmBiometricDisable(true)
        assertFalse(vm.state.value.biometricEnabled)
        assertFalse(vm.canUseBiometricLogin())
        assertTrue(vm.enterVisitor())
        assertFalse(vm.state.value.authenticated)
        assertEquals(listOf("cancel", "biometric:false", "logout"), calls)
    }

    @Test fun `platform exchange callback completes before backend verification`() = runTest(main.dispatcher) {
        val calls = mutableListOf<String>()
        val vm = LoginViewModel(session(calls), FixedCoroutineDispatchers(main.dispatcher),
            VerifyPostLoginAccessUseCase {
                calls += "verify"
                PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
            })
        vm.complete { calls += "exchange" }
        main.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf("exchange", "verify"), calls)
        assertTrue(vm.state.value.authenticated)
        assertFalse(vm.state.value.isLoading)
    }

    private fun session(calls: MutableList<String>) = object : SessionAuthentication {
        override fun cancelAuthorization() { calls += "cancel" }
        override fun logout() { calls += "logout" }
    }
}
