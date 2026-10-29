package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
import br.gov.caixa.loterias.feature.cart.domain.rule.CartReservationExpiry
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetCurrentCartUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveCartItemUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartReservationAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class CartReservationCoordinator(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val scope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val serverTime: br.gov.caixa.loterias.core.common.time.ServerTimeSource,
    private val getCurrentCart: GetCurrentCartUseCase,
    private val removeCartItem: RemoveCartItemUseCase,
    private val removeExpiredPoolShare: br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShareUseCase?,
    session: CartReservationSession,
    private val applyResult: (AppResult<ShoppingCart>) -> Unit,
    private val loadParameters: () -> Unit,
    private val setCartRequestRunning: (Boolean) -> Unit
) {
    private val reservationSessions = session.identities
    private val reservationOwner = reservationSessions?.captureIdentity()
    private var reservationSessionInvalidated = false
    private var expirationJob: Job? = null
    private data class ReservationKey(val itemId: Long, val poolCode: String, val expiresAt: String)
    private val attemptedReservations = mutableSetOf<ReservationKey>()
    init {
        session.changes?.let { changes ->
            scope.launch {
                changes.collect { identity ->
                    if (identity !== reservationOwner) invalidateReservationSession()
                }
            }
        }
    }

    fun reservationNow(): java.time.Instant = serverTime.now()

    fun reservationSessionCurrent(): Boolean = !reservationSessionInvalidated &&
        (
            reservationSessions == null || reservationOwner?.let(reservationSessions::isCurrent)
                ?: (reservationSessions.captureIdentity() == null)
            )

    fun invalidateReservationSession() {
        reservationSessionInvalidated = true
        expirationJob?.cancel()
        scope.cancel()
        mutableState.dispatch(CartReservationAction.SessionInvalidated)
    }

    fun refreshExpiredReservations() {
        if (!reservationSessionCurrent()) {
            invalidateReservationSession()
            return
        }
        if (mutableState.value.loading || expirationJob?.isActive == true) return
        val expired = mutableState.value.cart?.items.orEmpty().filter { item ->
            item.kind == br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind.POOL_SHARE &&
                item.poolShare?.let { share ->
                    CartReservationExpiry.removalDue(share.expiresAt, serverTime.now())
                } == true
        }
        if (expired.none { item ->
                item.poolShare?.let { ReservationKey(item.id, it.code, it.expiresAt) !in attemptedReservations } == true
            }
        ) {
            return
        }
        // Reserva o carrinho sincronamente: ações de interface na mesma execução não podem disputar o DELETE agendado.
        setCartRequestRunning(true)
        mutableState.dispatch(CartReservationAction.ExpirationRemovalStarted)
        expirationJob = scope.launch(dispatchers.io) {
            try {
                for (item in expired) {
                    coroutineContext.ensureActive()
                    if (!reservationSessionCurrent()) {
                        invalidateReservationSession()
                        return@launch
                    }
                    val share = item.poolShare ?: continue
                    val key = ReservationKey(item.id, share.code, share.expiresAt)
                    val stillPresent = mutableState.value.cart?.items.orEmpty().any {
                        it.id == key.itemId && it.poolShare?.code == key.poolCode && it.poolShare?.expiresAt == key.expiresAt
                    }
                    if (!stillPresent || !attemptedReservations.add(key)) continue
                    // Marca antes do transporte: timeout/503 nunca torna esta reserva elegível para repetição.
                    val result = if (removeExpiredPoolShare != null) {
                        val identity = reservationOwner ?: continue
                        removeExpiredPoolShare.invoke(item, identity)
                    } else {
                        removeCartItem(item)
                    }
                    coroutineContext.ensureActive()
                    if (!reservationSessionCurrent()) {
                        invalidateReservationSession()
                        return@launch
                    }
                    applyResult(result)
                    // O callback da CX atualiza os parâmetros mesmo quando a exclusão falha; o cache TTL compartilhado controla a atualização.
                    loadParameters()
                    if (result is AppResult.Success) {
                        // A CX atualiza o carrinho autoritativo após o callback de exclusão.
                        val refreshed = getCurrentCart()
                        coroutineContext.ensureActive()
                        if (!reservationSessionCurrent()) {
                            invalidateReservationSession()
                            return@launch
                        }
                        applyResult(refreshed)
                    }
                }
            } finally {
                if (reservationSessionCurrent()) {
                    setCartRequestRunning(false)
                    mutableState.dispatch(CartReservationAction.ExpirationRemovalFinished)
                }
            }
        }
    }
}

internal data class CartReservationSession(
    val identities: br.gov.caixa.loterias.feature.account.domain.port.AuthenticatedSessionIdentityProvider?,
    val changes: kotlinx.coroutines.flow.Flow<br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity?>?,
)
