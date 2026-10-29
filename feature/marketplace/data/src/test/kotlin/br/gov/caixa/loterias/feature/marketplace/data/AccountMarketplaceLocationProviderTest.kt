package br.gov.caixa.loterias.feature.marketplace.data

import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.account.domain.model.MunicipalityId
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.account.domain.repository.AccountMunicipalityRepository
import br.gov.caixa.loterias.feature.marketplace.data.location.AccountMarketplaceLocationProvider
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceLocation
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountMarketplaceLocationProviderTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private var loggedIn = true
    private var current = true
    private var reads = 0
    private val sessions = object : AuthenticatedSessionIdentityProvider {
        override fun captureIdentity() = identity.takeIf { loggedIn }
        override fun isCurrent(candidate: AuthenticatedSessionIdentity) = current && candidate === identity
    }

    @Test fun `converte municipio e estado do perfil de dominio`() = runTest {
        assertEquals(MarketplaceLocation(MUNICIPALITY_NUMBER, STATE_ID), provider().current())
        assertEquals(1, reads)
    }

    @Test fun `sessao ausente impede consultar o perfil`() = runTest {
        loggedIn = false
        assertTrue(runCatching { provider().current() }.exceptionOrNull() is IllegalArgumentException)
        assertEquals(0, reads)
    }

    @Test fun `substituicao durante consulta rejeita a localizacao antiga`() = runTest {
        val location = provider {
            current = false
            municipality()
        }
        assertTrue(runCatching { location.current() }.exceptionOrNull() is IllegalStateException)
        assertEquals(1, reads)
    }

    @Test fun `falha do perfil permanece disponivel ao caso de uso consumidor`() = runTest {
        val failure = IOException("perfil indisponivel")
        assertSame(failure, runCatching { provider { throw failure }.current() }.exceptionOrNull())
    }

    @Test fun `cancelamento nao vira localizacao ou falha sintetica`() = runTest {
        val cancellation = CancellationException("cancelado")
        assertSame(cancellation, runCatching { provider { throw cancellation }.current() }.exceptionOrNull())
    }

    private fun provider(read: suspend () -> MunicipalityId = { municipality() }) = AccountMarketplaceLocationProvider(
        AccountMunicipalityRepository {
            reads++
            read()
        },
        sessions
    )

    private fun municipality() = MunicipalityId(STATE_ID, MUNICIPALITY_NUMBER, 0)
    private companion object {
        const val STATE_ID = 7L
        const val MUNICIPALITY_NUMBER = 123L
    }
}
