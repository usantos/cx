package br.gov.caixa.loterias.apostas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import br.gov.caixa.loterias.core.common.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.cart.CheckoutLocationAccess
import br.gov.caixa.loterias.core.domain.cart.CheckoutLocationSource
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/** Platform-only boundary. Coordinates are transient and never logged or persisted. */
internal class AndroidCheckoutLocationSource(
    private val context: Context,
    private val dispatchers: CoroutineDispatchers,
) : CheckoutLocationSource {
    private val preferences = context.getSharedPreferences(REQUEST_COUNT, Context.MODE_PRIVATE)
    private val manager: LocationManager
        get() = requireNotNull(context.getSystemService(LocationManager::class.java))

    override fun access() = CheckoutLocationAccess(
        providerEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER),
        fineGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED,
        coarseGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED,
        permissionRequestCount = preferences.getInt(REQUEST_COUNT, 0),
    )

    override fun recordPermissionRequest() {
        val count = preferences.getInt(REQUEST_COUNT, 0).coerceIn(0, 2)
        check(preferences.edit().putInt(REQUEST_COUNT, (count + 1).coerceAtMost(2)).commit())
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override suspend fun countryCode(): String? = withContext(dispatchers.io) {
        val providers = manager.getProviders(true)
        val cached = providers.mapNotNull { manager.getLastKnownLocation(it) }.minByOrNull { it.accuracy }
        val location = cached ?: providers.firstOrNull {
            it == LocationManager.NETWORK_PROVIDER || it == LocationManager.GPS_PROVIDER
        }?.let { currentLocation(it) } ?: return@withContext null
        if (!Geocoder.isPresent()) return@withContext null
        Geocoder(context, Locale.getDefault()).getFromLocation(location.latitude, location.longitude, 1)
            ?.firstOrNull()?.countryCode
    }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(provider: String): Location? = withTimeoutOrNull(15_000) {
        suspendCancellableCoroutine { continuation ->
            val cancellation = CancellationSignal()
            continuation.invokeOnCancellation { cancellation.cancel() }
            LocationManagerCompat.getCurrentLocation(manager, provider, cancellation, ContextCompat.getMainExecutor(context)) {
                if (continuation.isActive) continuation.resume(it)
            }
        }
    }

    private companion object {
        const val REQUEST_COUNT = "CONT_PERMISSION_LOCATION"
    }
}
