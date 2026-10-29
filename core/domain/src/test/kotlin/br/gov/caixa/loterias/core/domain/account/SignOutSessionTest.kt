package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.domain.service.model.ServiceConnectionFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.SessionSignOutRepository
import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult
import br.gov.caixa.loterias.feature.account.domain.usecase.SignOutSession

class SignOutSessionTest {
    @Test fun `missing login sends nothing and replacement login rejects late confirmation`() = runTest {
        var current: AuthenticatedSessionIdentity? = null
        val identities = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = current
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = current === identity
        }
        var calls = 0
        val logout = SignOutSession(identities, SessionSignOutRepository { captured ->
            calls++
            current = AuthenticatedSessionIdentity("b".repeat(64))
            SessionSignOutResult.Confirmed(captured)
        })
        val invalid = SessionSignOutResult.Rejected(ServiceFailure.ConnectionFailed(ServiceConnectionFailure.INVALID_RESPONSE))
        assertEquals(invalid, logout())
        assertEquals(0, calls)
        current = AuthenticatedSessionIdentity("a".repeat(64))
        assertEquals(invalid, logout())
        assertEquals(1, calls)
    }

    @Test fun `same session passes confirmation and typed rejection through`() = runTest {
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        val identities = object : AuthenticatedSessionIdentityProvider {
            override fun captureIdentity() = identity
            override fun isCurrent(identity: AuthenticatedSessionIdentity) = identity === captureIdentity()
        }
        for (result in listOf(SessionSignOutResult.Confirmed(identity), SessionSignOutResult.Rejected(ServiceFailure.Unchanged))) {
            assertSame(result, SignOutSession(identities, SessionSignOutRepository { assertSame(identity, it); result })())
        }
    }
}
