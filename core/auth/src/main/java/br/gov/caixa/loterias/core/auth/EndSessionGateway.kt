package br.gov.caixa.loterias.core.auth

import android.content.Intent

/** Opaque identity only: browser requests and credentials never enter UI state. */
class EndSessionLaunch {
    override fun toString() = "EndSessionLaunch(redacted)"
}

/** RETURNED means a correlated protocol callback, not revocation of every server session. */
enum class EndSessionOutcome { RETURNED, CANCELLED, UNCONFIRMED }

interface EndSessionGateway {
    /** Called after local logout. Must never authorize or restore a session. */
    fun prepare(): EndSessionLaunch
    fun intentFor(launch: EndSessionLaunch): Intent
    fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome
    fun discard(launch: EndSessionLaunch)
}
