package br.gov.caixa.loterias.feature.betting.presentation.mapper

import br.gov.caixa.loterias.feature.betting.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTECA_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.LOTOGOL_ID
import br.gov.caixa.loterias.feature.betting.presentation.state.TIMEMANIA_ID

internal fun recurringLabel(draws: Int): String = when (draws) {
    0 -> "Nenhum concurso"
    1 -> "1 concurso"
    else -> "$draws concursos"
}

internal fun fallbackBettingTitle(modalityId: Int): String = when (modalityId) {
    2 -> "Mega-Sena"
    3 -> "Quina"
    5 -> "Lotomania"
    7 -> "Super Sete"
    8 -> messageText("lotofacil_d3ed7c")
    9 -> messageText("milionaria_11be79")
    11 -> messageText("dia_de_sorte_b5c7a5")
    LOTOGOL_ID -> "Lotogol"
    18 -> "Dupla Sena"
    LOTECA_ID -> "Loteca"
    TIMEMANIA_ID -> "Timemania"
    else -> "Simular aposta"
}

internal fun specialHeaderTitle(modalityId: Int, backendName: String): String = when (modalityId) {
    2 -> messageText("mega_da_virada_ce5f36")
    3 -> messageText("quina_de_sao_joao_6cc954")
    8 -> messageText("lotofacil_da_independencia_5a2372")
    18 -> messageText("dupla_de_pascoa_6da0ff")
    19 -> "loteca especial"
    else -> backendName.ifBlank { "" }.lowercase()
}

internal fun bettingHeaderDrawDate(value: String): String = Regex("^\\d{2}/\\d{2}").find(value)?.value.orEmpty()
