package br.gov.caixa.loterias.core.presentation.state

internal data class DestinationSnapshotState<T>(val request: Long = 0, val value: T? = null)

internal sealed interface DestinationSnapshotAction<out T> {
    data class Requested(val request: Long) : DestinationSnapshotAction<Nothing>
    data class Completed<T>(val request: Long, val value: T?) : DestinationSnapshotAction<T>
}

internal fun <T> reduceDestinationSnapshot(
    state: DestinationSnapshotState<T>,
    action: DestinationSnapshotAction<T>,
): DestinationSnapshotState<T> = when (action) {
    is DestinationSnapshotAction.Requested -> state.copy(request = action.request)
    is DestinationSnapshotAction.Completed -> if (action.request == state.request) state.copy(value = action.value) else state
}
