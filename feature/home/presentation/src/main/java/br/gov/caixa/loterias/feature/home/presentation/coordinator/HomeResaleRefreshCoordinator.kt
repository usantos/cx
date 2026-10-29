package br.gov.caixa.loterias.feature.home.presentation.coordinator

import br.gov.caixa.loterias.core.common.time.LotteryDateTimes
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import java.time.Duration
import java.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface HomeVisibilityActions {
    fun update(visible: Boolean, deadlines: List<String>, serverTime: ServerTimeSource)
}

internal class HomeResaleRefreshCoordinator(private val scope: CoroutineScope, private val refresh: () -> Unit) : HomeVisibilityActions {
    private var job: Job? = null

    override fun update(visible: Boolean, deadlines: List<String>, serverTime: ServerTimeSource) {
        job?.cancel()
        job = if (visible) {
            scope.launch {
                refreshAtPoolResaleExpirations(deadlines, serverTime::now, refresh)
            }
        } else {
            null
        }
    }
}

/** O encerramento inclusivo do servidor vale até seu instante; atualize um milissegundo depois. */
internal suspend fun refreshAtPoolResaleExpirations(deadlines: List<String>, now: () -> Instant, refresh: () -> Unit) {
    val instants = deadlines.mapNotNull(LotteryDateTimes::parse).map { it.plusMillis(1) }
        .filter { it > now() }.distinct().sorted()
    for (deadline in instants) {
        val remaining = Duration.between(now(), deadline).toMillis()
        if (remaining > 0) delay(remaining)
        refresh()
    }
}
