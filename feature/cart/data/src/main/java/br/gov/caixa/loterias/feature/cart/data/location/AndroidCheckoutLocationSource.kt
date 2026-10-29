package br.gov.caixa.loterias.feature.cart.data.location

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
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureStorageException
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreScope
import br.gov.caixa.loterias.core.persistence.AndroidSecureStores
import br.gov.caixa.loterias.core.persistence.SecurePreferenceMigration
import br.gov.caixa.loterias.feature.cart.domain.model.CheckoutLocationAccess
import br.gov.caixa.loterias.feature.cart.domain.port.CheckoutLocationSource
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
