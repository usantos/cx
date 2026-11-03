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
internal class FavoriteCartsImport(
    private val mutableState: ReduxStateStore<FavoriteCartsUiState, FavoriteCartsAction>,
    private val viewModelScope: kotlinx.coroutines.CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val cartId: Long?,
    private val failures: FavoriteCartsFailures,
    private val services: FavoriteCartsImportServices,
    private val isSessionCurrent: () -> Boolean,
    private val betsCache: MutableMap<Long, List<FavoriteCartBet>>,
    private val importedFavoriteCartIds: MutableSet<Long>,
    private val canStartOperation: () -> Boolean,
) {
    private val importMutex = Mutex()
    private var pendingAllowsExisting = false

    fun requestImport(id: Long? = cartId) {
        val favoriteId = id ?: return
        viewModelScope.launch(dispatchers.io) {
            if (!isSessionCurrent() || !canStartOperation()) return@launch
            try {
                val alreadyAdded = favoriteId in importedFavoriteCartIds || services.wasFavoriteCartAdded(favoriteId)
                if (!isSessionCurrent() || !canStartOperation()) return@launch
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
        if (!isSessionCurrent() || !canStartOperation()) return
        val loadedBets = if (favoriteId == cartId) mutableState.value.bets else betsCache[favoriteId]
        if (loadedBets == null) {
            viewModelScope.launch(dispatchers.io) {
                if (!isSessionCurrent() || !canStartOperation()) return@launch
                mutableState.dispatch(FavoriteCartsAction.CartLoadStarted(favoriteId))
                val result = services.getFavoriteCartBets(favoriteId)
                if (!isSessionCurrent()) return@launch
                if (!canStartOperation()) {
                    mutableState.dispatch(FavoriteCartsAction.ImportBetsLoaded)
                    return@launch
                }
                when (result) {
                    is AppResult.Success -> {
                        betsCache[favoriteId] = result.value
                        mutableState.dispatch(FavoriteCartsAction.ImportBetsLoaded)
                        if (canStartOperation()) evaluateImport(favoriteId, result.value, allowExisting)
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
                if (!isSessionCurrent() || !canStartOperation()) return@withLock
                if (!allowExisting && favoriteId in importedFavoriteCartIds) {
                    mutableState.dispatch(FavoriteCartsAction.ExistingImportDetected(favoriteId))
                    return@withLock
                }
                val before = mutableState.value
                mutableState.dispatch(FavoriteCartsAction.ImportStarted)
                try {
                    // Uma intenção conservadora persistente precede a mutação remota. Em caso de resultado
                    // incerto, preserva o aviso existente de confirmação explícita após a reinicialização.
                    services.markFavoriteCartAdded(favoriteId)
                    if (!isSessionCurrent()) return@withLock
                    importedFavoriteCartIds += favoriteId
                    if (!canStartOperation()) {
                        mutableState.dispatch(
                            FavoriteCartsAction.ImportPreparationCancelled(
                                before.specialSelectionRequired,
                                before.existingCartConfirmationRequired
                            )
                        )
                        return@withLock
                    }
                    val result = services.addFavoriteCartToCurrent(favoriteId, selection)
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

internal class FavoriteCartsImportServices(
    val getFavoriteCartBets: GetFavoriteCartBetsUseCase,
    val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase,
    val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase,
    val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase,
)
