package br.gov.caixa.loterias.apostas.di

import android.content.Context
import br.gov.caixa.loterias.core.common.coroutines.CoroutineDispatchers
import br.gov.caixa.loterias.core.common.coroutines.DefaultCoroutineDispatchers
import br.gov.caixa.loterias.core.data.di.AppConfigurationDataModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [AppConfigurationDataModule::class])
@InstallIn(SingletonComponent::class)
object AppInitializationBindings {
    @Provides @Singleton
    fun foregroundSessionBackgroundState(): br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionBackgroundState =
        br.gov.caixa.loterias.core.auth.foreground.ForegroundSessionBackgroundState()

    @Provides @Singleton
    fun networkAvailability(@ApplicationContext context: Context): br.gov.caixa.loterias.core.common.network.NetworkAvailability =
        br.gov.caixa.loterias.core.common.network.ReadNetworkAvailability {
            context.getSystemService(android.net.ConnectivityManager::class.java)?.let {
                val network = it.activeNetwork
                if (network == null) {
                    false
                } else {
                    it.getNetworkCapabilities(network)?.let { capabilities ->
                        when {
                            !capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) -> false
                            capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> true
                            else -> null
                        }
                    }
                }
            }
        }

    @Provides @Singleton
    fun localClock(): java.time.Clock = java.time.Clock.systemDefaultZone()

    @Provides @Singleton
    fun dispatchers(): CoroutineDispatchers = DefaultCoroutineDispatchers
}
