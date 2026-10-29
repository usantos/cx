package br.gov.caixa.loterias.feature.home.domain.usecase

import br.gov.caixa.loterias.core.common.time.LotteryDateTimes
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability

fun interface SelectHomeResaleOffersUseCase {
    operator fun invoke(offers: List<PoolModalityAvailability>, enabled: Boolean): List<PoolModalityAvailability>
}

/** A revenda encerra de forma inclusiva, usando o mesmo relógio do servidor da validação de entrada da aposta. */
class SelectHomeResaleOffers(private val serverTime: ServerTimeSource) : SelectHomeResaleOffersUseCase {
    override fun invoke(offers: List<PoolModalityAvailability>, enabled: Boolean): List<PoolModalityAvailability> {
        if (!enabled) return emptyList()
        val now = serverTime.now()
        return offers.filter { offer ->
            offer.contestNumber != null && LotteryDateTimes.parse(
                "${offer.resaleClosingDate} ${offer.resaleClosingTime}",
            )?.let { now <= it } == true
        }
    }
}
