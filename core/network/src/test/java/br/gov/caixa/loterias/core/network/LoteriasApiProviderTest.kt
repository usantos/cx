package br.gov.caixa.loterias.core.network

import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.core.network.api.BetsHistoryApi
import br.gov.caixa.loterias.core.network.api.MercadoPagoApi
import br.gov.caixa.loterias.core.network.api.RecargaPayApi
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.network.provider.BetsHistoryApiProvider
import br.gov.caixa.loterias.core.network.provider.PaymentApis
import br.gov.caixa.loterias.core.network.provider.PaymentApiProvider

class LoteriasApiProviderTest {
    @Test
    fun `requires initialization and returns latest api`() {
        val provider = LoteriasApiProvider()
        assertThrows(IllegalStateException::class.java, provider::current)
        val first = fakeApi()
        val second = fakeApi()
        provider.replace(first)
        assertSame(first, provider.current())
        provider.replace(second)
        assertSame(second, provider.current())
    }

    @Test
    fun `legacy provider requires initialization and returns latest api`() {
        val provider = BackendApiProvider()
        assertThrows(IllegalStateException::class.java, provider::current)
        val first = fakeLegacyApi()
        val second = fakeLegacyApi()
        provider.replace(first)
        assertSame(first, provider.current())
        provider.replace(second)
        assertSame(second, provider.current())
    }

    @Test
    fun `history and payment providers expose replaced services`() {
        val historyProvider = BetsHistoryApiProvider()
        assertThrows(IllegalStateException::class.java, historyProvider::current)
        val history = fake(BetsHistoryApi::class.java)
        historyProvider.replace(history)
        assertSame(history, historyProvider.current())

        val paymentProvider = PaymentApiProvider()
        assertThrows(IllegalStateException::class.java, paymentProvider::current)
        val payment = PaymentApis(fake(MercadoPagoApi::class.java), fake(RecargaPayApi::class.java))
        paymentProvider.replace(payment)
        assertSame(payment, paymentProvider.current())
    }

    private fun fakeApi(): LoteriasApi = java.lang.reflect.Proxy.newProxyInstance(
        LoteriasApi::class.java.classLoader,
        arrayOf(LoteriasApi::class.java),
    ) { _, _, _ -> error("not called") } as LoteriasApi

    private fun fakeLegacyApi(): BackendApi = java.lang.reflect.Proxy.newProxyInstance(
        BackendApi::class.java.classLoader,
        arrayOf(BackendApi::class.java),
    ) { _, _, _ -> error("not called") } as BackendApi

    @Suppress("UNCHECKED_CAST")
    private fun <T> fake(type: Class<T>): T = java.lang.reflect.Proxy.newProxyInstance(
        type.classLoader,
        arrayOf(type),
    ) { _, _, _ -> error("not called") } as T
}
