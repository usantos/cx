package br.gov.caixa.loterias.feature.home.presentation.route

import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import java.time.Instant

/** Projeção de compatibilidade para os testes existentes do contrato de prazo. Os efeitos pertencem ao ViewModel. */
internal suspend fun refreshHomeAtPoolResaleExpirations(lotteries: List<HomeLottery>, now: () -> Instant, refresh: () -> Unit) =
    br.gov.caixa.loterias.feature.home.presentation.coordinator.refreshAtPoolResaleExpirations(
        lotteries.map {
            it.poolResaleClosingDateTime
        },
        now,
        refresh
    )
