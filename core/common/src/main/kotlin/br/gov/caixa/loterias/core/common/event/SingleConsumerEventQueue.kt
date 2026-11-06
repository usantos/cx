package br.gov.caixa.loterias.core.common.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Uma FIFO limitada para um único consumidor de tela, retida apenas durante a vida de seu ViewModel.
 * Envios aguardam quando ela está cheia; eventos aguardam enquanto a tela não tem coletor. O recebimento remove
 * um evento, portanto coletores que retornam não o repetem. Não é um fluxo de difusão nem
 * uma confirmação de efeito externo, e não sobrevive ao encerramento do processo.
 */
class SingleConsumerEventQueue<T> {
    private val channel = Channel<T>(Channel.BUFFERED)
    val events: Flow<T> = channel.receiveAsFlow()

    suspend fun emit(value: T) {
        channel.send(value)
    }
}
