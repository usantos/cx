package br.gov.caixa.loterias.core.network.client

import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.PurchaseBackendApi
import br.gov.caixa.loterias.core.network.api.PurchaseBackendClient
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import retrofit2.Retrofit

internal object TransactionNetworkClients {
    private fun createRetrofit(options: NetworkClientOptions, tokenProvider: AuthTokenProvider): Retrofit {
        options.clientBuilder.singleAttemptRequests()
        return createConfiguredRetrofit(options, tokenProvider)
    }

    fun createPrizeRedemption(options: NetworkClientOptions, tokenProvider: AuthTokenProvider): BackendApi =
        createRetrofit(options, tokenProvider).create(BackendApi::class.java)

    fun createPurchaseBackend(
        options: NetworkClientOptions,
        tokenProvider: AuthTokenProvider,
    ): PurchaseBackendClient = PurchaseBackendClient(
        createRetrofit(options, tokenProvider).create(PurchaseBackendApi::class.java),
        options.gson,
    )
}
