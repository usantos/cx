package br.gov.caixa.loterias.feature.tickets.data.datasource.remote

import br.gov.caixa.loterias.feature.tickets.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import com.google.gson.JsonElement
import br.gov.caixa.loterias.feature.tickets.data.mapper.ticketDisplayName
import br.gov.caixa.loterias.feature.tickets.data.mapper.ticketLong
import br.gov.caixa.loterias.feature.tickets.data.mapper.ticketText
import br.gov.caixa.loterias.feature.tickets.data.mapper.toBets
import br.gov.caixa.loterias.feature.tickets.data.mapper.toCheckedTicketResult

/** Coordena as chamadas legadas de conferência, recibo e detalhes opcionais de prêmio na ordem original. */
internal class ConfirmedBetReceiptLoader(private val remote: TicketsRemoteDataSource) {
    suspend fun load(bet: ConfirmedBet): ConfirmedBet {
        require(bet.id > 0) { messageText("aposta_invalida_de9ad6") }
        val checked = if (bet.statusId in setOf(4L, 101L) || bet.status.equals("Efetivada", true)) {
            val status = requireNotNull(remote.checkConfirmedBet(bet.id)) { messageText("situacao_da_aposta_ausente_cc015d") }.asJsonObject
            bet.copy(
                statusId = requireNotNull(status.ticketLong("valor")) { messageText("situacao_da_aposta_incompleta_9e526c") },
                status = status.ticketText("descricao").ticketDisplayName().ifBlank { bet.status }
            )
        } else {
            bet
        }
        val payload = if (bet.backend == br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend.NOVA_API) {
            remote.recoverConfirmedBet(checked.id)
        } else {
            remote.confirmedBetReceipt(checked.id, paid = checked.statusId == 11L, historical = checked.statusId == 3L)
        }
        val hydrated = requireNotNull(payload.toBets().firstOrNull { it.id == checked.id }) {
            messageText("comprovante_da_aposta_ausente_1128e7")
        }
        // Clientes instalados preservam o estado da lista para Nova API/microsserviço e teimosinhas;
        // os dois endpoints de recibo possuem um indicador de combo reconhecidamente incorreto.
        val keepStatus = bet.backend != br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsBackend.SILCE ||
            hydrated.recurringDraws > 0 || checked !== bet || checked.statusId in setOf(3L, 11L, 100L)
        val receiptRoot = payload?.takeIf(JsonElement::isJsonObject)?.asJsonObject
        val prizeRoot = if (checked.statusId in setOf(100L, 11L) &&
            receiptRoot?.get("premio")?.takeIf(JsonElement::isJsonObject) == null
        ) {
            requireNotNull(remote.confirmedBetPrizeDetails(checked.id)) { messageText("detalhes_do_premio_ausentes_372548") }.asJsonObject
        } else {
            receiptRoot
        }
        val prize = prizeRoot?.get("premio")?.takeIf(JsonElement::isJsonObject)?.asJsonObject
        if (checked.statusId in setOf(100L, 11L)) requireNotNull(prize) { messageText("dados_do_premio_ausentes_92dfab") }
        val netPrize = prize?.get("valorLiquido")?.takeUnless(JsonElement::isJsonNull)?.let {
            runCatching { it.asBigDecimal }.getOrNull()
        }
        if (checked.statusId in setOf(100L, 11L)) requireNotNull(netPrize) { messageText("valor_liquido_do_premio_ausente_8a11c9") }
        val receipt = br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetReceipt(
            nsbi = receiptRoot?.ticketText("nsbi").orEmpty(),
            paymentDate = prize?.ticketText("dataPagamento").orEmpty(),
            paymentTime = prize?.ticketText("horaPagamento").orEmpty(),
            paymentLocation = prize?.ticketText("localPagamento").orEmpty(),
            paymentNsu = prize?.ticketText("nsuPagamento").orEmpty(),
            netPrize = netPrize,
            awards = prizeRoot?.toCheckedTicketResult()?.awards.orEmpty()
        )
        return hydrated.copy(
            receipt = receipt,
            backend = bet.backend,
            combo = bet.combo,
            statusId = if (keepStatus) checked.statusId else hydrated.statusId,
            status = if (keepStatus) checked.status else hydrated.status,
            prize = receipt.netPrize ?: hydrated.prize.takeIf { it.signum() != 0 } ?: checked.prize,
            redeemedPrize = checked.redeemedPrize || checked.statusId in setOf(3L, 11L),
            originalRecurringBet = hydrated.originalRecurringBet || bet.originalRecurringBet
        )
    }
}
