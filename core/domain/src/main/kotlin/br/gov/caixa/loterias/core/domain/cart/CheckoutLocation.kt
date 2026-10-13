package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf

data class CheckoutLocationAccess(
    val providerEnabled: Boolean,
    val fineGranted: Boolean,
    val coarseGranted: Boolean,
    val permissionRequestCount: Int,
)

enum class CheckoutLocationStatus {
    IN_BRAZIL, PERMISSION_REQUIRED, PERMISSION_SETTINGS_REQUIRED, PROVIDER_DISABLED, OUTSIDE_BRAZIL, UNAVAILABLE,
}

interface CheckoutLocationSource {
    fun access(): CheckoutLocationAccess
    suspend fun countryCode(): String?
    fun recordPermissionRequest()
}

fun interface CheckCheckoutLocationUseCase {
    suspend operator fun invoke(): AppResult<CheckoutLocationStatus>
}

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

fun interface RecordCheckoutLocationRequestUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}

class RecordCheckoutLocationRequest(private val source: CheckoutLocationSource) : RecordCheckoutLocationRequestUseCase {
    override suspend fun invoke() = appResultOf { source.recordPermissionRequest() }
}
