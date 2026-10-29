package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.PostLoginAccessResult

fun interface VerifyPostLoginAccessUseCase {
    suspend operator fun invoke(): PostLoginAccessResult
}
