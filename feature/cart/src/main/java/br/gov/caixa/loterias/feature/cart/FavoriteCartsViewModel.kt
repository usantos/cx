package br.gov.caixa.loterias.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.AddFavoriteCartToCurrentUseCase
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCartBetUseCase
import br.gov.caixa.loterias.core.domain.cart.DeleteFavoriteCartUseCase
import br.gov.caixa.loterias.core.domain.cart.FavoriteCart
import br.gov.caixa.loterias.core.domain.cart.FavoriteCartBet
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCartBetsUseCase
import br.gov.caixa.loterias.core.domain.cart.GetFavoriteCartsUseCase
import br.gov.caixa.loterias.core.domain.cart.MarkFavoriteCartAddedUseCase
import br.gov.caixa.loterias.core.domain.cart.WasFavoriteCartAddedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoriteCartsUiState(
    val loading: Boolean = true,
    val carts: List<FavoriteCart> = emptyList(),
    val bets: List<FavoriteCartBet> = emptyList(),
    val expandedCartId: Long? = null,
    val expandedBets: List<FavoriteCartBet> = emptyList(),
    val loadingCartId: Long? = null,
    val pendingImportCartId: Long? = null,
    val regularModalityName: String = "Concurso regular",
    val specialModalityName: String = "Concurso especial",
    val pendingCartRemoval: Long? = null,
    val pendingBetRemoval: Long? = null,
    val specialSelectionRequired: Boolean = false,
    val existingCartConfirmationRequired: Boolean = false,
    val imported: Boolean = false,
    val cartDeleted: Boolean = false,
    val errorMessage: String? = null,
)

