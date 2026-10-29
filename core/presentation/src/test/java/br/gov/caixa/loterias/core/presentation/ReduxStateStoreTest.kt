package br.gov.caixa.loterias.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReduxStateStoreTest {
    @Test fun dispatchPublishesTheReducerResultAsReadOnlyState() {
        reduxStateStore(Counter(), ::counterReducer).use { store ->
            assertFalse(store.state is MutableStateFlow<*>)
            store.dispatch(CounterAction.Add(DELTA))
            assertEquals(DELTA, store.value.count)
            assertEquals(store.value, store.state.value)
        }
    }

    @Test fun distinctStoresDoNotShareState() {
        reduxStateStore(Counter(), ::counterReducer).use { first ->
            reduxStateStore(Counter(), ::counterReducer).use { second ->
                first.dispatch(CounterAction.Add(DELTA))
                assertEquals(DELTA, first.value.count)
                assertEquals(0, second.value.count)
            }
        }
    }

    @Test fun closeIsIdempotentAndStopsFurtherDispatch() {
        val store = reduxStateStore(Counter(), ::counterReducer)
        store.dispatch(CounterAction.Add(DELTA))
        store.close()
        store.close()
        store.dispatch(CounterAction.Add(DELTA))
        assertEquals(DELTA, store.value.count)
        assertEquals(DELTA, store.state.value.count)
    }

    @Test fun viewModelClearingClosesItsOwnedStore() {
        val owner = ViewModelStore()
        val viewModel = CounterViewModel()
        owner.put("counter", viewModel)
        viewModel.store.dispatch(CounterAction.Add(DELTA))
        owner.clear()
        viewModel.store.dispatch(CounterAction.Add(DELTA))
        assertEquals(DELTA, viewModel.store.value.count)
    }

    @Test fun concurrentDispatchKeepsTheProjectionConsistentWithTheStore() {
        reduxStateStore(Counter(), ::counterReducer).use { store ->
            val pool = Executors.newFixedThreadPool(WORKERS)
            try {
                val futures = (1..ACTION_COUNT).map { pool.submit { store.dispatch(CounterAction.Add(1)) } }
                futures.forEach { it.get(TIMEOUT_SECONDS, TimeUnit.SECONDS) }
                assertEquals(ACTION_COUNT, store.value.count)
                assertEquals(store.value, store.state.value)
            } finally {
                pool.shutdownNow()
            }
        }
    }

    @Test fun anExternalClaimLockSerializesOrdinaryDispatch() {
        reduxStateStore(Counter(), ::counterReducer).use { store ->
            val pool = Executors.newSingleThreadExecutor()
            val started = CountDownLatch(1)
            val finished = CountDownLatch(1)
            try {
                val future = synchronized(store) {
                    val task = pool.submit {
                        started.countDown()
                        store.dispatch(CounterAction.Add(DELTA))
                        finished.countDown()
                    }
                    assertTrue(started.await(TIMEOUT_SECONDS, TimeUnit.SECONDS))
                    assertFalse(finished.await(CLAIM_WAIT_MILLIS, TimeUnit.MILLISECONDS))
                    task
                }
                future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                assertEquals(DELTA, store.value.count)
            } finally {
                pool.shutdownNow()
            }
        }
    }

    @Test fun reducersReceiveCurrentStateAndDoNotMutateTheSeed() {
        val seed = Counter()
        reduxStateStore(seed, ::counterReducer).use { store ->
            store.dispatch(CounterAction.Add(DELTA))
            store.dispatch(CounterAction.Add(DELTA))
            assertEquals(0, seed.count)
            assertEquals(DELTA + DELTA, store.value.count)
            assertTrue(store.state.value !== seed)
        }
    }
}

private data class Counter(val count: Int = 0)
private sealed interface CounterAction {
    data class Add(val value: Int) : CounterAction
}
private fun counterReducer(state: Counter, action: CounterAction): Counter = when (action) {
    is CounterAction.Add -> state.copy(count = state.count + action.value)
}
private class CounterViewModel : ViewModel() {
    val store = reduxStateStore(Counter(), ::counterReducer).also { addCloseable(it) }
}
private const val DELTA = 3
private const val WORKERS = 4
private const val ACTION_COUNT = 100
private const val TIMEOUT_SECONDS = 5L

private const val CLAIM_WAIT_MILLIS = 250L
