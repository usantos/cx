package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.ZipCodeLocation

fun interface ResolveZipCodeUseCase {
    suspend operator fun invoke(zipCode: String): AppResult<ZipCodeLocation>
}
