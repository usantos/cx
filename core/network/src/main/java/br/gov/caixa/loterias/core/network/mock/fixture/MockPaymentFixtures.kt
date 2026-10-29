package br.gov.caixa.loterias.core.network.mock.fixture

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal object MockPaymentFixtures {
    fun paymentMethods() = "[${paymentMethod(1)},${paymentMethod(5)},${paymentMethod(6)}]"

    fun paymentMethod(id: Int?) = when (id) {
        1 -> """{"id":1,"nomePagamentoCompra":"Mercado Pago","padrao":true,"publicKey":"mock-public-key"}"""
        5 -> """{"id":5,"nomePagamentoCompra":"RecargaPay","padrao":false}"""
        6 -> """{"id":6,"nomePagamentoCompra":"Pix","padrao":false}"""
        else -> "{}"
    }

    fun cards() = """[{"idCartao":10,"mesValidade":12,"anoValidade":2029,"primeirosDigitos":"458900",
        "ultimosDigitos":"4589","tamanhoCodigoSeguranca":3,"nomeMetodoPagamento":"Visa",
        "idMetodoPagamento":"visa","operacaoExecutadaComSucesso":true,"tokenCartao":"mock-card-token"}]"""

    fun pix(expired: Boolean): String {
        val now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"))
        val expiration = (if (expired) now.minusMinutes(1) else now.plusMinutes(36))
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return """{"pixCopiaECola":"00020101021226850010014BR.GOV.BCB.PIX0136mock.caixa.gov.br/pix/checkout/99","txId":"MOCK-TX",
            "qrcodeArrByte":"","dataHoraExpiracao":"$expiration",
            "dataHoraCriacao":"${now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}","idCompra":99,"valorCobranca":25}"""
    }

    fun String.isPurchaseOperation(): Boolean = contains("verifica-compra-processamento") || endsWith("registrar-apostas-async")

    fun asyncPurchase(status: Int, purchaseId: Long) = "{\"id\":$purchaseId,\"periodicidade\":1,\"repeticoes\":1,\"idSituacaoCompra\":$status}"
}
