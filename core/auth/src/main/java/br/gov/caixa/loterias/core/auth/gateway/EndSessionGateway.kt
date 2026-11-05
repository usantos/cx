package br.gov.caixa.loterias.core.auth.gateway

import android.content.Intent
import br.gov.caixa.loterias.core.domain.contract.account.model.EndSessionLaunch
import br.gov.caixa.loterias.core.domain.contract.account.model.EndSessionOutcome

interface EndSessionGateway : br.gov.caixa.loterias.core.domain.contract.account.port.EndSessionLifecycle {
    /** Chamado após o encerramento local da sessão. Nunca deve autorizar nem restaurar uma sessão. */
    override fun prepare(): EndSessionLaunch
    fun intentFor(launch: EndSessionLaunch): Intent
    fun complete(launch: EndSessionLaunch, result: Intent?): EndSessionOutcome
    override fun discard(launch: EndSessionLaunch)
}
