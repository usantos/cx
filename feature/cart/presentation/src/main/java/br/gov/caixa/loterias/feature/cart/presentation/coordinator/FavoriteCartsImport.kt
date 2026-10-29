package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.model.FavoriteCartBet
import br.gov.caixa.loterias.feature.cart.domain.usecase.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.WasFavoriteCartAddedUseCase
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// A coordenação dos destinos mantém explícitos o estado compartilhado existente e as portas das funcionalidades testáveis de forma independente.
@Suppress("LongParameterList")
internal class FavoriteCartsImport(
    private val mutableState: ReduxStateStore<FavoriteCartsUiState, FavoriteCartsAction>,
    private val viewModelScope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val cartId: Long?,
    private val failures: FavoriteCartsFailures,
    private val getFavoriteCartBets: GetFavoriteCartBetsUseCase,
    private val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase,
    private val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase,
    private val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase,
    private val isSessionCurrent: () -> Boolean,
    private val betsCache: MutableMap<Long, List<FavoriteCartBet>>,
    private val importedFavoriteCartIds: MutableSet<Long>,
) {
    private val importMutex = Mutex()
    private var pendingAllowsExisting = false

    fun requestImport(id: Long? = cartId) {
        val favoriteId = id ?: return
        viewModelScope.launch(dispatchers.io) {
            if (!isSessionCurrent()) return@launch
            try {
                val alreadyAdded = favoriteId in importedFavoriteCartIds || wasFavoriteCartAdded(favoriteId)
                if (!isSessionCurrent()) return@launch
                if (alreadyAdded) {
                    mutableState.dispatch(FavoriteCartsAction.ExistingImportDetected(favoriteId))
                } else {
                    prepareImport(favoriteId)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (isSessionCurrent()) failures.applyFailure(AppError.Unexpected, itemOnly = true)
            }
        }
    }

    fun confirmExistingCartImport() {
        val favoriteId = mutableState.value.pendingImportCartId ?: return
        mutableState.dispatch(FavoriteCartsAction.ExistingImportConfirmed)
        prepareImport(favoriteId, allowExisting = true)
    }

    private fun prepareImport(favoriteId: Long, allowExisting: Boolean = false) {
        if (!isSessionCurrent()) return
        val loadedBets = if (favoriteId == cartId) mutableState.value.bets else betsCache[favoriteId]
        if (loadedBets == null) {
            viewModelScope.launch(dispatchers.io) {
                mutableState.dispatch(FavoriteCartsAction.CartLoadStarted(favoriteId))
                if (!isSessionCurrent()) return@launch
                val result = getFavoriteCartBets(favoriteId)
                if (!isSessionCurrent()) return@launch
                when (result) {
                    is AppResult.Success -> {
                        betsCache[favoriteId] = result.value
                        mutableState.dispatch(FavoriteCartsAction.ImportBetsLoaded)
                        evaluateImport(favoriteId, result.value, allowExisting)
                    }

                    is AppResult.Failure -> failures.applyFailure(result.error, itemOnly = true)
                }
            }
            return
        }
        evaluateImport(favoriteId, loadedBets, allowExisting)
    }

    private fun evaluateImport(favoriteId: Long, loadedBets: List<FavoriteCartBet>, allowExisting: Boolean) {
        val hasNormal = loadedBets.any { !it.special }
        val hasSpecial = loadedBets.any(FavoriteCartBet::special)
        pendingAllowsExisting = allowExisting
        if (hasNormal && hasSpecial) {
            val regularName = loadedBets.first { !it.special }.modality
            val specialName = loadedBets.first(FavoriteCartBet::special).modality
            mutableState.dispatch(FavoriteCartsAction.SpecialSelectionRequested(favoriteId, regularName, specialName))
        } else {
            import(favoriteId, 0, allowExisting)
        }
    }

    fun selectSpecialMode(selection: Int) {
        val favoriteId = mutableState.value.pendingImportCartId ?: cartId ?: return
        import(favoriteId, selection, pendingAllowsExisting)
    }

    private fun import(favoriteId: Long, selection: Int, allowExisting: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            importMutex.withLock {
                if (!isSessionCurrent()) return@withLock
                if (!allowExisting && favoriteId in importedFavoriteCartIds) {
                    mutableState.dispatch(FavoriteCartsAction.ExistingImportDetected(favoriteId))
                    return@withLock
                }
                mutableState.dispatch(FavoriteCartsAction.ImportStarted)
                try {
                    // Uma intenção conservadora persistente precede a mutação remota. Em caso de resultado
                    // incerto, preserva o aviso existente de confirmação explícita após a reinicialização.
                    markFavoriteCartAdded(favoriteId)
                    if (!isSessionCurrent()) return@withLock
                    importedFavoriteCartIds += favoriteId
                    val result = addFavoriteCartToCurrent(favoriteId, selection)
                    if (!isSessionCurrent()) return@withLock
                    when (result) {
                        is AppResult.Success -> mutableState.dispatch(FavoriteCartsAction.Imported)
                        is AppResult.Failure -> failures.applyFailure(result.error)
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    if (isSessionCurrent()) failures.applyFailure(AppError.Unexpected)
                }
            }
        }
    }
}
