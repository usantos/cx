package br.gov.caixa.loterias.feature.account.domain.usecase

import kotlinx.coroutines.flow.Flow
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionContent

fun interface ObserveAuthenticatedSessionContentUseCase {
    operator fun invoke(): Flow<AuthenticatedSessionContent?>
}
