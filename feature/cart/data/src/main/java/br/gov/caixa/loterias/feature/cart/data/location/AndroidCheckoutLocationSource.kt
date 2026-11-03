package br.gov.caixa.loterias.feature.cart.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.core.persistence.SecurePreferenceMigration
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationAccess
import br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/** Fronteira exclusiva da plataforma. Coordenadas são transitórias e nunca são registradas nem persistidas. */
class AndroidCheckoutLocationSource(
    private val context: Context,
    private val dispatchers: CoroutineDispatchers,
    factory: SecureStoreFactory = AndroidSecureStores(context),
) : CheckoutLocationSource {
    private val store = SecurePreferenceMigration(
        factory.open(SecureStoreScope("installation", "installation", "location_permission")),
        readLegacy = {
            withContext(dispatchers.io) {
                val preferences = context.getSharedPreferences(REQUEST_COUNT, Context.MODE_PRIVATE)
                if (preferences.contains(REQUEST_COUNT)) {
                    mapOf(REQUEST_COUNT to preferences.getInt(REQUEST_COUNT, 0).coerceIn(0, 2).toString())
                } else {
                    emptyMap()
                }
            }
        },
        clearLegacy = {
            withContext(dispatchers.io) {
                val preferences = context.getSharedPreferences(REQUEST_COUNT, Context.MODE_PRIVATE)
                if (preferences.contains(REQUEST_COUNT)) {
                    val editor = preferences.edit()
                    editor.remove(REQUEST_COUNT)
                    if (!editor.commit()) throw SecureStorageException.WriteFailure()
                }
            }
        }
    )
    private val manager: LocationManager
        get() = requireNotNull(context.getSystemService(LocationManager::class.java))

    override suspend fun access() = CheckoutLocationAccess(
        providerEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER),
        fineGranted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED,
        coarseGranted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED,
        permissionRequestCount = count(store.read().values[REQUEST_COUNT])
    )

    override suspend fun recordPermissionRequest() {
        store.update { values -> values + (REQUEST_COUNT to (count(values[REQUEST_COUNT]) + 1).coerceAtMost(2).toString()) }
    }

    private fun count(value: String?): Int {
        if (value == null) return 0
        return value.toIntOrNull()?.takeIf { it in 0..2 } ?: throw SecureStorageException.InvalidRecord()
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    override suspend fun countryCode(): String? = withContext(dispatchers.io) {
        if (!Geocoder.isPresent()) return@withContext null
        val providers = manager.getProviders(true)
        val cached = providers.mapNotNull { manager.getLastKnownLocation(it) }
            .filter(::isRecentLocation).minByOrNull { it.accuracy }
        val location = cached ?: freshLocation(providers) ?: return@withContext null
        try {
            Geocoder(context, Locale.getDefault()).getFromLocation(location.latitude, location.longitude, 1)
                ?.firstOrNull()?.countryCode
        } catch (_: IOException) {
            null
        }
    }

    private fun isRecentLocation(location: Location): Boolean {
        val now = SystemClock.elapsedRealtimeNanos()
        return location.elapsedRealtimeNanos in 0..now && now - location.elapsedRealtimeNanos <= MAX_LOCATION_AGE_NANOS &&
            location.hasAccuracy() && location.accuracy.isFinite() && location.accuracy >= 0f &&
            location.latitude in MIN_LATITUDE..MAX_LATITUDE && location.longitude in MIN_LONGITUDE..MAX_LONGITUDE
    }

    private suspend fun freshLocation(enabledProviders: List<String>): Location? {
        for (provider in listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)) {
            if (provider !in enabledProviders) continue
            val location = currentLocation(provider)
            if (location != null && isRecentLocation(location)) return location
        }
        return null
    }

    @SuppressLint("MissingPermission")
    private suspend fun currentLocation(provider: String): Location? = withTimeoutOrNull(PROVIDER_TIMEOUT_MILLIS) {
        suspendCancellableCoroutine { continuation ->
            val cancellation = CancellationSignal()
            continuation.invokeOnCancellation { cancellation.cancel() }
            LocationManagerCompat.getCurrentLocation(manager, provider, cancellation, ContextCompat.getMainExecutor(context)) {
                if (continuation.isActive) continuation.resume(it)
            }
        }
    }

    private companion object {
        const val MAX_LOCATION_AGE_NANOS = 60_000_000_000L
        const val PROVIDER_TIMEOUT_MILLIS = 7_500L
        const val MIN_LATITUDE = -90.0
        const val MAX_LATITUDE = 90.0
        const val MIN_LONGITUDE = -180.0
        const val MAX_LONGITUDE = 180.0
        const val REQUEST_COUNT = "CONT_PERMISSION_LOCATION"
    }
}
