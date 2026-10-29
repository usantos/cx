// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart.data.location

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(Geocoder::class)
class CheckoutGeocoderShadow {
    @Implementation fun getFromLocation(latitude: Double, longitude: Double, maximum: Int): List<Address> {
        require(maximum == 1)
        lastLatitude = latitude
        lastLongitude = longitude
        return listOf(Address(Locale.ROOT).apply { countryCode = "BR" })
    }
    companion object {
        var present = true
        var lastLatitude: Double? = null
        var lastLongitude: Double? = null

        @JvmStatic @Implementation
        fun isPresent() = present
    }
}

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], shadows = [CheckoutGeocoderShadow::class])
class CheckoutLocationAndroidTest {
    private val context get() = RuntimeEnvironment.getApplication()
    private val manager get() = context.getSystemService(LocationManager::class.java)
    private val memory = object : SecureRecordStore {
        var snapshot = SecureSnapshot(0, emptyMap())
        override suspend fun read() = snapshot
        override suspend fun update(transform: (Map<String, String>) -> Map<String, String>): SecureSnapshot {
            snapshot = SecureSnapshot(snapshot.revision + 1, transform(snapshot.values))
            return snapshot
        }
    }
    private fun source() = AndroidCheckoutLocationSource(
        context,
        FixedCoroutineDispatchers(Dispatchers.Unconfined),
        SecureStoreFactory { memory }
    )
    private fun disable() {
        shadowOf(manager).setProviderEnabled(LocationManager.GPS_PROVIDER, false)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, false)
        shadowOf(manager).setProviderEnabled(LocationManager.PASSIVE_PROVIDER, false)
    }

    @Test fun `permission and provider access reflect Android and request count persists with a bound`() = runBlocking {
        disable()
        shadowOf(context).denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        val source = source()
        assertFalse(source.access().providerEnabled)
        assertFalse(source.access().fineGranted)
        assertFalse(source.access().coarseGranted)
        assertEquals(0, source.access().permissionRequestCount)
        repeat(4) { source.recordPermissionRequest() }
        assertEquals(2, source().access().permissionRequestCount)
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        assertTrue(source.access().providerEnabled)
        assertTrue(source.access().coarseGranted)
        assertFalse(source.access().fineGranted)
        context.getSharedPreferences("CONT_PERMISSION_LOCATION", 0).edit().putInt("CONT_PERMISSION_LOCATION", -5).commit()
        source.recordPermissionRequest()
        assertEquals(2, source.access().permissionRequestCount)
    }

    @Test fun `country lookup uses most accurate cached location and stores no coordinates`() = runBlocking {
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        shadowOf(manager).setProviderEnabled(LocationManager.GPS_PROVIDER, true)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        shadowOf(manager).setLastKnownLocation(
            LocationManager.GPS_PROVIDER,
            Location(LocationManager.GPS_PROVIDER).apply {
                latitude = -15.0
                longitude =
                    -47.0
                accuracy = 5f
            }
        )
        shadowOf(manager).setLastKnownLocation(
            LocationManager.NETWORK_PROVIDER,
            Location(LocationManager.NETWORK_PROVIDER).apply {
                latitude =
                    -20.0
                longitude = -43.0
                accuracy = 100f
            }
        )
        CheckoutGeocoderShadow.present = true
        assertEquals("BR", source().countryCode())
        assertEquals(-15.0, CheckoutGeocoderShadow.lastLatitude!!, 0.0)
        assertEquals(-47.0, CheckoutGeocoderShadow.lastLongitude!!, 0.0)
        assertTrue(context.getSharedPreferences("CONT_PERMISSION_LOCATION", 0).all.isEmpty())
        CheckoutGeocoderShadow.present = false
        assertNull(source().countryCode())
    }

    @Test fun `no provider returns no country and current request cancellation ignores later positions`() = runBlocking {
        disable()
        assertNull(source().countryCode())
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        shadowOf(manager).setProviderEnabled(LocationManager.GPS_PROVIDER, true)
        CheckoutGeocoderShadow.present = true
        val pending = async(Dispatchers.Unconfined) { source().countryCode() }
        pending.cancel()
        pending.join()
        shadowOf(manager).simulateLocation(
            LocationManager.GPS_PROVIDER,
            Location(LocationManager.GPS_PROVIDER).apply {
                latitude = 1.0
                longitude =
                    2.0
                accuracy = 3f
            }
        )
        shadowOf(Looper.getMainLooper()).idle()
        assertTrue(pending.isCancelled)
    }
}
