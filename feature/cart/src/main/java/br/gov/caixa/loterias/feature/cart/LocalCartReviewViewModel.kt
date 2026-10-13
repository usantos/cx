package br.gov.caixa.loterias.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.*
import br.gov.caixa.loterias.core.domain.account.*
import br.gov.caixa.loterias.core.domain.cart.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LocalCartReviewState(
    val loading: Boolean = false,
    val local: ShoppingCart? = null,
    val remote: ShoppingCart? = null,
    val review: LegacyCartReview? = null,
    val error: String? = null,
    val discarded: Boolean = false,
)

/** Consultation only: an uncertain transaction is never retried by this screen. */
class LocalCartReviewViewModel(
    private val sessions: AuthenticatedSessionIdentityProvider,
    private val content: AuthenticatedSessionContentRepository,
    private val transfer: LegacyCartTransferRepository,
    private val local: VisitorCartRepository,
    private val remote: GetCurrentCartUseCase,
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
) : ViewModel() {
    private val identity = sessions.captureIdentity()
    private val mutableState = MutableStateFlow(LocalCartReviewState())
    val state = mutableState.asStateFlow()
    init { load() }
    fun load() {
        if (state.value.loading) return
        mutableState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch(dispatchers.io) {
            try {
                check(identity != null && sessions.isCurrent(identity))
                val review = transfer.review(identity.ownerKey)
                val pending = local.current().let { cart ->
                    val items = cart.items.filter { item -> review?.bets?.any { bet -> bet.localId == item.id } == true }
                    cart.copy(items = items, total = items.sumOf { it.amount }, totalBetCount = items.size)
                }
                val accountCart = when (val result = remote()) {
                    is AppResult.Success -> result.value
                    is AppResult.Failure -> error("Consulta indisponível")
                }
                check(sessions.isCurrent(identity))
                mutableState.value = LocalCartReviewState(local = pending, remote = accountCart, review = review)
            } catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.update { it.copy(loading = false, error = "Não foi possível consultar as apostas. Entre na conta utilizada no envio e tente novamente.") } }
        }
    }
    fun discard() {
        val review = state.value.review ?: return
        if (state.value.loading || state.value.remote == null) return
        mutableState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch(dispatchers.io) {
            try {
                check(identity != null && sessions.isCurrent(identity))
                check(transfer.discardReviewed(identity.ownerKey, review))
                // Capture content only for the same live identity; do not publish into another login.
                val existing = content.observe().first()
                if (sessions.isCurrent(identity)) check(content.publish(identity, existing?.copy(localCartReviewRequired = false)))
                mutableState.value = LocalCartReviewState(discarded = true)
            } catch (cancelled: kotlinx.coroutines.CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.update { it.copy(loading = false, error = "As apostas ou a sessão foram alteradas. Atualize a consulta antes de excluir.") } }
        }
    }
}
