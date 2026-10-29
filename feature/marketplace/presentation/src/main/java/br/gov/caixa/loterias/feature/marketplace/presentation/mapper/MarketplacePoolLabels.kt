package br.gov.caixa.loterias.feature.marketplace.presentation.mapper

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplacePool
import br.gov.caixa.loterias.feature.marketplace.domain.model.MarketplaceSpecialContests
import br.gov.caixa.loterias.feature.marketplace.presentation.formatting.localizedMessage as messageText
import java.util.Locale

private fun modalityKey(value: String) = value.uppercase(Locale.ROOT).replace('-', '_').replace(' ', '_')

internal fun marketplaceModalityNames(games: List<LotteryGameParameters>): Map<Pair<String, Int>, String> = games.associate { game ->
    val special = game.contestType.equals("ESPECIAL", true) || game.contestType.equals("E", true)
    (modalityKey(game.modality.code) to if (special) 2 else 1) to
        (if (special) game.modality.specialName else "").lowercase(Locale.forLanguageTag("pt-BR"))
}

internal fun marketplacePoolModalityLabel(
    pool: MarketplacePool,
    names: Map<Pair<String, Int>, String>,
    specialContests: MarketplaceSpecialContests = MarketplaceSpecialContests(),
): String {
    specialContests.label(pool.modality, pool.contestType, pool.contest)?.let { return it }
    val code = modalityKey(pool.modality)
    val normal = normalPoolModalityLabel(pool, code)
    if (pool.contestType != 2) return normal
    val fallback = when (code) {
        "MEGA_SENA", "MEGASENA" -> messageText("mega_da_virada_4113fa")
        "QUINA" -> messageText("quina_de_sao_joao_a3337a")
        "LOTOFACIL", "LOTOFÁCIL" -> messageText("lotofacil_da_independencia_86aece")
        "DUPLA_SENA" -> messageText("dupla_de_pascoa_68c9b0")
        "LOTECA" -> "loteca especial"
        else -> normal
    }
    val name = names[code to pool.contestType].orEmpty().ifBlank { fallback }
    return specialPoolDisplayName(name)
}

private fun normalPoolModalityLabel(pool: MarketplacePool, code: String): String = when (code) {
    "MEGA_SENA", "MEGASENA" -> "mega sena"
    "LOTOFACIL", "LOTOFÁCIL" -> messageText("lotofacil_dbb2e4")
    "SUPER_7", "SUPER_SETE" -> "super sete"
    "MAIS_MILIONARIA", "MAIS_MILIONÁRIA" -> messageText("milionaria_c791e4")
    else -> pool.modality.replace('_', ' ').lowercase(Locale.forLanguageTag("pt-BR"))
}

private fun specialPoolDisplayName(name: String): String = when (name) {
    "mega da virada" -> messageText("mega_da_virada_ce5f36")
    "quina de são joão" -> messageText("quina_de_sao_joao_6cc954")
    "lotofácil da independência" -> messageText("lotofacil_da_independencia_5a2372")
    "dupla de páscoa" -> messageText("dupla_de_pascoa_6da0ff")
    "loteca especial" -> "loteca \nespecial"
    else -> name
}
