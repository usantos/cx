package br.gov.caixa.loterias.feature.retail.data.mapper

import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.feature.retail.data.localizedMessage as messageText
import br.gov.caixa.loterias.feature.retail.domain.model.LotteryRedemptionCode
import br.gov.caixa.loterias.feature.retail.domain.model.PixBeneficiary
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeAward
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeDetails
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionChannel
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionOption
import br.gov.caixa.loterias.feature.retail.domain.model.PrizeRedemptionResult
import com.google.gson.JsonElement
import com.google.gson.JsonObject

internal fun JsonElement?.toPrizeDetails(betId: Long): PrizeDetails {
    val root = retailObjectOrEmpty()
    val prize = root.retailObject("premio")
    val bet = root.retailObject("aposta")
    val responseBetId = bet.retailLong("id") ?: betId
    val modalityCode = bet.retailText("modalidade")
    val modalityId = modalityCode.toPrizeModalityId()
    val modalityName = modalityCode.toPrizeModalityName()
    return PrizeDetails(
        betId = responseBetId,
        netValue = prize.retailDecimal("valorLiquido"),
        nsbi = root.retailText("nsbi"),
        options = root.retailArrayObjects("meiosResgate").mapNotNull { option ->
            val channel = PrizeRedemptionChannel.entries.firstOrNull { it.legacyId == option.retailLong("id") }
                ?: return@mapNotNull null
            // O serviço decide quais canais estão disponíveis. Os textos são contrato
            // do produto e reproduzem a montagem de ResultadoApostaConfirmadaActivity.
            PrizeRedemptionOption(channel, channel.defaultTitle(), channel.defaultDescription())
        },
        incomeTaxMessage = root.retailText("mensagemInformativaIR"),
        modalityId = modalityId,
        modalityName = modalityName,
        initialContest = (bet.retailLong("concursoInicial") ?: bet.retailLong("concursoAlvo") ?: 0).toInt(),
        targetContest = (bet.retailLong("concursoAlvo") ?: bet.retailLong("concursoInicial") ?: 0).toInt(),
        isPool = bet.retailText("indicadorCotaBolao").equals("true", ignoreCase = true),
        exchange = bet.retailText("troca").equals("true", ignoreCase = true),
        combo = bet.retailText("combo").equals("true", ignoreCase = true),
        mirror = bet.retailText("espelho").equals("true", ignoreCase = true),
        surprise = bet.retailText("surpresinha").equals("true", ignoreCase = true),
        recurringDraws = (bet.retailLong("quantidadeTeimosinhas") ?: 0).toInt(),
        awards = prize.retailArrayObjects("concursos").flatMap { awardedContest ->
            val contest = awardedContest.retailObject("concurso").retailLong("numero")?.toInt()
                ?: return@flatMap emptyList()
            awardedContest.retailArrayObjects("faixas").map { range ->
                PrizeAward(
                    contest = contest,
                    description = range.installedPrizeDescription(modalityCode),
                    // Zero é um prêmio de cota fornecido, e não um valor ausente.
                    amount = range.retailOptionalDecimal("valorPremioCota") ?: range.retailOptionalDecimal("valorLiquido"),
                    status = awardedContest.retailText("situacao"),
                    hitCount = range.retailLong("quantidadeAcertos")?.toInt(),
                    draw = range.retailLong("sorteio")?.toInt()
                )
            }
        } + prize.retailArrayObjects("concursosNaoPremiado").mapNotNull { contest ->
            val number = contest.retailObject("concurso").retailLong("numero")?.toInt() ?: return@mapNotNull null
            PrizeAward(number, description = "", amount = null, status = contest.retailText("situacao"), isAwarded = false)
        }
    )
}

internal fun JsonElement?.toLotteryRedemptionCode(): LotteryRedemptionCode {
    val root = retailObjectOrEmpty()
    return LotteryRedemptionCode(
        qrCode = root.retailText("qrCode"),
        code = root.retailText("codigo"),
        nsb = root.retailText("nsb"),
        validityMinutes = (root.retailLong("validade") ?: 15).toInt().coerceAtLeast(1)
    )
}

internal fun JsonElement?.toPixBeneficiary(): PixBeneficiary {
    val root = retailObjectOrEmpty()
    return PixBeneficiary(root.retailText("cpfMascarado"), root.retailText("nome"), root.retailText("chave"), root.retailText("nomeBanco"))
}

internal fun mercadoPagoRedemptionBody(email: String) = JsonObject().apply {
    addProperty("meioPagamento", PrizeRedemptionChannel.MERCADO_PAGO.legacyId)
    add("parametrosPagamento", JsonObject().apply { addProperty("EMAIL", email) })
}

