package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.domain.contract.account.model.AuthenticatedSessionContent
import kotlinx.coroutines.flow.Flow

fun interface ObserveAuthenticatedSessionContentUseCase {
    operator fun invoke(): Flow<AuthenticatedSessionContent?>
}
