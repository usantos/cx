package br.gov.caixa.loterias.feature.home.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText

internal sealed interface HomeStateAction {
    data object ParameterRetryDismissed : HomeLoadingAction
    data object UnavailableParametersDismissed : HomeLoadingAction
    data object ParametersLoading : HomeLoadingAction
    data class EmptyParametersReceived(
        val retryMessage: String?,
        val message: String = messageText("nenhuma_modalidade_disponivel_no_8d67af"),
    ) : HomeLoadingAction
    data class ParametersReceived(
        val parameters: br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters,
        val features: HomeFeatureConfiguration,
        val combos: List<br.gov.caixa.loterias.feature.betting.domain.model.BetCombo>,
        val resaleOffers: List<br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability>,
    ) : HomeLoadingAction
    data class ParametersFailed(val retryMessage: String?, val message: String = messageText("nao_foi_possivel_carregar_as_mod_7d682d")) :
        HomeLoadingAction
    data object ParametersLoadingFinished : HomeLoadingAction
    data class SessionContentObserved(val content: br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent?) :
        HomeSessionAction
    data object LocalCartReviewAcknowledged : HomeSessionAction
    data object PaymentNoticeAcknowledged : HomeSessionAction
    data object BetEntryStarted : HomeEntryAction
    data class BetEntryPrepared(val decision: br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision) : HomeEntryAction
    data object BetEntryConsumed : HomeEntryAction
    data object BetEntryErrorDismissed : HomeEntryAction
    data object PoolEntryStarted : HomeEntryAction
    data class PoolEntryPrepared(val decision: br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision) : HomeEntryAction
    data object PoolEntryConsumed : HomeEntryAction
    data object OtherBettingEntryStarted : HomeEntryAction
    data class OtherBettingEntryPrepared(val decision: br.gov.caixa.loterias.feature.betting.domain.model.HomeBettingDecision) :
        HomeEntryAction
    data object OtherBettingEntryConsumed : HomeEntryAction
    data class ExternalBettingFailed(val message: String = messageText("nao_foi_possivel_abrir_o_navegad_b731a5")) : HomeEntryAction
    data class BetEntryFailed(val error: AppError, val message: String = captureHomeEntryFailure(error)) : HomeEntryAction
    data object CartLoading : HomeContentAction
    data class CartReceived(val requestCurrent: Boolean, val cart: br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart) :
        HomeContentAction
    data class CartFailed(val requestCurrent: Boolean) : HomeContentAction
    data object SocialTransfersLoading : HomeContentAction
    data class SocialTransfersReceived(val transfers: br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers) :
        HomeContentAction
    data object SocialTransfersFailed : HomeContentAction
    data object HeaderActionsRevealed : HomeSessionAction
}

private fun captureHomeEntryFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_acessar_o_servi_856170")
    is AppError.Validation -> error.reason.ifBlank { messageText("nao_foi_possivel_concluir_a_oper_c13b9b") }
    AppError.Unexpected -> messageText("nao_foi_possivel_concluir_a_oper_29014f")
}
