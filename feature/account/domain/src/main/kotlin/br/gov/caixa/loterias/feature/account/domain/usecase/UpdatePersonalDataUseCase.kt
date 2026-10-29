package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.PersonalDataUpdate
import br.gov.caixa.loterias.feature.account.domain.model.AccountProfile

fun interface UpdatePersonalDataUseCase {
    suspend operator fun invoke(update: PersonalDataUpdate, profile: AccountProfile): AppResult<Unit>
}
