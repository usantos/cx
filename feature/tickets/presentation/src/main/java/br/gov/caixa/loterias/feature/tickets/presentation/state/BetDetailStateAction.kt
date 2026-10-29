package br.gov.caixa.loterias.feature.tickets.presentation.state
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import br.gov.caixa.loterias.feature.tickets.presentation.formatting.localizedMessage as messageText

internal sealed interface BetDetailStateAction {
    data class DetailsLoaded(val bet: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet) : BetDetailDetailsAction
    data class RecoveryFailed(val bet: br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet, val message: String) :
        BetDetailDetailsAction
    data object DetailsLoading : BetDetailDetailsAction
    data class DetailsFailed(val message: String) : BetDetailDetailsAction
    data class ServiceRejected(val failure: br.gov.caixa.loterias.core.domain.service.model.ServiceFailure) : BetDetailDetailsAction
    data class DrawResultsReceived(val results: List<br.gov.caixa.loterias.feature.results.domain.model.DrawResult>) :
        BetDetailDetailsAction
    data object ServiceFailureDismissed : BetDetailDetailsAction
    data object FavoriteEditorToggled : BetDetailFavoriteAction
    data class FavoriteNameChanged(val value: String) : BetDetailFavoriteAction
    data object FavoriteValidationStarted : BetDetailFavoriteAction
    data class FavoriteNameRejected(val message: String = messageText("por_favor_insira_o_nome_da_apost_ca54f1")) : BetDetailFavoriteAction
    data object FavoriteSavingStarted : BetDetailFavoriteAction
    data class FavoriteOverwriteRequired(val message: String) : BetDetailFavoriteAction
    data class FavoriteValidationFailed(val message: String) : BetDetailFavoriteAction
    data object FavoriteOverwriteConfirmed : BetDetailFavoriteAction
    data object FavoriteOverwriteDismissed : BetDetailFavoriteAction
    data object FavoriteSaved : BetDetailFavoriteAction
    data class FavoriteSaveFailed(val message: String) : BetDetailFavoriteAction
    data class ReceiptUnavailable(val message: String = messageText("comprovante_indisponivel_para_es_829b02")) : BetDetailReceiptAction
    data object ReceiptLoading : BetDetailReceiptAction
    data object ReceiptDelivered : BetDetailReceiptAction
    data class ReceiptFailed(val message: String) : BetDetailReceiptAction
    data class ReceiptOpenFailed(val message: String = messageText("nao_foi_possivel_abrir_o_comprov_1e8165")) : BetDetailReceiptAction
    data object CartAdditionDismissed : BetDetailRepeatAction
    data class RepeatSelectionMissing(val message: String = messageText("nao_foi_possivel_recuperar_os_da_cb8122")) : BetDetailRepeatAction
    data object RepeatLoading : BetDetailRepeatAction
    data class RepeatParametersFailed(val message: String) : BetDetailRepeatAction
    data class RepeatContestSelected(val index: Int) : BetDetailRepeatAction
    data object RepeatContestConfirmed : BetDetailRepeatAction
    data object RepeatContestDismissed : BetDetailRepeatAction
    data object RepeatConversionConfirmed : BetDetailRepeatAction
    data class RepeatUnavailable(val message: String = messageText("modalidade_nao_disponivel_no_mom_5f4037")) : BetDetailRepeatAction
    data class RepeatConversionRequired(val option: br.gov.caixa.loterias.feature.tickets.presentation.state.RepeatContestOption) :
        BetDetailRepeatAction
    data class RepeatContestsReceived(
        val available: List<br.gov.caixa.loterias.feature.tickets.presentation.state.RepeatContestOption>,
        val selected: Int,
    ) : BetDetailRepeatAction
    data object RepeatAddedToCart : BetDetailRepeatAction
    data class RepeatAdditionFailed(val message: String) : BetDetailRepeatAction
    data object InitialDetailsLoading : BetDetailDetailsAction
    data object ErrorDismissed : BetDetailDetailsAction
}
