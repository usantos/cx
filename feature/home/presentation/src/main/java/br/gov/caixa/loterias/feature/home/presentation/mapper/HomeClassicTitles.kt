package br.gov.caixa.loterias.feature.home.presentation.mapper

import br.gov.caixa.loterias.feature.home.presentation.formatting.localizedMessage as messageText
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import java.util.Locale

/** ModalidadeEnum.fromString; somente ESPECIAL usa descricaoEspecial do servidor. */
internal fun homeClassicTitle(lottery: HomeLottery): String = when {
    lottery.kind == HomeLotteryKind.COMBO -> messageText("combos_de_apostas_d263ed")

    lottery.kind == HomeLotteryKind.INSTANT -> messageText("final_instant_title")

    lottery.contestType.equals("ESPECIAL", true) -> lottery.name.lowercase(Locale.forLanguageTag("pt-BR"))

    else -> when (canonicalModality(lottery.resultCode)) {
        "MEGASENA" -> messageText("final_mega_sena_title")
        "LOTOFACIL" -> messageText("lotofacil_dbb2e4")
        "DUPLASENA" -> messageText("final_dupla_sena_title")
        "DIADESORTE" -> messageText("dia_de_sorte_71bc30")
        "SUPERSETE", "SUPER7" -> messageText("final_super_seven_title")
        "MAISMILIONARIA" -> messageText("milionaria_c791e4")
        else -> lottery.resultCode.lowercase(Locale.forLanguageTag("pt-BR"))
    }
}
