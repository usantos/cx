package br.gov.caixa.loterias.core.presentation.rule

import br.gov.caixa.loterias.core.presentation.formatting.localizedMessage as messageText

// Texto explicativo literal do AccordionModel legado canônico.
fun lotteryGameRulesHtml(modalityId: Int): String = when (modalityId) {
    11 -> messageText("escolha_de_7_a_15_numeros_entre_41efca")
    18 -> messageText("escolha_de_6_a_15_numeros_entre_093c29")
    13, 19 -> messageText("escolha_os_resultados_dos_jogos_fe3e2d")
    8 -> messageText("escolha_de_15_a_20_numeros_entre_562def")
    5, 16 -> messageText("escolha_50_numeros_entre_os_100_2c8ac4")
    2 -> messageText("escolha_de_6_a_20_numeros_entre_667389")
    1, 3 -> messageText("escolha_de_5_a_15_numeros_entre_8f6942")
    20 -> messageText("escolha_10_numeros_entre_os_80_d_0f5ae8")
    7 -> messageText("o_super_sete_tem_7_colunas_cada_84c6ee")
    9 -> messageText("escolha_de_6_a_12_numeros_entre_17d285")
    else -> ""
}
