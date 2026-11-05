package br.gov.caixa.loterias.feature.tickets.domain.model

import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBet
import br.gov.caixa.loterias.core.domain.contract.tickets.model.ConfirmedBetsBackend

data class ConfirmedBetsPage(val bets: List<ConfirmedBet>, val backend: ConfirmedBetsBackend = ConfirmedBetsBackend.SILCE)
