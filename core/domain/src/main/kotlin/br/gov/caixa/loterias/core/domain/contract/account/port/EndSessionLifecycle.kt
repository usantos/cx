package br.gov.caixa.loterias.core.domain.contract.account.port

import br.gov.caixa.loterias.core.domain.contract.account.model.EndSessionLaunch

interface EndSessionLifecycle {
    fun prepare(): EndSessionLaunch
    fun discard(launch: EndSessionLaunch)
}
