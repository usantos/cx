package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.ServerTimeSource
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.configuration.GetHomeFeatureConfigurationUseCase

sealed interface BetEntryDecision {
    data class Ready(val game: LotteryGameParameters, val parameters: BettingParameters? = null) : BetEntryDecision
    data class CompleteAccount(val destination: PostLoginDestination) : BetEntryDecision
}

data class BetEntryRequest(
    val modalityId: Int,
    val contestType: String? = null,
    val contestNumber: Int? = null,
)

fun interface PrepareBetEntryUseCase {
    suspend operator fun invoke(request: BetEntryRequest): AppResult<BetEntryDecision>

    /** Historical bet results request the current contest, not the historical one. */
    suspend operator fun invoke(modalityId: Int): AppResult<BetEntryDecision> = invoke(BetEntryRequest(modalityId))
}

class PrepareBetEntry(
    private val verifyAccess: VerifyAccountAccessUseCase,
    private val getParameters: GetBettingParametersUseCase,
    private val getFeatures: GetHomeFeatureConfigurationUseCase,
    private val serverTime: ServerTimeSource,
    private val isAuthenticated: () -> Boolean = { true },
) : PrepareBetEntryUseCase {
    override suspend fun invoke(request: BetEntryRequest): AppResult<BetEntryDecision> {
        if (request.modalityId <= 0) return AppResult.Failure(AppError.Validation("Modalidade inválida."))
        if (request.contestType?.isBlank() == true || request.contestNumber?.let { it <= 0 } == true) {
            return AppResult.Failure(AppError.Validation("Concurso inválido."))
        }
        val destination = when (val result = if (isAuthenticated()) verifyBettingAccount(verifyAccess) else AppResult.Success(PostLoginDestination.HOME)) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        if (destination != PostLoginDestination.HOME) {
            return AppResult.Success(BetEntryDecision.CompleteAccount(destination))
        }
        val parameters = when (val result = getParameters()) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        // A carousel selection must not silently switch to another contest after refresh.
        val game = parameters.games.firstOrNull {
            it.modality.id == request.modalityId &&
                (request.contestType == null || it.contestType.equals(request.contestType, ignoreCase = true)) &&
                (request.contestNumber == null || it.contestNumber == request.contestNumber)
        }
            ?: return AppResult.Failure(AppError.Validation("Modalidade indisponível para apostar no momento."))
        val features = when (val result = getFeatures()) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        if (isPoolOnlyWindow(game, parameters.availablePoolModalities, features.poolSalesEnabled, serverTime.now())) {
            return AppResult.Failure(AppError.Validation(
                "O registro de apostas individuais para o concurso ${game.contestNumber} foi encerrado. " +
                    "Mas ainda dá tempo de comprar seu bolão e concorrer a esse sorteio.\n" +
                    "Acesse \"Compre seu bolão\" e participe!",
            ))
        }
        if (!game.open) return AppResult.Failure(AppError.Validation(
            "O registro de apostas individuais para o concurso ${game.contestNumber} foi encerrado.",
        ))
        return AppResult.Success(BetEntryDecision.Ready(game, parameters))
    }
}
