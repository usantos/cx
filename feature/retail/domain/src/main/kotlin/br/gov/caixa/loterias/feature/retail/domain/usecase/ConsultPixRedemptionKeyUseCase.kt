package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary

fun interface ConsultPixRedemptionKeyUseCase {
    suspend operator fun invoke(betId: Long, key: String): AppResult<PixBeneficiary>
}
