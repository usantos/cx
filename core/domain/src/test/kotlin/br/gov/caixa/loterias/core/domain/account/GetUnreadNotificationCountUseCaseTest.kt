package br.gov.caixa.loterias.core.domain.account

import org.junit.Assert.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.usecase.GetUnreadNotificationCountUseCase
import br.gov.caixa.loterias.feature.account.domain.repository.UnreadNotificationCountRepository
import br.gov.caixa.loterias.feature.account.domain.model.UnreadNotificationCountUnavailableException

class GetUnreadNotificationCountUseCaseTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private var current: AuthenticatedSessionIdentity? = identity
    private val sessions = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = current
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === current
    }

    @Test fun `empty and two unread records produce zero and two`() = runTest {
        for (count in listOf(0, 2)) {
            var calls = 0
            val useCase = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository {
                assertSame(identity, it); calls++; count
            }, sessions)
            assertEquals(count, useCase())
            assertEquals(1, calls)
        }
    }

    @Test fun `absent session never requests unread records`() = runTest {
        current = null
        val useCase = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository {
            fail("No authenticated session"); 2
        }, sessions)
        assertEquals(0, useCase())
    }

    @Test fun `session expired immediately after capture does not request`() = runTest {
        val expired = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = false
        }
        val useCase = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository {
            fail("Expired captured session"); 2
        }, expired)
        assertEquals(0, useCase())
    }

    @Test fun `old session response is discarded even for the same owner`() = runTest {
        val useCase = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository {
            current = AuthenticatedSessionIdentity(identity.ownerKey)
            2
        }, sessions)
        assertEquals(0, useCase())
    }

    @Test fun `negative counts are clamped and expected service failure hides badge`() = runTest {
        assertEquals(0, GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository { -1 }, sessions)())
        assertEquals(0, GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository {
            throw UnreadNotificationCountUnavailableException()
        }, sessions)())
    }

    @Test fun `unexpected programmer failure is not hidden`() = runTest {
        val failure = IllegalStateException("Synthetic invariant failure")
        val useCase = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository { throw failure }, sessions)
        assertSame(failure, runCatching { useCase() }.exceptionOrNull())
    }

    @Test fun `failed requests hide badge and cancellation is propagated`() = runTest {
        val useCase = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository { throw IOException() }, sessions)
        assertEquals(0, useCase())
        val cancelled = CancellationException("cancelled")
        val cancelling = GetUnreadNotificationCountUseCase(UnreadNotificationCountRepository { throw cancelled }, sessions)
        assertSame(cancelled, runCatching { cancelling() }.exceptionOrNull())
    }
}
