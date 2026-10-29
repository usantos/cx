package br.gov.caixa.loterias.feature.tickets.data.mapper

import br.gov.caixa.loterias.feature.tickets.domain.model.Purchase
import com.google.gson.JsonElement

internal fun JsonElement?.mapTicketPurchases(): List<Purchase> = ticketCollectObjects().filter { objectValue ->
    objectValue.ticketLong("id") != null && objectValue.has("valorTotal") &&
        (objectValue.has("dataInicioCompra") || objectValue.has("situacao"))
}.distinctBy { it.ticketLong("id") }.map { value ->
    Purchase(
        id = value.ticketLong("id") ?: 0,
        date = value.ticketText("dataInicioCompra").ifBlank { value.ticketText("dataFinalizacaoCompra") },
        status = value.ticketText("situacao").ticketDisplayName(),
        paymentMethod = value.ticketText("meioPagamento").ticketDisplayName(),
        total = value.ticketDecimal("valorTotal"),
        pixCopyPaste = value.ticketText("pixCopiaECola"),
        paymentExpiresAt = value.ticketText("dataHoraExpiracaoPagamento"),
        statusId = value.ticketChildLong("situacao", "valor") ?: value.ticketLong("idSituacaoCompra") ?: 0,
        paymentMethodId = value.ticketChildLong("meioPagamento", "valor") ?: value.ticketLong("idMeioPagamento") ?: 0,
        finalizedTime = value.ticketText("horaFinalizacaoCompra"),
        endToEndId = value.ticketText("e2eId"),
    )
}
