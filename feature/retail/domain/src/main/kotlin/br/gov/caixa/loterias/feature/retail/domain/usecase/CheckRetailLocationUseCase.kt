package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.feature.retail.domain.model.RetailLocationEligibility

interface CheckRetailLocationUseCase {
    suspend operator fun invoke(mockInsideBrazil: Boolean = false): RetailLocationEligibility
}
