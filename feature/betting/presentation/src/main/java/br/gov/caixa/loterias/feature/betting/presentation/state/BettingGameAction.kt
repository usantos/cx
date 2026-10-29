package br.gov.caixa.loterias.feature.betting.presentation.state

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.text.CapturedTextTemplate
import br.gov.caixa.loterias.core.domain.configuration.model.HomeSpecialContests
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedTemplate
import br.gov.caixa.loterias.feature.cart.domain.model.ShoppingCart

internal sealed interface BettingGameAction {
    data object DailyLimitCancelled : BettingGameAction
    data object DuplicateCancelled : BettingGameAction
    data class ClosedContestRejected(
        val contest: Int,
        val message: String = messageText("o_registro_de_apostas_individuai_b546d7", contest),
    ) : BettingGameAction
    data class IncompleteBetRejected(val message: String) : BettingGameAction
    data object DailyLimitRequired : BettingGameAction
    data object CartSubmissionStarted : BettingGameAction
    data class CartAdditionConfirmed(val game: LotteryGameParameters, val cart: ShoppingCart) : BettingGameAction
    data object FavoriteRequested : BettingGameAction
    data class FavoriteNameChanged(val value: String) : BettingGameAction
    data class FavoriteNameMissing(val message: String = messageText("favor_inserir_um_nome_para_sua_a_8a57b0")) : BettingGameAction
    data class FavoriteUnavailable(val message: String = messageText("nao_foi_possivel_salvar_a_aposta_eab8cf")) : BettingGameAction
    data object FavoriteSavingStarted : BettingGameAction
    data object FavoriteLoadingFinished : BettingGameAction
    data class FavoriteOverwriteRequired(val message: String) : BettingGameAction
    data class FavoriteFailed(val message: String) : BettingGameAction
    data object FavoriteCancelled : BettingGameAction
    data object FavoriteOverwriteStarted : BettingGameAction
    data object FavoriteOverwriteDismissed : BettingGameAction
    data class FavoriteSaved(val currentRevision: Boolean) : BettingGameAction
    data object ParametersLoading : BettingGameAction
    data object GameUnavailable : BettingGameAction
    data class ParametersLoaded(val game: LotteryGameParameters, val parameters: BettingParameters, val seed: BettingGameSeed) :
        BettingGameAction
    data object ComplementStarted : BettingGameAction
    data object NumbersStarted : BettingGameAction
    data object CartAdditionAcknowledged : BettingGameAction
    data object MessageDismissed : BettingGameAction
    data class ShakeSelectionGenerated(val selection: GeneratedBettingSelection) : BettingGameAction
    data class NumberToggled(
        val number: Int,
        val warn: Boolean,
        val maximumMessage: String = messageText("nao_e_possivel_selecionar_quanti_5a1fce"),
        val increasedMessage: String = messageText("voce_aumentou_a_quantidade_de_nu_2d08ee"),
    ) : BettingGameAction
    data class LuckyMonthSelected(val month: LuckyMonth) : BettingGameAction
    data object MirrorToggled : BettingGameAction
    data class CloverToggled(val clover: Int) : BettingGameAction
    data class CloverQuantitySelected(
        val quantity: Int,
        val warn: Boolean,
        val message: String = messageText("voce_aumentou_a_quantidade_de_tr_ad1edd"),
    ) : BettingGameAction
    data class NumberQuantitySelected(
        val quantity: Int,
        val warn: Boolean,
        val message: String = messageText("voce_aumentou_a_quantidade_de_nu_2d08ee"),
    ) : BettingGameAction
    data class RecurringDrawsSelected(val draws: Int) : BettingGameAction
    data class SpecialRecurringUnavailable(val message: String = messageText("por_ser_uma_modalidade_especial_d5f024")) : BettingGameAction
    data object SurpriseQuantityIncreased : BettingGameAction
    data object SurpriseQuantityDecreased : BettingGameAction
    data object SurpriseCloversIncreased : BettingGameAction
    data object SurpriseCloversDecreased : BettingGameAction
    data class ModeSelected(val mode: BetEntryMode) : BettingGameAction
    data object SelectionCleared : BettingGameAction
    data object CurrentStepCleared : BettingGameAction
    data class TeamQueryChanged(val value: String) : BettingGameAction
    data class TeamSelected(val team: SportsTeam) : BettingGameAction
    data class LotecaOutcomeToggled(
        val matchNumber: Int,
        val outcome: LotecaOutcome,
        val combinationMessage: String = messageText("essa_combinacao_de_simples_duplo_411b1c"),
        val maximumMessage: String = messageText("nao_e_possivel_selecionar_quanti_5a1fce"),
    ) : BettingGameAction
    data class LotogolScoreSelected(val matchNumber: Int, val home: Boolean, val score: Int) : BettingGameAction
    data class SuperSevenDigitToggled(
        val column: Int,
        val digit: Int,
        val messages: SuperSevenSelectionMessages = captureSuperSevenSelectionMessages(),
    ) : BettingGameAction
    data class FailureReceived(val error: AppError, val message: String) : BettingGameAction
    data class SpecialContestsLoaded(val contests: HomeSpecialContests) : BettingGameAction
    data class RecoveryOperationFailed(val message: String) : BettingGameAction

    /** Reproduz ações já geradas e persistidas; nunca repete efeitos ou seleção aleatória. */
    data class DraftCommitted(val actions: List<BettingGameAction>) : BettingGameAction

    /** Somente a fronteira de comandos persistentes pode restaurar este ponto de recuperação já confirmado. */
    data class RestoreConfirmedDraft(val checkpoint: BettingGameUiState) : BettingGameAction
    data class CheckpointRestored(val checkpoint: BettingGameUiState) : BettingGameAction
    data class RecoveryRejected(val message: String, val review: br.gov.caixa.loterias.feature.betting.domain.recovery.BettingCheckpoint?) :
        BettingGameAction
    data class DraftWriteFailed(val message: String) : BettingGameAction
}

internal data class BettingGameSeed(
    val initialNumbers: List<Int>,
    val initialClovers: List<Int>,
    val initialTeam: SportsTeam?,
    val initialLuckyMonth: LuckyMonth?,
    val initialLotecaSelections: List<LotecaMatchSelection>,
    val initialLotogolSelections: List<LotogolMatchSelection>,
    val initialSuperSevenColumns: List<List<Int>>,
    val initialRecurringDraws: Int,
    val initialMirror: Boolean,
)

internal data class SuperSevenSelectionMessages(
    val columnMinimum: CapturedTextTemplate,
    val columnMaximum: CapturedTextTemplate,
    val selectionMaximum: String,
)

private fun captureSuperSevenSelectionMessages() = SuperSevenSelectionMessages(
    localizedTemplate("voce_nao_pode_fazer_isso_pois_ne_0b0267"),
    localizedTemplate("para_esta_aposta_selecione_no_ma_343a7c"),
    messageText("nao_e_possivel_selecionar_uma_qu_0ede70")
)
