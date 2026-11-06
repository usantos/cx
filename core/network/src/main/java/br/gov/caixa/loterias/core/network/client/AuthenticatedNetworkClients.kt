package br.gov.caixa.loterias.core.network.client

import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider

internal object AuthenticatedNetworkClients {
    fun create(options: NetworkClientOptions, tokenProvider: AuthTokenProvider): LoteriasApi =
        createConfiguredRetrofit(options, tokenProvider).create(LoteriasApi::class.java)

    fun createLegacy(options: NetworkClientOptions, tokenProvider: AuthTokenProvider): BackendApi =
        createConfiguredRetrofit(options, tokenProvider).create(BackendApi::class.java)

    fun createBetsHistory(options: NetworkClientOptions, tokenProvider: AuthTokenProvider): BetsHistoryApi =
        createConfiguredRetrofit(options, tokenProvider).create(BetsHistoryApi::class.java)
}
