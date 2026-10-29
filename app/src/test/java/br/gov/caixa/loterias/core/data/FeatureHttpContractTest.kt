package br.gov.caixa.loterias.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class FeatureHttpContractTest {
    @Test fun featureContractsRetainExistingPathsPayloadsHeadersAndParameters() {
        val contracts = listOf(
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.BackendApi::class.java to br.gov.caixa.loterias.feature.account.data.datasource.remote.AccountBackendApi::class.java,
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.BackendApi::class.java to br.gov.caixa.loterias.feature.betting.data.datasource.remote.BettingBackendApi::class.java,
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.BackendApi::class.java to br.gov.caixa.loterias.feature.cart.data.datasource.remote.CartBackendApi::class.java,
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.marketplace.data.datasource.remote.MarketplaceLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.BackendApi::class.java to br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentBackendApi::class.java,
            br.gov.caixa.loterias.core.network.api.MercadoPagoApi::class.java to br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentMercadoPagoApi::class.java,
            br.gov.caixa.loterias.core.network.api.RecargaPayApi::class.java to br.gov.caixa.loterias.feature.payment.data.datasource.remote.PaymentRecargaPayApi::class.java,
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.BackendApi::class.java to br.gov.caixa.loterias.feature.results.data.datasource.remote.ResultsBackendApi::class.java,
            br.gov.caixa.loterias.core.network.api.BackendApi::class.java to br.gov.caixa.loterias.feature.retail.data.datasource.remote.RetailBackendApi::class.java,
            br.gov.caixa.loterias.core.network.api.LoteriasApi::class.java to br.gov.caixa.loterias.feature.support.data.datasource.remote.SupportLoteriasApi::class.java,
            br.gov.caixa.loterias.core.network.api.BackendApi::class.java to br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsBackendApi::class.java,
            br.gov.caixa.loterias.core.network.api.BetsHistoryApi::class.java to br.gov.caixa.loterias.feature.tickets.data.datasource.remote.TicketsBetsHistoryApi::class.java
        )
        contracts.forEach { (compatibility, feature) ->
            feature.declaredMethods.filterNot { it.isSynthetic }.forEach { actual ->
                val original = compatibility.getDeclaredMethod(actual.name, *actual.parameterTypes)
                assertEquals(actual.toString(), original.genericReturnType, actual.genericReturnType)
                assertEquals(actual.toString(), original.genericParameterTypes.toList(), actual.genericParameterTypes.toList())
                assertEquals(actual.toString(), original.annotations.toList(), actual.annotations.toList())
                assertEquals(actual.toString(), original.parameterAnnotations.map { it.toList() }, actual.parameterAnnotations.map { it.toList() })
            }
        }
    }
}
