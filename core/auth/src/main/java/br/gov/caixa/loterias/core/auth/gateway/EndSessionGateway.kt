package br.gov.caixa.loterias.core.auth.gateway

import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch
import br.gov.caixa.loterias.feature.account.domain.model.EndSessionOutcome
import android.content.Intent

interface EndSessionGateway : br.gov.caixa.loterias.feature.account.domain.port.EndSessionLifecycle {
    /** Chamado após o encerramento local da sessão. Nunca deve autorizar nem restaurar uma sessão. */
    override fun prepare(): EndSessionLaunch
    fun intentFor(launch: EndSessionLaunch): Intent
    fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome
    override fun discard(launch: EndSessionLaunch)
}
