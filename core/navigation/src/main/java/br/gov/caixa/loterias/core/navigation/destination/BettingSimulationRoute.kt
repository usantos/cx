package br.gov.caixa.loterias.core.navigation.destination

import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters

/** Transporta o concurso validado durante a atualização independente dos parâmetros do simulador. */
object BettingSimulationRoute {
    const val pattern = "betting/simulation/{modalityId}?contestTypeId={contestTypeId}&contestNumber={contestNumber}"

    fun from(game: LotteryGameParameters): String {
        val type = if (game.contestType.equals("ESPECIAL", ignoreCase = true)) 2 else 1
        return "betting/simulation/${game.modality.id}?contestTypeId=$type&contestNumber=${game.contestNumber}"
    }
}
