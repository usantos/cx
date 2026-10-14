package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** CX keeps simulation parameters for 180 seconds within the current session. */
class SessionBettingParametersRepository(
    private val delegate: BettingRepository,
    private val namespace: () -> String,
    private val identity: () -> AuthenticatedSessionIdentity?,
    private val elapsedMillis: () -> Long = { System.nanoTime() / 1_000_000 },
) : BettingRepository by delegate {
    private val mutex = Mutex()
    private var cached: Entry? = null

    override suspend fun parameters(): BettingParameters = mutex.withLock {
        val requestNamespace = namespace()
        val requestIdentity = identity()
        val previous = cached
        if (previous != null && (previous.namespace != requestNamespace || previous.identity !== requestIdentity)) {
            cached = null
        }
        val age = cached?.let { elapsedMillis() - it.loadedAt }
        if (age != null && age in 0 until TTL_MILLIS) return@withLock requireNotNull(cached).parameters

        val parameters = delegate.parameters()
        check(namespace() == requestNamespace && identity() === requestIdentity) {
            "Sessão substituída durante a consulta de parâmetros"
        }
        cached = Entry(requestNamespace, requestIdentity, elapsedMillis(), parameters)
        parameters
    }

    private data class Entry(
        val namespace: String,
        val identity: AuthenticatedSessionIdentity?,
        val loadedAt: Long,
        val parameters: BettingParameters,
    )

    companion object {
        const val TTL_MILLIS = 180_000L
    }
}
