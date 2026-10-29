package br.gov.caixa.loterias.feature.account.domain.usecase

import br.gov.caixa.loterias.feature.account.domain.model.localizedMessage as messageText

import br.gov.caixa.loterias.core.common.result.appResultOf
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.account.domain.repository.AccountRepository

class RecalculateAuthorizedLimit(private val repository: AccountRepository) : RecalculateAuthorizedLimitUseCase {
    override suspend fun invoke() = appResultOf {
        repository.recalculateAuthorizedLimit().also { require(it >= BigDecimal.ZERO) { messageText("limite_invalido_cd553d") } }
    }
}
