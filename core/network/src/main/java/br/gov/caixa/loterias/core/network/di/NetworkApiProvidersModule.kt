package br.gov.caixa.loterias.core.network.di

import br.gov.caixa.loterias.core.network.api.AccountVerificationBackendProvider
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.api.NotificationReadApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider
import br.gov.caixa.loterias.core.network.provider.PrizeBackendApiProvider
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApiProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/** Cria os contêineres sem estado dos providers de API pertencentes a CORE Network. */
@Module
object NetworkApiProvidersModule {
    @Provides @Singleton fun loteriasApiProvider() = LoteriasApiProvider()
    @Provides @Singleton fun legacyApiProvider() = BackendApiProvider()
    @Provides @Singleton fun betsHistoryApiProvider() = BetsHistoryApiProvider()
    @Provides @Singleton fun paymentApiProvider() = PaymentApiProvider()
    @Provides @Singleton fun purchaseApiProvider() = PurchaseBackendApiProvider()
    @Provides @Singleton fun prizeApiProvider() = PrizeBackendApiProvider()
    @Provides @Singleton fun accountVerificationProvider() = AccountVerificationBackendProvider()
    @Provides @Singleton fun notificationInboxProvider() = br.gov.caixa.loterias.core.network.api.NotificationInboxApiProvider()

    @Provides @Singleton
    fun notificationReadProvider() = NotificationReadApiProvider()
}
