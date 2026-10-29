package br.gov.caixa.loterias.feature.betting.domain.usecase

import br.gov.caixa.loterias.feature.betting.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.core.common.time.ServerTimeSource
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.core.domain.configuration.usecase.GetHomeFeatureConfigurationUseCase
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.BetEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.model.isPoolOnlyWindow

class PrepareBetEntry(
    private val verifyAccess: VerifyAccountAccessUseCase,
    private val getParameters: GetBettingParametersUseCase,
    private val getFeatures: GetHomeFeatureConfigurationUseCase,
    private val serverTime: ServerTimeSource,
    private val isAuthenticated: () -> Boolean = { true },
) : PrepareBetEntryUseCase {
    override suspend fun invoke(request: BetEntryRequest): AppResult<BetEntryDecision> {
        if (request.modalityId <= 0) return AppResult.Failure(AppError.Validation(messageText("modalidade_invalida_964af4")))
        if (request.contestType?.isBlank() == true || request.contestNumber?.let { it <= 0 } == true) {
            return AppResult.Failure(AppError.Validation(messageText("concurso_invalido_2780ba")))
        }
        val destination = when (
            val result = if (isAuthenticated()) {
                verifyBettingAccount(
                    verifyAccess
                )
            } else {
                AppResult.Success(PostLoginDestination.HOME)
            }
        ) {
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
        // A seleção do carrossel não deve mudar silenciosamente para outro concurso após a atualização.
        val game = parameters.games.firstOrNull {
            it.modality.id == request.modalityId &&
                (request.contestType == null || it.contestType.equals(request.contestType, ignoreCase = true)) &&
                (request.contestNumber == null || it.contestNumber == request.contestNumber)
        }
        if (game == null) {
            // O legado cria um jogo temporário no carrossel com a disponibilidade de revenda quando faltam os parâmetros individuais.
            val pool = parameters.availablePoolModalities.firstOrNull {
                it.modalityId == request.modalityId && it.contestNumber != null &&
                    (request.contestType == null || it.contestType.equals(request.contestType, true)) &&
                    (request.contestNumber == null || it.contestNumber == request.contestNumber) &&
                    br.gov.caixa.loterias.core.common.time.LotteryDateTimes.parse("${it.resaleClosingDate} ${it.resaleClosingTime}")
                        ?.let { deadline -> serverTime.now() <= deadline } == true
            }
            if (pool != null) {
                val enabled = when (val result = getFeatures()) {
                    is AppResult.Success -> result.value.poolSalesEnabled
                    is AppResult.Failure -> return result
                }
                if (enabled) return AppResult.Failure(AppError.Validation(poolOnlyMessage(pool.contestNumber!!)))
            }
            return AppResult.Failure(AppError.Validation(messageText("modalidade_indisponivel_para_apo_59acb4")))
        }
        val features = when (val result = getFeatures()) {
            is AppResult.Success -> result.value
            is AppResult.Failure -> return result
        }
        if (isPoolOnlyWindow(game, parameters.availablePoolModalities, features.poolSalesEnabled, serverTime.now())) {
            return AppResult.Failure(
                AppError.Validation(
                    poolOnlyMessage(game.contestNumber)
                )
            )
        }
        // O legado permite abrir a simulação fora do período exclusivo de bolões.
        // O envio continua validando se o concurso aceita apostas individuais.
        return AppResult.Success(BetEntryDecision.Ready(game, parameters))
    }
}

private fun poolOnlyMessage(contest: Int) = messageText("o_registro_de_apostas_individuai_571554", contest) +
    messageText("mas_ainda_da_tempo_de_comprar_se_831645") +
    messageText("acesse_compre_seu_bolao_e_partic_9b96df")
