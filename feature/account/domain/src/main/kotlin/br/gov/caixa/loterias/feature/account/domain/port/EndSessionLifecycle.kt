package br.gov.caixa.loterias.feature.account.domain.port

import br.gov.caixa.loterias.feature.account.domain.model.EndSessionLaunch

interface EndSessionLifecycle {
    fun prepare(): EndSessionLaunch
    fun discard(launch: EndSessionLaunch)
}
