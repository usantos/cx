package br.gov.caixa.loterias.feature.tickets.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration

fun interface GetConfirmedBetsConfigurationUseCase {
    suspend operator fun invoke(): AppResult<ConfirmedBetsConfiguration>
}
