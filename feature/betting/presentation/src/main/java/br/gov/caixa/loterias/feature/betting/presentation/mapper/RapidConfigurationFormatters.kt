package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText

internal fun rapidFavoriteNumbers(bet: br.gov.caixa.loterias.feature.betting.domain.model.FavoriteBet): String = buildString {
    append(
        bet.numberColumns.joinToString(if (bet.isSuperSeven) "   " else " - ") { column ->
            column.joinToString(" - ") { it.toString().padStart(2, '0') }
        }
    )
    if (bet.heartTeam.isNotBlank()) append(messageText("time_do_coracao_1_s_73aec0", bet.heartTeam))
    if (bet.luckyMonth.isNotBlank()) append(messageText("mes_de_sorte_1_s_327387", bet.luckyMonth))
    if (bet.clovers.isNotEmpty()) append("\nTrevos: ${bet.clovers.joinToString(" - ")}")
}
