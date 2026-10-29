package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.domain.usecase.SaveCurrentCartFavoriteUseCase
import br.gov.caixa.loterias.feature.cart.domain.usecase.ValidateFavoriteCartNameUseCase
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.presentation.state.CartAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartFavoriteAction
import br.gov.caixa.loterias.feature.cart.presentation.state.CartUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class CartFavoritesCoordinator(
    private val mutableState: ReduxStateStore<CartUiState, CartAction>,
    private val viewModelScope: CoroutineScope,
    private val dispatchers: CoroutineDispatchers,
    private val validateFavoriteCartName: ValidateFavoriteCartNameUseCase?,
    private val saveCurrentCartFavorite: SaveCurrentCartFavoriteUseCase?,
    private val errorMessage: (AppError) -> String,
) {
    private fun AppError.userMessage(): String = errorMessage(this)

    fun requestFavorite() = mutableState.dispatch(CartFavoriteAction.Requested)

    fun updateFavoriteName(name: String) = mutableState.dispatch(CartFavoriteAction.NameChanged(name))

    fun submitFavoriteName() {
        if (mutableState.value.loading) return
        val name = mutableState.value.favoriteName.trim()
        if (name.isBlank()) {
            mutableState.dispatch(CartFavoriteAction.NameRejected(messageText("informe_o_nome_do_carrinho_favor_fe59d0")))
            return
        }
        mutableState.dispatch(CartFavoriteAction.ValidationStarted)
        viewModelScope.launch(dispatchers.io) {
            val validation = validateFavoriteCartName?.invoke(name) ?: AppResult.Success(Unit)
            when (validation) {
                is AppResult.Success -> saveFavorite(name)

                is AppResult.Failure -> when (validation.error) {
                    is AppError.Validation -> mutableState.dispatch(CartFavoriteAction.OverwriteRequested)
                    else -> mutableState.dispatch(CartFavoriteAction.ValidationFailed(validation.error.userMessage()))
                }
            }
        }
    }

    fun confirmFavoriteOverwrite() = viewModelScope.launch(dispatchers.io) {
        val name = mutableState.value.favoriteName.trim()
        mutableState.dispatch(CartFavoriteAction.OverwriteStarted)
        saveFavorite(name)
    }

    suspend fun saveFavorite(name: String) {
        when (val result = saveCurrentCartFavorite?.invoke(name) ?: AppResult.Success(Unit)) {
            is AppResult.Success -> mutableState.dispatch(CartFavoriteAction.Saved)
            is AppResult.Failure -> mutableState.dispatch(CartFavoriteAction.SaveFailed(result.error.userMessage()))
        }
    }
}
