package br.gov.caixa.loterias.feature.home.presentation.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import br.gov.caixa.loterias.feature.home.presentation.mapper.remainingTimeLabel
import kotlinx.coroutines.delay

@Composable
internal fun rememberHomeCardCountdown(closingDateTime: String): androidx.compose.runtime.State<String> {
    val clock = br.gov.caixa.loterias.feature.home.navigation.LocalHomeRuntimeConfiguration.current.serverTime
    return produceState(
        initialValue = remainingTimeLabel(closingDateTime, clock.now().toEpochMilli()),
        key1 = closingDateTime,
        key2 = clock,
    ) {
        while (true) {
            value = remainingTimeLabel(closingDateTime, clock.now().toEpochMilli())
            delay(1_000)
        }
    }
}
