package br.gov.caixa.loterias.feature.support.presentation.state

import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import java.io.Closeable

internal class SupportStateStore(initialState: SupportUiState) : Closeable {
    private val store = reduxStateStore(
        initialState = if (initialState.mode == SupportMode.TERMS_READ_ONLY) {
            initialState.copy(
                selectedTab = SupportTab.TERMS,
                showTermsTabs = false,
                acceptanceRequired = false,
                termsAccepted = false,
                selectedSectionId = null
            )
        } else {
            initialState
        },
        reducer = ::reduceSupportState
    )
    val state = store.state

    fun dispatch(mutation: SupportMutation) {
        store.dispatch(mutation)
    }

    override fun close() {
        store.close()
    }
}
