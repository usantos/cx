package br.gov.caixa.loterias.core.network.client

import com.google.gson.Gson
import okhttp3.OkHttpClient
import br.gov.caixa.loterias.core.keymanagement.model.PublicKeySet
import br.gov.caixa.loterias.core.network.interceptor.AuthTokenProvider
import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import br.gov.caixa.loterias.core.network.api.RecargaPayApi
import br.gov.caixa.loterias.core.network.mock.MockBackendOptions
import br.gov.caixa.loterias.core.network.interceptor.NetworkDebugOptions

internal object PaymentProviderNetworkClients {
    fun createMercadoPago(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
    ): MercadoPagoApi = createConfiguredRetrofit(
        baseUrl = baseUrl,
        tokenProvider = AuthTokenProvider { null },
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        attachBearerToken = false,
    ).create(MercadoPagoApi::class.java)

    fun createRecargaPay(
        baseUrl: String,
        clientBuilder: OkHttpClient.Builder,
        gson: Gson,
        publicKeySet: PublicKeySet?,
        debugOptions: NetworkDebugOptions,
        mockOptions: MockBackendOptions,
    ): RecargaPayApi = createConfiguredRetrofit(
        baseUrl = baseUrl,
        tokenProvider = AuthTokenProvider { null },
        clientBuilder = clientBuilder,
        gson = gson,
        publicKeySet = publicKeySet,
        debugOptions = debugOptions,
        mockOptions = mockOptions,
        attachBearerToken = false,
    ).create(RecargaPayApi::class.java)
}
