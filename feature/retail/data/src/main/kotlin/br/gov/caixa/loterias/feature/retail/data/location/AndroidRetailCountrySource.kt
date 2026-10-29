package br.gov.caixa.loterias.feature.retail.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.feature.retail.domain.port.RetailCountrySource
import java.util.Locale
import kotlinx.coroutines.withContext

class AndroidRetailCountrySource(
    private val context: Context,
    private val dispatchers: CoroutineDispatchers,
) : RetailCountrySource {
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override suspend fun countryCode(): String? = withContext(dispatchers.io) {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = manager.getProviders(true).mapNotNull { provider ->
            runCatching { manager.getLastKnownLocation(provider) }.getOrNull()
        }.minByOrNull { it.accuracy } ?: return@withContext null
        Geocoder(context, Locale.getDefault())
            .getFromLocation(location.latitude, location.longitude, 1)
            ?.firstOrNull()?.countryCode
    }
}
