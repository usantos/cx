package br.gov.caixa.loterias.feature.retail.domain.usecase

import br.gov.caixa.loterias.feature.retail.domain.model.RetailLocationEligibility
import br.gov.caixa.loterias.feature.retail.domain.port.RetailCountrySource
import kotlinx.coroutines.CancellationException

class CheckRetailLocation(private val countrySource: RetailCountrySource) : CheckRetailLocationUseCase {
    override suspend fun invoke(mockInsideBrazil: Boolean): RetailLocationEligibility {
        if (mockInsideBrazil) return RetailLocationEligibility.ALLOWED
        val country = try {
            countrySource.countryCode()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        }
        return when {
            country == null -> RetailLocationEligibility.UNAVAILABLE
            country.equals("BR", ignoreCase = true) -> RetailLocationEligibility.ALLOWED
            else -> RetailLocationEligibility.OUTSIDE_BRAZIL
        }
    }
}
