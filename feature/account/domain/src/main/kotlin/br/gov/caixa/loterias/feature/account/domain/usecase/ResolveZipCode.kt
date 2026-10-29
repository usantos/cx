package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.account.domain.model.localizedMessage as messageText
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository

class ResolveZipCode(private val repository: AccountRepository) : ResolveZipCodeUseCase {
    override suspend fun invoke(zipCode: String) = appResultOf {
        val normalized = zipCode.filter(Char::isDigit)
        require(normalized.length == 8) { messageText("cep_invalido_8b6688") }
        repository.locationForZipCode(normalized)
    }
}
