package br.gov.caixa.loterias.feature.payment.presentation.mapper

private val operatingTimeWithSeconds = Regex("(?<![0-9:])([01][0-9]|2[0-3]):([0-5][0-9]):[0-5][0-9](?![0-9:])")

/** Somente apresentação: mantém a resposta DAM e suas regras de horário de operação inalteradas. */
internal fun paymentOperatingHours(message: String): String =
    operatingTimeWithSeconds.replace(message) { "${it.groupValues[1]}:${it.groupValues[2]}" }
