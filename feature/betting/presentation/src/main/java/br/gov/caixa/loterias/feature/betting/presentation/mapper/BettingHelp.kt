package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.core.presentation.rule.lotteryGameRulesHtml
import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText

internal fun legacyBettingHelp(modalityId: Int): String = lotteryGameRulesHtml(modalityId)

/** AccordionModel.getTituloComoJogar usa ModalidadeEnum.fromString, e não o rótulo do servidor. */
internal fun legacyBettingHelpTitle(modalityId: Int, fallbackName: String): String {
    val name = when (modalityId) {
        1 -> "quina"
        2 -> "mega-sena"
        5 -> "lotomania"
        7 -> "super sete"
        8 -> messageText("lotofacil_dbb2e4")
        9 -> messageText("milionaria_c791e4")
        11 -> messageText("dia_de_sorte_71bc30")
        14 -> "lotogol"
        18 -> "dupla sena"
        19 -> "loteca"
        20 -> "timemania"
        else -> fallbackName
    }
    return "Como Jogar: $name"
}
