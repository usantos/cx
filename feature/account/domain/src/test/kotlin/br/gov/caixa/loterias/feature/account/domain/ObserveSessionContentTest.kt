package br.gov.caixa.loterias.feature.account.domain

import br.gov.caixa.loterias.core.domain.contract.account.model.AccountNotification
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionContent
import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.testing.repository.FakeSessionContentRepository
import br.gov.caixa.loterias.feature.account.domain.usecase.ObserveAuthenticatedSessionContent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val OWNER_KEY_LENGTH = 64

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveSessionContentTest {
    @Test
    fun `observation exposes initial state content changes and logout without publishing`() = runTest {
        val repository = FakeSessionContentRepository()
        val values = mutableListOf<AuthenticatedSessionContent?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            ObserveAuthenticatedSessionContent(repository)().collect { values += it }
        }
        assertEquals(listOf<AuthenticatedSessionContent?>(null), values)
        assertEquals(0, repository.publications)

        val identity = AuthenticatedSessionIdentity("a".repeat(OWNER_KEY_LENGTH))
        val content = AuthenticatedSessionContent(
            cart = null,
            latestNotification = AccountNotification(null, "Título", "Mensagem", null, null),
            unidentifiedPayment = false
        )
        assertTrue(repository.publish(identity, content))
        assertTrue(repository.publish(identity, null))

        assertEquals(listOf(null, content, null), values)
        assertEquals(2, repository.publications)
    }
}
