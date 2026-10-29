package br.gov.caixa.loterias.feature.cart.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.presentation.state.reduxStateStore
import br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider
import br.gov.caixa.loterias.feature.cart.domain.usecase.DiscardLocalCartReviewUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetLocalCartReviewUseCase
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewAction
import br.gov.caixa.loterias.feature.cart.presentation.state.LocalCartReviewState
import br.gov.caixa.loterias.feature.cart.presentation.state.reduceLocalCartReviewState
import kotlinx.coroutines.launch

/** Somente consulta: uma transação incerta nunca é repetida por esta tela. */
class LocalCartReviewViewModel(
    private val sessions: AuthenticatedSessionIdentityProvider,
    private val getReview: GetLocalCartReviewUseCase,
    private val discardReview: DiscardLocalCartReviewUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val identity = sessions.captureIdentity()
    private val mutableState = reduxStateStore<LocalCartReviewState, LocalCartReviewAction>(
        LocalCartReviewState(),
        ::reduceLocalCartReviewState
    ).also {
        addCloseable(it)
    }
    val state = mutableState.state
    init {
        load()
    }
    fun load() {
        if (state.value.loading) return
        mutableState.dispatch(LocalCartReviewAction.LoadStarted)
        viewModelScope.launch(dispatchers.io) {
            try {
                val snapshot = getReview(identity)
                mutableState.dispatch(LocalCartReviewAction.Loaded(snapshot.local, snapshot.remote, snapshot.review))
            } catch (
                cancelled: kotlinx.coroutines.CancellationException,
            ) {
                throw cancelled
            } catch (
                _: Exception,
            ) {
                mutableState.dispatch(LocalCartReviewAction.Failed(messageText("nao_foi_possivel_consultar_as_ap_430cc0")))
            }
        }
    }
    fun discard() {
        val review = state.value.review ?: return
        if (state.value.loading || state.value.remote == null) return
        mutableState.dispatch(LocalCartReviewAction.LoadStarted)
        viewModelScope.launch(dispatchers.io) {
            try {
                discardReview(identity, review)
                mutableState.dispatch(LocalCartReviewAction.Discarded)
            } catch (
                cancelled: kotlinx.coroutines.CancellationException,
            ) {
                throw cancelled
            } catch (
                _: Exception,
            ) {
                mutableState.dispatch(LocalCartReviewAction.Failed(messageText("as_apostas_ou_a_sessao_foram_alt_8c96c3")))
            }
        }
    }
}
