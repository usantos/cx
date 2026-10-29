package br.gov.caixa.loterias.feature.cart.domain.usecase

import br.gov.caixa.loterias.core.common.result.appResultOf
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationStatus
import br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource

class CheckCheckoutLocation(private val source: CheckoutLocationSource) : CheckCheckoutLocationUseCase {
    override suspend fun invoke() = appResultOf {
        val access = source.access()
        when {
            !access.providerEnabled -> CheckoutLocationStatus.PROVIDER_DISABLED
            !access.fineGranted || !access.coarseGranted ->
                if (access.permissionRequestCount >= 2) CheckoutLocationStatus.PERMISSION_SETTINGS_REQUIRED
                else CheckoutLocationStatus.PERMISSION_REQUIRED
            else -> when (val country = source.countryCode()) {
                null, "" -> CheckoutLocationStatus.UNAVAILABLE
                else -> if (country.equals("BR", ignoreCase = true)) CheckoutLocationStatus.IN_BRAZIL
                    else CheckoutLocationStatus.OUTSIDE_BRAZIL
            }
        }
    }
}
