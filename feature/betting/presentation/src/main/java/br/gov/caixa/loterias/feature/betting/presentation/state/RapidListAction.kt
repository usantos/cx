package br.gov.caixa.loterias.feature.betting.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart
internal sealed interface RapidListAction {
    data class ServiceRedirectConsumed(val expected: br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent) : RapidListAction
    data class ValidationRedirectConsumed(val expected: br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent) :
        RapidListAction
    data class CartReloaded(val cart: ShoppingCart) : RapidListAction
    data class RecoveryRequired(val message: String) : RapidListAction
    data object CartOperationStarted : RapidListAction
    data class CartLoaded(val cart: ShoppingCart) : RapidListAction
    data class ServiceFailureReceived(val presentation: ServiceFailurePresentation) : RapidListAction
    data object LoadingStarted : RapidListAction
    data class EmptyCartRejected(val message: String = messageText("seu_carrinho_esta_vazio_6e7dbb")) : RapidListAction
    data object PaymentReady : RapidListAction
    data class ValidationRejected(val presentation: ServiceFailurePresentation) : RapidListAction
    data class ServiceNoticeDismissed(val expected: ServiceNoticeUiState) : RapidListAction
    data class ServiceNoticeAccepted(val expected: ServiceNoticeUiState, val presentation: ServiceFailurePresentation) : RapidListAction
    data object ErrorDismissed : RapidListAction
    data object PaymentReadyConsumed : RapidListAction
    data class ValidationNoticeDismissed(val expected: ServiceNoticeUiState) : RapidListAction
    data class ValidationNoticeAccepted(val expected: ServiceNoticeUiState, val presentation: ServiceFailurePresentation) : RapidListAction
    data class Failed(val error: AppError, val message: String = captureRapidListFailure(error)) : RapidListAction
}

private fun captureRapidListFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_atualizar_as_ap_f4191d")
    is AppError.Validation -> error.reason.ifBlank { messageText("nao_foi_possivel_atualizar_o_rap_0cd7c5") }
    AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_no_ra_2ee739")
}
