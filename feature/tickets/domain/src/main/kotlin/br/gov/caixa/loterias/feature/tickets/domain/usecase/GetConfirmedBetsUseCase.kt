package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsPage
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet

fun interface GetConfirmedBetsUseCase {
    suspend operator fun invoke(filters: Map<String, String>): AppResult<List<ConfirmedBet>>
    suspend fun page(filters: Map<String, String>): AppResult<ConfirmedBetsPage> = when (val result = invoke(filters)) {
        is AppResult.Success -> AppResult.Success(ConfirmedBetsPage(result.value))
        is AppResult.Failure -> result
    }
}
