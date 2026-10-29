package br.gov.caixa.loterias.feature.results.presentation.mapper

import br.gov.caixa.loterias.core.presentation.formatting.formatBrazilianCurrency
import br.gov.caixa.loterias.feature.results.presentation.formatting.localizedMessage as messageText

internal fun resultMoney(value: String): String = value.toBigDecimalOrNull()?.let {
    formatBrazilianCurrency(it)
} ?: value

internal fun resultWinners(count: Long): String = when (count) {
    0L -> "Sem ganhadores"
    1L -> "1 aposta ganhadora"
    else -> "$count apostas ganhadoras"
}

/** Faixas de PartidaViewDetalhesResultado.obterMapFaixa; descrições que não são códigos permanecem legíveis. */
internal fun resultPayoutDescription(modality: String, description: String): String {
    val index = description.toIntOrNull()?.minus(1) ?: return description
    val correctNumbers = messageText("numeros_acertados_934ae9")
    val names = when (modality) {
        "megasena" -> listOf("Sena - 6", "Quina - 5", "Quadra - 4").map { it + correctNumbers }

        "quina" -> listOf("Quina - 5", "Quadra - 4", "Terno - 3", "Duque - 2").map { it + correctNumbers }
        "duplasena" -> List(2) { listOf("Sena - 6", "Quina - 5", "Quadra - 4", "Terno - 3") }.flatten().map { it + correctNumbers }
        "lotofacil" -> (15 downTo 11).map { "$it$correctNumbers" }
        "lotomania" -> (20 downTo 15).map { "$it$correctNumbers" } + "0 acertos"
        "diadesorte" -> (7 downTo 4).map { "$it$correctNumbers" } + messageText("mes_de_sorte_4fd420")
        "timemania" -> (7 downTo 3).map { "$it$correctNumbers" } + messageText("time_do_coracao_3a182e")
        "supersete" -> (7 downTo 3).map { "$it$correctNumbers" }

        "loteca" -> listOf("1º (14 jogos acertados)", "2º (13 jogos acertados)")

        "maismilionaria" -> listOf(
            messageText("6_numeros_2_trevos_acertados_bf714e"), messageText("6_numeros_1_ou_6_0_trevos_acerta_78c0c2"),
            messageText("5_numeros_2_trevos_acertados_d08e75"), messageText("5_numeros_e_1_ou_0_trevos_acerta_76578f"),
            messageText("4_numeros_e_2_trevos_acertados_195c27"), messageText("4_numeros_e_1_ou_0_trevos_acerta_af30a9"),
            messageText("3_numeros_e_2_trevos_acertados_bb86bb"), messageText("3_numeros_e_1_trevo_acertados_1dae8c"),
            messageText("2_numeros_e_2_trevos_acertados_220ba0"), messageText("2_numeros_e_1_trevo_acertados_92a7be")
        )

        else -> emptyList()
    }
    return names.getOrNull(index) ?: description
}
