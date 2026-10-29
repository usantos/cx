package br.gov.caixa.loterias.feature.betting.domain.model

import br.gov.caixa.loterias.core.common.time.LotteryDateTimes
import java.time.Instant

/** EncerramentoUtil: os instantes de encerramento individual e da revenda são inclusivos. */
fun isPoolOnlyWindow(
    game: LotteryGameParameters,
    availablePools: List<PoolModalityAvailability>,
    poolSalesEnabled: Boolean,
    now: Instant,
): Boolean {
    if (!poolSalesEnabled) return false
    val pool = availablePools.firstOrNull {
        it.modalityId == game.modality.id && it.contestType.equals(game.contestType, ignoreCase = true) &&
            it.contestNumber == game.contestNumber
    } ?: return false
    val individualClosing = LotteryDateTimes.parse(game.closingDateTime) ?: return false
    val resaleClosing = LotteryDateTimes.parse("${pool.resaleClosingDate} ${pool.resaleClosingTime}") ?: return false
    return now >= individualClosing && now <= resaleClosing
}
