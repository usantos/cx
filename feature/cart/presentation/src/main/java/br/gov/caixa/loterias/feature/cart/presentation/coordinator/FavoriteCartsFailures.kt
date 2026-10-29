package br.gov.caixa.loterias.feature.cart.presentation.coordinator

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.presentation.state.ReduxStateStore
import br.gov.caixa.loterias.feature.cart.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsAction
import br.gov.caixa.loterias.feature.cart.presentation.state.FavoriteCartsUiState

internal class FavoriteCartsFailures(private val mutableState: ReduxStateStore<FavoriteCartsUiState, FavoriteCartsAction>) {
    fun applyFailure(error: AppError, itemOnly: Boolean = false) =
        mutableState.dispatch(FavoriteCartsAction.Failed(error.userMessage(), itemOnly))

    private fun AppError.userMessage(): String = when (this) {
        AppError.Network -> messageText("nao_foi_possivel_carregar_os_car_ddd83b")
        is AppError.Validation -> reason.ifBlank { messageText("nao_foi_possivel_atualizar_o_car_80502e") }
        AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_ao_at_0c9f29")
    }
}
