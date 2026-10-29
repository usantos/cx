package br.gov.caixa.loterias.core.auth.authorization

import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome

/** Correlação em memória e de uso único. Uma função de retorno antiga não pode consumir uma requisição mais recente. */
internal class EndSessionRegistry<T> {
    private class Pending<T>(val launch: EndSessionLaunch, val state: String, var request: T?)
    private var pending: Pending<T>? = null

    @Synchronized fun prepare(state: String, request: T): EndSessionLaunch {
        require(state.isNotBlank())
        return EndSessionLaunch().also { pending = Pending(it, state, request) }
    }

    @Synchronized fun takeRequest(launch: EndSessionLaunch): T? {
        val current = pending ?: return null
        if (current.launch !== launch) return null
        return current.request.also { current.request = null }
    }

    @Synchronized fun complete(launch: EndSessionLaunch, state: String?, cancelled: Boolean): EndSessionOutcome {
        val current = pending ?: return EndSessionOutcome.UNCONFIRMED
        if (current.launch !== launch) return EndSessionOutcome.UNCONFIRMED
        pending = null
        // Uma resposta anterior ao envio da requisição nunca é uma confirmação.
        return when {
            current.request != null -> EndSessionOutcome.UNCONFIRMED
            cancelled -> EndSessionOutcome.CANCELLED
            state == current.state -> EndSessionOutcome.RETURNED
            else -> EndSessionOutcome.UNCONFIRMED
        }
    }

    @Synchronized fun discard(launch: EndSessionLaunch) {
        if (pending?.launch === launch) pending = null
    }

    @Synchronized fun clear() { pending = null }
}
