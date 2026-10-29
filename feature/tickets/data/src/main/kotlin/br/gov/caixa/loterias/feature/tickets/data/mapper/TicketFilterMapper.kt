package br.gov.caixa.loterias.feature.tickets.data.mapper

import br.gov.caixa.loterias.feature.tickets.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBetsConfiguration
import br.gov.caixa.loterias.feature.tickets.domain.model.PurchaseMonth
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketFilterOption
import br.gov.caixa.loterias.feature.tickets.domain.model.TicketPeriod
import com.google.gson.JsonElement

internal fun JsonElement?.mapTicketFilters(): ConfirmedBetsConfiguration {
    val root = ticketPayloadObject() ?: return ConfirmedBetsConfiguration()
    fun options(name: String) = root.get(name).mapTicketFilterOptions("descricao", idKey = "valor")
    fun period(element: JsonElement?): TicketPeriod? {
        val value = element?.takeIf(JsonElement::isJsonObject)?.asJsonObject ?: return null
        val month = value.getAsJsonObject("mes") ?: return null
        return TicketPeriod(
            month = month.ticketInt("valor") ?: return null,
            year = value.ticketInt("ano"),
            description = month.ticketText("descricao").ifBlank { month.ticketInt("valor")?.toString().orEmpty() }
        )
    }
    return ConfirmedBetsConfiguration(
        defaultOrdering = root.getAsJsonObject("ordenacaoPadrao")?.ticketLong("valor"),
        defaultStatus = root.getAsJsonObject("situacaoPadrao")?.ticketLong("valor"),
        defaultPeriod = period(root.get("periodoPadrao")),
        statuses = options("situacoes"),
        orderings = options("ordenacoes"),
        periods = root.get("periodos")?.takeIf(JsonElement::isJsonArray)?.asJsonArray
            ?.mapNotNull(::period).orEmpty(),
        betTypes = options("listaTipoAposta"),
        contestTypes = options("listaTipoConcurso")
    )
}

internal fun JsonElement?.mapPurchaseMonths(): List<PurchaseMonth> = ticketPayloadArrayObjects().mapNotNull { value ->
    val month = value.getAsJsonObject("mes") ?: return@mapNotNull null
    val number = month.ticketInt("valor") ?: return@mapNotNull null
    val year = value.ticketInt("ano") ?: return@mapNotNull null
    PurchaseMonth(number, year, messageText("1_s_de_2_s_ec1761", month.ticketText("descricao"), year))
}

internal fun JsonElement?.mapTicketFilterOptions(vararg labelKeys: String, idKey: String = "id"): List<TicketFilterOption> =
    ticketPayloadArrayObjects().mapNotNull { value ->
        val id = value.ticketLong(idKey) ?: return@mapNotNull null
        val label = labelKeys.firstNotNullOfOrNull { key -> value.ticketText(key).takeIf(String::isNotBlank) }
            ?: return@mapNotNull null
        TicketFilterOption(id, label.replace(" - ", "-"))
    }.distinctBy(TicketFilterOption::value)
