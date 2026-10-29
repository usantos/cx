package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.SessionSignOutResult

fun interface SignOutSessionUseCase {
    suspend operator fun invoke(): SessionSignOutResult
}