/** ResultadoApostaConfirmadaViewModel#getAcertosNormalized instalado. */
private fun JsonObject.installedPrizeDescription(modalityCode: String): String {
    var description = retailText("descricaoAcerto").ifBlank { retailText("nome") }
    // Preserva a precedência da expressão instalada e suas substituições literais.
    if (description.contains("1º") || (description.contains("2º") && modalityCode == "LOTECA")) {
        description = description.replace("1º (", "").replace("2º (", "").replace(")", "")
    }
    if (modalityCode == "DUPLA_SENA") {
        when (retailLong("sorteio")?.toInt()) {
            1 -> description = messageText("1o_sorteio_1_s_c0dc67", description)
            2 -> description = messageText("2o_sorteio_1_s_5db28e", description)
        }
    }
    val occurrences = retailLong("quantidadeAcertos")?.toInt() ?: 0
    return description + if (occurrences >= 2) messageText("1_s_vezes_56b471", occurrences) else ""
}

internal fun ServiceFailure.toRetailPrizeResult(): PrizeRedemptionResult = when (this) {
    is ServiceFailure.ConnectionFailed -> PrizeRedemptionResult.ConnectionFailed(reason)
    ServiceFailure.Unchanged -> PrizeRedemptionResult.Unchanged
    is ServiceFailure.NoticeRequired -> PrizeRedemptionResult.NoticeRequired(notice)
    is ServiceFailure.RedirectRequired -> PrizeRedemptionResult.RedirectRequired(destination)
    is ServiceFailure.RegistrationRequired -> PrizeRedemptionResult.RegistrationRequired(message)
}

internal fun prizePixBody(key: String) = JsonObject().apply {
    addProperty("meioPagamento", PrizeRedemptionChannel.PIX.legacyId)
    addProperty("chavePix", key)
}

private fun PrizeRedemptionChannel.defaultTitle() = when (this) {
    PrizeRedemptionChannel.MERCADO_PAGO -> messageText("mercado_pago_fe6402")
    PrizeRedemptionChannel.PIX -> messageText("pix_41b063")
    PrizeRedemptionChannel.LOTTERY -> messageText("loterica_6552e8")
    PrizeRedemptionChannel.AGENCY -> messageText("agencia_caixa_f622ab")
}

private fun PrizeRedemptionChannel.defaultDescription() = when (this) {
    PrizeRedemptionChannel.MERCADO_PAGO, PrizeRedemptionChannel.PIX -> messageText("receba_na_hora_fe40ac")
    PrizeRedemptionChannel.LOTTERY -> messageText("retire_em_qualquer_loterica_d53b24")
    PrizeRedemptionChannel.AGENCY -> messageText("valores_acima_da_isencao_do_ir_b68573")
}

private fun String.toPrizeModalityId(): Int = when (uppercase()) {
    "QUINA" -> 3
    "SUPER_7" -> 7
    "LOTOFACIL" -> 8
    "MAIS_MILIONARIA" -> 9
    "DIA_DE_SORTE" -> 11
    "LOTOGOL" -> 14
    "LOTOMANIA" -> 16
    "DUPLA_SENA" -> 18
    "LOTECA" -> 19
    "TIMEMANIA" -> 20
    else -> 2
}

private fun String.toPrizeModalityName(): String = when (uppercase()) {
    "QUINA" -> messageText("quina_2d1f9e")
    "SUPER_7" -> messageText("super_sete_8d4823")
    "LOTOFACIL" -> messageText("lotofacil_d3ed7c")
    "MAIS_MILIONARIA" -> messageText("milionaria_11be79")
    "DIA_DE_SORTE" -> messageText("dia_de_sorte_b5c7a5")
    "LOTOGOL" -> messageText("lotogol_adaed4")
    "LOTOMANIA" -> messageText("lotomania_244a56")
    "DUPLA_SENA" -> messageText("dupla_sena_a2c9e5")
    "LOTECA" -> messageText("loteca_2979a8")
    "TIMEMANIA" -> messageText("timemania_e24e8e")
    else -> messageText("mega_sena_1af8ff")
}

internal fun JsonElement?.prizeNoticeCode(): String? = when {
    this == null || isJsonNull -> null
    isJsonPrimitive -> runCatching { asString }.getOrNull()?.trim()?.takeIf(String::isNotEmpty)
    isJsonObject -> asJsonObject.retailText("codigo").ifBlank { asJsonObject.retailText("mensagem") }.takeIf(String::isNotBlank)
    else -> null
}
