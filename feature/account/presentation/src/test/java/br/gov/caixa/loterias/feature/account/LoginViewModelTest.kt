package br.gov.caixa.loterias.feature.account

import android.content.Intent
import br.gov.caixa.loterias.core.auth.gateway.AuthenticationGateway
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.AccountStatus
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyPostLoginAccessUseCase
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult
import java.math.BigDecimal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import br.gov.caixa.loterias.feature.account.presentation.state.LoginUiState
import br.gov.caixa.loterias.feature.account.presentation.viewmodel.LoginViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule val dispatcherRule = MainDispatcherRule()
    private val allowed = VerifyPostLoginAccessUseCase { PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO)) }

    @Test fun `publishes authenticated state after token exchange`() = runTest(dispatcherRule.dispatcher) {
        val authentication = FakeAuthentication()
        val viewModel = LoginViewModel(authentication,
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            allowed,
        )
        viewModel.complete { authentication.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.state.value.authenticated)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test fun `publishes safe error after failed authentication`() = runTest(dispatcherRule.dispatcher) {
        val authentication = FakeAuthentication(failure = true)
        val viewModel = LoginViewModel(authentication,
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            allowed,
        )
        viewModel.complete { authentication.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Não foi possível entrar. Tente novamente.", viewModel.state.value.errorMessage)
    }

    @Test fun `updates biometric preference without changing authentication state`() {
        val authentication = FakeAuthentication()
        val viewModel = LoginViewModel(authentication,
            FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            allowed,
        )

        viewModel.setBiometricEnabled(true)

        assertTrue(viewModel.state.value.biometricEnabled)
        assertFalse(viewModel.state.value.authenticated)
    }

    @Test fun `maps backend account destination after login`() = runTest(dispatcherRule.dispatcher) {
        val destinations = mapOf(
            PostLoginDestination.HOME to "home",
            PostLoginDestination.REGISTRATION to "account/register",
            PostLoginDestination.TERMS to "support/terms-acceptance",
            PostLoginDestination.SELF_ASSESSMENT to "responsible/start/required",
        )
        destinations.forEach { (destination, route) ->
            val authentication = FakeAuthentication()
        val viewModel = LoginViewModel(authentication,
                FixedCoroutineDispatchers(dispatcherRule.dispatcher),
                VerifyPostLoginAccessUseCase {
                    PostLoginAccessResult.Verified(AccountAccess(destination, false, BigDecimal.ZERO))
                },
            )
            viewModel.setBiometricEnabled(true)
            viewModel.complete { authentication.completeAuthorization(null) }
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(route, viewModel.state.value.destination)
            assertTrue(viewModel.state.value.biometricEnabled)
        }
    }

    @Test fun `maps account verification failures without authenticating`() = runTest(dispatcherRule.dispatcher) {
        listOf(
            AppError.Network to "Não foi possível validar o cadastro. Verifique sua internet e tente novamente.",
            AppError.Validation("") to "Não foi possível validar o cadastro.",
            AppError.Validation("Cadastro inválido") to "Cadastro inválido",
            AppError.Unexpected to "Ocorreu um erro inesperado ao validar o cadastro. Tente novamente.",
        ).forEach { (error, message) ->
            val authentication = FakeAuthentication()
        val viewModel = LoginViewModel(authentication,
                FixedCoroutineDispatchers(dispatcherRule.dispatcher),
                VerifyPostLoginAccessUseCase { PostLoginAccessResult.AccountCheckFailed(error) },
            )
            viewModel.complete { authentication.completeAuthorization(null) }
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertFalse(viewModel.state.value.authenticated)
            assertEquals(message, viewModel.state.value.errorMessage)
        }
    }

    @Test fun `authorization launch is single flight and cancellation permits a manual retry`() {
        val auth = FakeAuthentication()
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), allowed)
        assertTrue(vm.beginAuthorization())
        assertFalse(vm.beginAuthorization())
        assertTrue(vm.state.value.authorizationPending)
        vm.authorizationCancelled()
        assertEquals(1, auth.cancellations)
        assertFalse(vm.state.value.authorizationPending)
        assertEquals(0, auth.completions)
        assertEquals(null, vm.state.value.errorMessage)
        assertTrue(vm.beginAuthorization())
        vm.authorizationLaunchFailed()
        assertEquals(2, auth.cancellations)
        assertFalse(vm.state.value.authorizationPending)
        assertEquals("Não foi possível abrir o navegador para entrar. Tente novamente.", vm.state.value.errorMessage)
        assertTrue(vm.beginAuthorization())
        assertEquals(null, vm.state.value.errorMessage)
    }

    @Test fun `duplicate callback cannot repeat token exchange or verification`() = runTest(dispatcherRule.dispatcher) {
        val auth = FakeAuthentication()
        var checks = 0
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase { checks++; allowed() })
        assertTrue(vm.beginAuthorization())
        vm.complete { auth.completeAuthorization(null) }
        vm.complete { auth.completeAuthorization(null) }
        assertFalse(vm.beginAuthorization())
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        vm.complete { auth.completeAuthorization(null) }
        assertFalse(vm.beginAuthorization())
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, auth.completions)
        assertEquals(1, checks)
        assertTrue(vm.state.value.authenticated)
        assertFalse(vm.state.value.authorizationPending)
    }

    @Test fun `cancelled token exchange does not validate account or leave loading`() = runTest(dispatcherRule.dispatcher) {
        val authentication = FakeAuthentication(cancelled = true)
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { error("Must not verify after cancellation") })
        vm.complete { authentication.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.isLoading)
        assertFalse(vm.state.value.authenticated)
        assertEquals(null, vm.state.value.errorMessage)
        assertTrue(vm.beginAuthorization())
    }

    @Test fun `cancelled account verification does not authorize or leave loading`() = runTest(dispatcherRule.dispatcher) {
        val authentication = FakeAuthentication()
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { throw kotlinx.coroutines.CancellationException() })
        vm.complete { authentication.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.isLoading)
        assertFalse(vm.state.value.authenticated)
        assertTrue(vm.beginAuthorization())
    }

    @Test fun `token screen remains active through exchange and account validation without repeating callbacks`() = runTest(dispatcherRule.dispatcher) {
        val tokenGate = CompletableDeferred<Unit>()
        val accessGate = CompletableDeferred<Unit>()
        var exchanges = 0
        var verifications = 0
        val authentication = object : AuthenticationGateway by FakeAuthentication() {
            override suspend fun completeAuthorization(result: Intent?) {
                exchanges++
                tokenGate.await()
            }
        }
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase {
                verifications++
                accessGate.await()
                allowed()
            })

        vm.beginAuthorization()
        assertFalse(vm.state.value.isLoading)
        vm.complete { authentication.completeAuthorization(null) }
        assertTrue(vm.state.value.isLoading)
        assertFalse(vm.state.value.authorizationPending)
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(1, exchanges)
        assertEquals(0, verifications)
        assertTrue(vm.state.value.isLoading)

        tokenGate.complete(Unit)
        dispatcherRule.dispatcher.scheduler.runCurrent()
        assertEquals(1, verifications)
        assertTrue(vm.state.value.isLoading)
        assertFalse(vm.state.value.authenticated)
        vm.complete { authentication.completeAuthorization(null) }
        assertFalse(vm.beginAuthorization())
        accessGate.complete(Unit)
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.isLoading)
        assertTrue(vm.state.value.authenticated)
        assertEquals(1, exchanges)
        assertEquals(1, verifications)
    }

    @Test fun `blocked check failure waits for acknowledgment clears session once and only permits manual retry`() = runTest(dispatcherRule.dispatcher) {
        var logouts = 0
        var checks = 0
        val auth = object : AuthenticationGateway by FakeAuthentication() {
            override fun logout() { logouts++ }
        }
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { checks++; PostLoginAccessResult.BlockedAccountCheckFailed })
        assertFalse(vm.acknowledgeBlockedAccountFailure())
        vm.setBiometricEnabled(true)
        vm.complete { auth.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.blockedAccountCheckFailed)
        assertFalse(vm.state.value.authenticated)
        assertFalse(vm.state.value.isLoading)
        assertFalse(vm.beginAuthorization())
        vm.complete { auth.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, checks)
        assertEquals(0, logouts)
        assertTrue(vm.acknowledgeBlockedAccountFailure())
        assertEquals(LoginUiState(biometricEnabled = true), vm.state.value)
        assertFalse(vm.acknowledgeBlockedAccountFailure())
        assertEquals(1, logouts)
        assertEquals(1, checks)
        assertTrue(vm.beginAuthorization())
        assertEquals(1, checks)
    }

    @Test fun `cannot dismiss blocking decision if clearing secure credentials fails`() = runTest(dispatcherRule.dispatcher) {
        var failLogout = true
        val auth = object : AuthenticationGateway by FakeAuthentication() {
            override fun logout() { if (failLogout) error("private-storage-error") }
        }
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { PostLoginAccessResult.BlockedAccountCheckFailed })
        vm.complete { auth.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.acknowledgeBlockedAccountFailure())
        assertTrue(vm.state.value.blockedAccountCheckFailed)
        assertFalse(vm.beginAuthorization())
        assertEquals(null, vm.state.value.errorMessage)
        failLogout = false
        assertTrue(vm.acknowledgeBlockedAccountFailure())
        assertFalse(vm.state.value.blockedAccountCheckFailed)
    }

    @Test fun `partial block requires one acknowledgment preserves destination and never logs out or repeats calls`() = runTest(dispatcherRule.dispatcher) {
        for (destination in PostLoginDestination.entries) {
            var checks = 0
            val auth = FakeAuthentication()
            val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase {
                checks++
                PostLoginAccessResult.Verified(AccountAccess(destination, false, BigDecimal.TEN, AccountStatus.PARTIALLY_BLOCKED))
            })
            assertFalse(vm.acknowledgeConsultationOnlyNotice())
            vm.setBiometricEnabled(true)
            vm.complete { auth.completeAuthorization(null) }
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            val pending = vm.state.value
            assertTrue(pending.consultationOnlyNoticePending)
            assertFalse(pending.authenticated)
            assertFalse(pending.isLoading)
            assertFalse(vm.beginAuthorization())
            vm.complete { auth.completeAuthorization(null) }
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, auth.completions)
            assertEquals(1, checks)
            assertFalse(vm.acknowledgeBlockedAccountFailure())
            assertTrue(vm.acknowledgeConsultationOnlyNotice())
            assertTrue(vm.state.value.authenticated)
            assertEquals(pending.destination, vm.state.value.destination)
            assertTrue(vm.state.value.biometricEnabled)
            assertFalse(vm.state.value.consultationOnlyNoticePending)
            assertFalse(vm.acknowledgeConsultationOnlyNotice())
            assertFalse(vm.beginAuthorization())
            vm.complete { auth.completeAuthorization(null) }
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertEquals(1, auth.completions)
            assertEquals(1, checks)
            assertEquals(0, auth.logouts)
        }
    }

    @Test fun `absent and active account status do not show a partial block notice`() = runTest(dispatcherRule.dispatcher) {
        for (status in listOf(null, AccountStatus.ACTIVE)) {
            val authentication = FakeAuthentication()
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher), VerifyPostLoginAccessUseCase {
                PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN, status))
            })
            vm.complete { authentication.completeAuthorization(null) }
            dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
            assertFalse(vm.state.value.consultationOnlyNoticePending)
            assertTrue(vm.state.value.authenticated)
            assertFalse(vm.acknowledgeConsultationOnlyNotice())
        }
    }

    @Test fun `visitor entry after failed account verification clears exchanged session`() = runTest(dispatcherRule.dispatcher) {
        val authentication = FakeAuthentication()
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { PostLoginAccessResult.AccountCheckFailed(AppError.Network) })
        vm.complete { authentication.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertFalse(vm.state.value.authenticated)
        assertNotNull(vm.state.value.errorMessage)
        assertEquals(1, authentication.logouts)
        assertTrue(vm.enterVisitor())
        assertEquals(2, authentication.logouts)
        assertEquals(LoginUiState(), vm.state.value)
    }

    @Test fun `failed visitor cleanup denies navigation and can be retried`() {
        var logoutFails = true
        var logouts = 0
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("not used")
            override suspend fun completeAuthorization(result: Intent?) = Unit
            override fun cancelAuthorization() = Unit
            override fun logout() { logouts++; check(!logoutFails) }
        }
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { PostLoginAccessResult.BlockedAccountCheckFailed })
        assertFalse(vm.enterVisitor())
        assertNotNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.authenticated)
        logoutFails = false
        assertTrue(vm.enterVisitor())
        assertEquals(2, logouts)
        assertEquals(LoginUiState(), vm.state.value)
    }

    @Test fun `visitor entry does not clear a successfully verified or pending login`() = runTest(dispatcherRule.dispatcher) {
        val auth = FakeAuthentication()
        val vm = LoginViewModel(auth, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { PostLoginAccessResult.Verified(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN)) })
        assertTrue(vm.beginAuthorization())
        assertFalse(vm.enterVisitor())
        vm.complete { auth.completeAuthorization(null) }
        assertFalse(vm.enterVisitor())
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.authenticated)
        assertFalse(vm.enterVisitor())
        assertEquals(0, auth.logouts)
    }

    @Test fun `failed account query immediately revokes acquired identity before login error is shown`() = runTest(dispatcherRule.dispatcher) {
        val store = br.gov.caixa.loterias.core.auth.session.SessionTokenStore()
        var acquiredIdentity: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity? = null
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("not used")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) {
                assertTrue(br.gov.caixa.loterias.core.auth.fixture.SyntheticSessionFixture.install(store))
                acquiredIdentity = store.captureIdentity()
            }
            override fun logout() { store.clear() }
        }
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase {
                assertNotNull(store.captureIdentity())
                PostLoginAccessResult.AccountCheckFailed(AppError.Network)
            })
        vm.complete { authentication.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertNotNull(acquiredIdentity)
        assertEquals(null, store.captureIdentity())
        assertEquals(null, store.token())
        assertFalse(store.isCurrent(acquiredIdentity!!))
        assertEquals(null, store.tokenFor(acquiredIdentity!!))
        assertNotNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.authenticated)
        assertTrue(vm.beginAuthorization())
    }

    @Test fun `account query cleanup failure retains blocking notice until successful cleanup retry`() = runTest(dispatcherRule.dispatcher) {
        var cleanupFails = true
        var cleared = false
        val authentication = object : AuthenticationGateway {
            override fun createAuthorizationIntent(): Intent = error("not used")
            override fun cancelAuthorization() = Unit
            override suspend fun completeAuthorization(result: Intent?) = Unit
            override fun logout() { check(!cleanupFails); cleared = true }
        }
        val vm = LoginViewModel(authentication, FixedCoroutineDispatchers(dispatcherRule.dispatcher),
            VerifyPostLoginAccessUseCase { PostLoginAccessResult.AccountCheckFailed(AppError.Network) })
        vm.complete { authentication.completeAuthorization(null) }
        dispatcherRule.dispatcher.scheduler.advanceUntilIdle()
        assertTrue(vm.state.value.blockedAccountCheckFailed)
        assertFalse(vm.beginAuthorization())
        assertFalse(vm.enterVisitor())
        assertFalse(vm.acknowledgeBlockedAccountFailure())
        assertFalse(cleared)
        cleanupFails = false
        assertTrue(vm.acknowledgeBlockedAccountFailure())
        assertTrue(cleared)
        assertFalse(vm.state.value.blockedAccountCheckFailed)
        assertTrue(vm.beginAuthorization())
    }

    private class FakeAuthentication(private val failure: Boolean = false, private val cancelled: Boolean = false) : AuthenticationGateway {
        var completions = 0
        var logouts = 0
        var cancellations = 0
        override fun cancelAuthorization() { cancellations++ }
        override fun createAuthorizationIntent(): Intent = error("not used")
        override suspend fun completeAuthorization(result: Intent?) {
            completions++
            if (cancelled) throw kotlinx.coroutines.CancellationException()
            if (failure) error("denied")
        }
        override fun logout() { logouts++ }
    }
}
