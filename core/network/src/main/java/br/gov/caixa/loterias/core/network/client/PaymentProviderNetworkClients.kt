package br.gov.caixa.loterias.core.network.client

import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import br.gov.caixa.loterias.core.network.api.RecargaPayApi

internal object PaymentProviderNetworkClients {
    fun createMercadoPago(options: NetworkClientOptions): MercadoPagoApi =
        createConfiguredRetrofit(options).create(MercadoPagoApi::class.java)

    fun createRecargaPay(options: NetworkClientOptions): RecargaPayApi =
        createConfiguredRetrofit(options).create(RecargaPayApi::class.java)
}