class FavoriteCartsViewModel(
    private val cartId: Long?,
    private val getFavoriteCarts: GetFavoriteCartsUseCase,
    private val getFavoriteCartBets: GetFavoriteCartBetsUseCase,
    private val deleteFavoriteCart: DeleteFavoriteCartUseCase,
    private val deleteFavoriteCartBet: DeleteFavoriteCartBetUseCase,
    private val addFavoriteCartToCurrent: AddFavoriteCartToCurrentUseCase,
    private val wasFavoriteCartAdded: WasFavoriteCartAddedUseCase = WasFavoriteCartAddedUseCase { false },
    private val markFavoriteCartAdded: MarkFavoriteCartAddedUseCase = MarkFavoriteCartAddedUseCase { },
    private val dispatchers: CoroutineDispatchers = DefaultCoroutineDispatchers,
    autoLoad: Boolean = true,
    private val isSessionCurrent: () -> Boolean = { true },
) : ViewModel() {
    private val mutableState = MutableStateFlow(FavoriteCartsUiState(loading = autoLoad))
    val state = mutableState.asStateFlow()
    private val betsCache = mutableMapOf<Long, List<FavoriteCartBet>>()
    private val importedFavoriteCartIds = mutableSetOf<Long>()

    init { if (autoLoad) load() }

    fun load() = viewModelScope.launch(dispatchers.io) {
        if (!isSessionCurrent()) return@launch
        mutableState.update { it.copy(loading = true, errorMessage = null) }
        if (cartId == null) applyCarts(getFavoriteCarts()) else applyBets(getFavoriteCartBets(cartId))
    }

    fun refreshAfterDetail() {
        if (!isSessionCurrent()) return
        betsCache.clear()
        mutableState.update { it.copy(expandedCartId = null, expandedBets = emptyList()) }
        load()
    }

    fun toggleCart(id: Long) {
        if (!isSessionCurrent()) return
        if (state.value.expandedCartId == id) {
            mutableState.update { it.copy(expandedCartId = null, expandedBets = emptyList()) }
            return
        }
        betsCache[id]?.let { bets ->
            mutableState.update { it.copy(expandedCartId = id, expandedBets = bets, errorMessage = null) }
            return
        }
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loadingCartId = id, errorMessage = null) }
            if (!isSessionCurrent()) return@launch
            val result = getFavoriteCartBets(id)
            if (!isSessionCurrent()) return@launch
            when (result) {
                is AppResult.Success -> {
                    betsCache[id] = result.value
                    mutableState.update {
                        it.copy(
                            loadingCartId = null,
                            expandedCartId = id,
                            expandedBets = result.value,
                            errorMessage = null,
                        )
                    }
                }
                is AppResult.Failure -> applyFailure(result.error, itemOnly = true)
            }
        }
    }

    fun requestCartRemoval(id: Long) = mutableState.update { it.copy(pendingCartRemoval = id) }
    fun requestBetRemoval(id: Long) = mutableState.update { it.copy(pendingBetRemoval = id) }
    fun dismissConfirmation() = mutableState.update {
        it.copy(
            pendingCartRemoval = null,
            pendingBetRemoval = null,
            pendingImportCartId = null,
            specialSelectionRequired = false,
            existingCartConfirmationRequired = false,
        )
    }

    fun confirmCartRemoval() {
        val id = state.value.pendingCartRemoval ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, pendingCartRemoval = null, errorMessage = null) }
            when (val result = deleteFavoriteCart(id)) {
                is AppResult.Success -> {
                    betsCache.remove(id)
                    mutableState.update {
                        it.copy(
                            loading = false,
                            carts = result.value,
                            expandedCartId = if (it.expandedCartId == id) null else it.expandedCartId,
                            expandedBets = if (it.expandedCartId == id) emptyList() else it.expandedBets,
                            errorMessage = null,
                        )
                    }
                }
                is AppResult.Failure -> applyFailure(result.error)
            }
        }
    }

    fun confirmBetRemoval() {
        val id = state.value.pendingBetRemoval ?: return
        val favoriteId = cartId ?: return
        viewModelScope.launch(dispatchers.io) {
            mutableState.update { it.copy(loading = true, pendingBetRemoval = null, errorMessage = null) }
            if (state.value.bets.size <= 1) {
                when (val result = deleteFavoriteCart(favoriteId)) {
                    is AppResult.Success -> mutableState.update {
                        it.copy(loading = false, bets = emptyList(), cartDeleted = true, errorMessage = null)
                    }
                    is AppResult.Failure -> applyFailure(result.error)
                }
            } else {
                applyBets(deleteFavoriteCartBet(favoriteId, id))
            }
        }
    }

    fun requestImport(id: Long? = cartId) {
        val favoriteId = id ?: return
        if (favoriteId in importedFavoriteCartIds || wasFavoriteCartAdded(favoriteId)) {
            mutableState.update {
                it.copy(pendingImportCartId = favoriteId, existingCartConfirmationRequired = true)
            }
            return
        }
        prepareImport(favoriteId)
    }

    fun confirmExistingCartImport() {
        val favoriteId = state.value.pendingImportCartId ?: return
        mutableState.update { it.copy(existingCartConfirmationRequired = false) }
        prepareImport(favoriteId)
    }

    private fun prepareImport(favoriteId: Long) {
        if (!isSessionCurrent()) return
        val loadedBets = if (favoriteId == cartId) state.value.bets else betsCache[favoriteId]
        if (loadedBets == null) {
            viewModelScope.launch(dispatchers.io) {
                mutableState.update { it.copy(loadingCartId = favoriteId, errorMessage = null) }
                if (!isSessionCurrent()) return@launch
                val result = getFavoriteCartBets(favoriteId)
                if (!isSessionCurrent()) return@launch
                when (result) {
                    is AppResult.Success -> {
                        betsCache[favoriteId] = result.value
                        mutableState.update { it.copy(loadingCartId = null) }
                        evaluateImport(favoriteId, result.value)
                    }
                    is AppResult.Failure -> applyFailure(result.error, itemOnly = true)
                }
            }
            return
        }
        evaluateImport(favoriteId, loadedBets)
    }

    private fun evaluateImport(favoriteId: Long, loadedBets: List<FavoriteCartBet>) {
        val hasNormal = loadedBets.any { !it.special }
        val hasSpecial = loadedBets.any(FavoriteCartBet::special)
        if (hasNormal && hasSpecial) {
            val regularName = loadedBets.first { !it.special }.modality
            val specialName = loadedBets.first(FavoriteCartBet::special).modality
            mutableState.update {
                it.copy(
                    pendingImportCartId = favoriteId,
                    regularModalityName = regularName,
                    specialModalityName = specialName,
                    specialSelectionRequired = true,
                )
            }
        } else {
            import(favoriteId, 0)
        }
    }

    fun selectSpecialMode(selection: Int) {
        val favoriteId = state.value.pendingImportCartId ?: cartId ?: return
        import(favoriteId, selection)
    }

    private fun import(favoriteId: Long, selection: Int) {
        viewModelScope.launch(dispatchers.io) {
            mutableState.update {
                it.copy(
                    loading = true,
                    specialSelectionRequired = false,
                    existingCartConfirmationRequired = false,
                    errorMessage = null,
                )
            }
            when (val result = addFavoriteCartToCurrent(favoriteId, selection)) {
                is AppResult.Success -> {
                    importedFavoriteCartIds += favoriteId
                    markFavoriteCartAdded(favoriteId)
                    mutableState.update {
                        it.copy(loading = false, pendingImportCartId = null, imported = true)
                    }
                }
                is AppResult.Failure -> applyFailure(result.error)
            }
        }
    }

    fun consumeImported() = mutableState.update { it.copy(imported = false) }

    private fun applyCarts(result: AppResult<List<FavoriteCart>>) = if (!isSessionCurrent()) Unit else when (result) {
        is AppResult.Success -> mutableState.update {
            it.copy(loading = false, carts = result.value, errorMessage = null)
        }
        is AppResult.Failure -> applyFailure(result.error)
    }

    private fun applyBets(result: AppResult<List<FavoriteCartBet>>) = if (!isSessionCurrent()) Unit else when (result) {
        is AppResult.Success -> mutableState.update {
            it.copy(loading = false, bets = result.value, errorMessage = null)
        }
        is AppResult.Failure -> applyFailure(result.error)
    }

    private fun applyFailure(error: AppError, itemOnly: Boolean = false) = mutableState.update {
        it.copy(
            loading = if (itemOnly) it.loading else false,
            loadingCartId = null,
            errorMessage = error.userMessage(),
        )
    }

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> "Não foi possível carregar os carrinhos favoritos. Verifique sua internet."
        is AppError.Validation -> reason.ifBlank { "Não foi possível atualizar o carrinho favorito." }
        AppError.Unexpected -> "Ocorreu um erro inesperado ao atualizar o carrinho favorito."
    }
}
