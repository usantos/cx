package br.gov.caixa.loterias.feature.home.presentation.screen

import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult

internal fun homeResultColumns(modality: String, count: Int): Int =
    if (normalizeModality(modality) != "maismilionaria" && count > 6) 4 else 3

internal fun homeResultMoney(value: String): String = value.toBigDecimalOrNull()?.let(::formatBrazilianCurrency) ?: value

internal fun homeResultWinners(count: Long): String = when (count) {
    0L -> "Sem ganhadores"
    1L -> "1 aposta ganhadora"
    else -> "$count apostas ganhadoras"
}

/** Os serviços legados também retornam as oito faixas da Dupla Sena na primeira lista. */
internal fun homeResultPayouts(result: DrawResult): Pair<List<DrawPayout>, List<DrawPayout>> {
    if (normalizeModality(result.modality) != "duplasena" || result.secondDrawPayouts.isNotEmpty()) {
        return result.payouts to result.secondDrawPayouts
    }
    val midpoint = (result.payouts.size + 1) / 2
    return result.payouts.take(midpoint) to result.payouts.drop(midpoint)
}

internal fun homeResultPayoutDescription(modality: String, description: String): String {
    val index = description.toIntOrNull()?.minus(1) ?: return description
    val suffix = messageText("numeros_acertados_934ae9")
    val labels = when (normalizeModality(modality)) {
        "megasena" -> listOf("Sena - 6", "Quina - 5", "Quadra - 4").map { it + suffix }

        "quina" -> listOf("Quina - 5", "Quadra - 4", "Terno - 3", "Duque - 2").map { it + suffix }

        "duplasena" -> List(2) { listOf("Sena - 6", "Quina - 5", "Quadra - 4", "Terno - 3") }.flatten().map { it + suffix }

        "lotofacil" -> (15 downTo 11).map { "$it$suffix" }

        "lotomania" -> (20 downTo 15).map { "$it$suffix" } + "0 acertos"

        "diadesorte" -> (7 downTo 4).map { "$it$suffix" } + messageText("mes_de_sorte_4fd420")

        "timemania" -> (7 downTo 3).map { "$it$suffix" } + messageText("time_do_coracao_3a182e")

        "supersete" -> (7 downTo 3).map { "$it$suffix" }

        "loteca" -> listOf("1º (14 jogos acertados)", "2º (13 jogos acertados)")

        "lotogol" -> listOf("1º (5 acertos)", "2º (4 acertos)", "3º (3 acertos)")

        "maismilionaria" -> listOf(
            messageText("6_numeros_2_trevos_acertados_bf714e"), messageText("6_numeros_1_ou_6_0_trevos_acerta_78c0c2"),
            messageText("5_numeros_2_trevos_acertados_d08e75"), messageText("5_numeros_e_1_ou_0_trevos_acerta_76578f"),
            messageText("4_numeros_e_2_trevos_acertados_195c27"), messageText("4_numeros_e_1_ou_0_trevos_acerta_af30a9"),
            messageText("3_numeros_e_2_trevos_acertados_bb86bb"), messageText("3_numeros_e_1_trevo_acertados_1dae8c"),
            messageText("2_numeros_e_2_trevos_acertados_220ba0"), messageText("2_numeros_e_1_trevo_acertados_92a7be")
        )

        else -> emptyList()
    }
    return labels.getOrNull(index) ?: description
}

internal fun homeResultDate(value: String): String {
    val parts = value.split(' ')
    return if (parts.size == 3) {
        parts.mapIndexed { index, word ->
            if (index == 1) word.replaceFirstChar(Char::uppercaseChar) else word
        }.joinToString(" ")
    } else {
        value
    }
}
