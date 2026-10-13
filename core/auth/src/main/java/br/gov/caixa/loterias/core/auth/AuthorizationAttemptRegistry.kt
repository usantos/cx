package br.gov.caixa.loterias.core.auth

internal class PendingAuthorization<T>(val state: String, val request: T, val ticket: AuthorizationAttempt) {
    override fun toString() = "PendingAuthorization(redacted)"
}

/** Keeps the locally generated request, never a request deserialized from an incoming intent. */
internal class AuthorizationAttemptRegistry<T>(private val tokens: SessionTokenStore) {
    private var pending: PendingAuthorization<T>? = null

    @Synchronized fun begin(state: String, request: T): PendingAuthorization<T> {
        require(state.isNotBlank())
        return PendingAuthorization(state, request, tokens.beginAuthorization()).also { pending = it }
    }

    @Synchronized fun consume(state: String?): PendingAuthorization<T>? {
        val current = pending ?: return null
        if (state == null || current.state != state) return null
        pending = null
        return current
    }

    @Synchronized fun cancel() {
        pending = null
        tokens.cancelAuthorization()
    }

    @Synchronized fun logout(): String? {
        pending = null
        return tokens.clearForEndSession()
    }
}
