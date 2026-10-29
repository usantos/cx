package br.gov.caixa.loterias.feature.betting.presentation.state
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
internal sealed interface FavoriteBetsAction {
    data object LoadingStarted : FavoriteBetsAction
    data class ParametersLoaded(val modalities: List<LotteryModality>, val parameters: BettingParameters) : FavoriteBetsAction
    data class ModalitySelected(val modalityId: Int?) : FavoriteBetsAction
    data object MoreLoading : FavoriteBetsAction
    data class MoreLoaded(val favorites: List<FavoriteBet>) : FavoriteBetsAction
    data class DeletionRequested(val favorite: FavoriteBet) : FavoriteBetsAction
    data object DeletionCancelled : FavoriteBetsAction
    data object DeletionStarted : FavoriteBetsAction
    data class Deleted(val remaining: List<FavoriteBet>) : FavoriteBetsAction
    data object ModalityCleared : FavoriteBetsAction
    data object SuspensionReceived : FavoriteBetsAction
    data object LoadingFinished : FavoriteBetsAction
    data object DailyLimitConfirmationRequired : FavoriteBetsAction
    data object DailyLimitConfirmationCancelled : FavoriteBetsAction
    data object DuplicateConfirmationRequired : FavoriteBetsAction
    data object DuplicateConfirmationCancelled : FavoriteBetsAction
    data object ConfirmationsCancelled : FavoriteBetsAction
    data class AdditionRequested(val favorite: FavoriteBet, val special: Boolean) : FavoriteBetsAction
    data object AdditionCancelled : FavoriteBetsAction
    data class ContestTypeChanged(val value: Int) : FavoriteBetsAction
    data class RecurringDrawsChanged(val value: Int) : FavoriteBetsAction
    data object MirrorToggled : FavoriteBetsAction
    data object AdditionStarted : FavoriteBetsAction
    data class RecoveryRequired(val message: String) : FavoriteBetsAction
    data object AddedToCart : FavoriteBetsAction
    data object AdditionConsumed : FavoriteBetsAction
    data object ErrorDismissed : FavoriteBetsAction
    data object FirstPageLoading : FavoriteBetsAction
    data class FirstPageLoaded(val favorites: List<FavoriteBet>, val filtered: Boolean) : FavoriteBetsAction
    data class Failed(val error: AppError, val message: String = captureFavoriteBetsFailure(error)) : FavoriteBetsAction
}

private fun captureFavoriteBetsFailure(error: AppError): String = when (error) {
    AppError.Network -> messageText("nao_foi_possivel_carregar_as_apo_e3b519")
    is AppError.Validation -> error.reason.ifBlank { messageText("nao_foi_possivel_atualizar_a_apo_711f0f") }
    AppError.Unexpected -> messageText("ocorreu_um_erro_inesperado_ao_ca_be2fa9")
}
