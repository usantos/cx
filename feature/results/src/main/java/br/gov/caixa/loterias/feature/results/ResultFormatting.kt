package br.gov.caixa.loterias.feature.results

import java.text.NumberFormat
import java.util.Locale

internal fun resultMoney(value: String): String = value.toBigDecimalOrNull()?.let {
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(it)
} ?: value

internal fun resultWinners(count: Long): String = when (count) {
    0L -> "Sem ganhadores"
    1L -> "1 aposta ganhadora"
    else -> "$count apostas ganhadoras"
}

/** Faixas de PartidaViewDetalhesResultado.obterMapFaixa; non-code descriptions remain readable. */
internal fun resultPayoutDescription(modality: String, description: String): String {
    val index = description.toIntOrNull()?.minus(1) ?: return description
    val correctNumbers = " números acertados"
    val names = when (modality) {
        "megasena" -> listOf("Sena - 6", "Quina - 5", "Quadra - 4").map { it + correctNumbers }
        "quina" -> listOf("Quina - 5", "Quadra - 4", "Terno - 3", "Duque - 2").map { it + correctNumbers }
        "duplasena" -> List(2) { listOf("Sena - 6", "Quina - 5", "Quadra - 4", "Terno - 3") }.flatten().map { it + correctNumbers }
        "lotofacil" -> (15 downTo 11).map { "$it$correctNumbers" }
        "lotomania" -> (20 downTo 15).map { "$it$correctNumbers" } + "0 acertos"
        "diadesorte" -> (7 downTo 4).map { "$it$correctNumbers" } + "Mês de sorte"
        "timemania" -> (7 downTo 3).map { "$it$correctNumbers" } + "Time do Coração"
        "supersete" -> (7 downTo 3).map { "$it$correctNumbers" }
        "loteca" -> listOf("1º (14 jogos acertados)", "2º (13 jogos acertados)")
        "maismilionaria" -> listOf(
            "6 números +2 trevos acertados", "6 números +1 ou 6 +0 trevos acertados",
            "5 números +2 trevos acertados", "5 números e 1 ou 0 trevos acertados",
            "4 números e 2 trevos acertados", "4 números e 1 ou 0 trevos acertados",
            "3 números e 2 trevos acertados", "3 números e 1 trevo acertados",
            "2 números e 2 trevos acertados", "2 números e 1 trevo acertados",
        )
        else -> emptyList()
    }
    return names.getOrNull(index) ?: description
}
