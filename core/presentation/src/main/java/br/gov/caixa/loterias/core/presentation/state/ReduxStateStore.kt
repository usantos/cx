package br.gov.caixa.loterias.core.presentation.state

import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.reduxkotlin.TypedStore
import org.reduxkotlin.threadsafe.createTypedThreadSafeStore

class ReduxStateStore<State, Action : Any> @PublishedApi internal constructor(private val store: TypedStore<State, Action>) : Closeable {
    private val closed = AtomicBoolean()
    private val projection = MutableStateFlow(store.state)
    val state = projection.asStateFlow()
    val value: State get() = synchronized(this) { store.state }
    private val unsubscribe = store.subscribe { projection.value = store.state }

    @Synchronized
    fun dispatch(action: Action) {
        if (!closed.get()) store.dispatch(action)
    }

    @Synchronized
    override fun close() {
        if (closed.compareAndSet(false, true)) unsubscribe()
    }
}

inline fun <State, reified Action : Any> reduxStateStore(
    initialState: State,
    crossinline reducer: (State, Action) -> State,
): ReduxStateStore<State, Action> = ReduxStateStore(createTypedThreadSafeStore(reducer, initialState))
