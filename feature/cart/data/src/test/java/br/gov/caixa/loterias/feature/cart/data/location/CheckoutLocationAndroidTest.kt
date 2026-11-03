// Literais numéricos descrevem conteúdos sintéticos, níveis da API da plataforma e limites esperados das massas de teste.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.cart.data.location

import android.Manifest
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.os.SystemClock
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.domain.persistence.SecureRecordStore
import br.gov.caixa.loterias.core.domain.persistence.SecureSnapshot
import br.gov.caixa.loterias.core.domain.persistence.SecureStoreFactory
import java.io.IOException
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
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
        if (unavailable) throw IOException("Geocodificação indisponível")
        require(maximum == 1)
        lastLatitude = latitude
        lastLongitude = longitude
        return listOf(Address(Locale.ROOT).apply { countryCode = "BR" })
    }
    companion object {
        var present = true
        var unavailable = false
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

    @Before fun resetGeocoder() {
        CheckoutGeocoderShadow.present = true
        CheckoutGeocoderShadow.unavailable = false
        CheckoutGeocoderShadow.lastLatitude = null
        CheckoutGeocoderShadow.lastLongitude = null
    }

    private fun source(dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.Unconfined) = AndroidCheckoutLocationSource(
        context,
        FixedCoroutineDispatchers(dispatcher),
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
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
        )
        shadowOf(manager).setLastKnownLocation(
            LocationManager.NETWORK_PROVIDER,
            Location(LocationManager.NETWORK_PROVIDER).apply {
                latitude =
                    -20.0
                longitude = -43.0
                accuracy = 100f
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
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

    @Test fun `posicao antiga precisa nao substitui posicao recente do outro provedor`() = runBlocking {
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        shadowOf(manager).setProviderEnabled(LocationManager.GPS_PROVIDER, true)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        org.robolectric.shadows.ShadowSystemClock.advanceBy(java.time.Duration.ofMinutes(2))
        shadowOf(manager).setLastKnownLocation(
            LocationManager.GPS_PROVIDER,
            Location(LocationManager.GPS_PROVIDER).apply {
                latitude = 40.0
                longitude = -74.0
                accuracy = 1f
            }
        )
        shadowOf(manager).setLastKnownLocation(
            LocationManager.NETWORK_PROVIDER,
            Location(LocationManager.NETWORK_PROVIDER).apply {
                latitude = -15.0
                longitude = -47.0
                accuracy = 100f
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
        )
        assertEquals("BR", source().countryCode())
        assertEquals(-15.0, CheckoutGeocoderShadow.lastLatitude!!, 0.0)
    }

    @Test fun `sem cache solicita posicao atual e nao persiste coordenadas`() = runBlocking {
        disable()
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        val lookup = async(Dispatchers.Unconfined) { source().countryCode() }
        shadowOf(manager).simulateLocation(
            LocationManager.NETWORK_PROVIDER,
            Location(LocationManager.NETWORK_PROVIDER).apply {
                latitude = -15.0
                longitude = -47.0
                accuracy = 10f
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
        )
        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("BR", lookup.await())
        assertTrue(memory.snapshot.values.isEmpty())
    }

    @Test fun `falha do geocodificador retorna indisponibilidade sem afirmar outro pais`() = runBlocking {
        disable()
        shadowOf(manager).setProviderEnabled(LocationManager.PASSIVE_PROVIDER, true)
        shadowOf(manager).setLastKnownLocation(
            LocationManager.PASSIVE_PROVIDER,
            Location(LocationManager.PASSIVE_PROVIDER).apply {
                accuracy = 10f
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
        )
        CheckoutGeocoderShadow.unavailable = true
        assertNull(source().countryCode())
    }

    @Test fun `timeout da rede tenta GPS e aceita somente posicao recente`() = kotlinx.coroutines.test.runTest {
        disable()
        shadowOf(context).grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        shadowOf(manager).setProviderEnabled(LocationManager.GPS_PROVIDER, true)
        val dispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        val lookup = async { source(dispatcher).countryCode() }
        testScheduler.runCurrent()
        testScheduler.advanceTimeBy(7_500)
        testScheduler.runCurrent()
        shadowOf(manager).simulateLocation(
            LocationManager.GPS_PROVIDER,
            Location(LocationManager.GPS_PROVIDER).apply {
                latitude = -20.0
                longitude = -43.0
                accuracy = 10f
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
        )
        shadowOf(Looper.getMainLooper()).idle()
        testScheduler.runCurrent()
        assertEquals("BR", lookup.await())
        assertEquals(-20.0, CheckoutGeocoderShadow.lastLatitude!!, 0.0)
    }

    @Test fun `cache futuro ou sem precisao nao confirma pais e timeout nao inventa posicao`() = kotlinx.coroutines.test.runTest {
        disable()
        shadowOf(manager).setProviderEnabled(LocationManager.PASSIVE_PROVIDER, true)
        val dispatcher = kotlinx.coroutines.test.StandardTestDispatcher(testScheduler)
        for (location in listOf(
            Location(LocationManager.PASSIVE_PROVIDER).apply {
                accuracy = 10f
                elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos() + 1_000_000_000L
            },
            Location(LocationManager.PASSIVE_PROVIDER)
        )) {
            shadowOf(manager).setLastKnownLocation(LocationManager.PASSIVE_PROVIDER, location)
            assertNull(source(dispatcher).countryCode())
        }
        shadowOf(manager).setProviderEnabled(LocationManager.NETWORK_PROVIDER, true)
        shadowOf(manager).setProviderEnabled(LocationManager.GPS_PROVIDER, true)
        val lookup = async { source(dispatcher).countryCode() }
        testScheduler.advanceUntilIdle()
        assertNull(lookup.await())
        assertNull(CheckoutGeocoderShadow.lastLatitude)
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
