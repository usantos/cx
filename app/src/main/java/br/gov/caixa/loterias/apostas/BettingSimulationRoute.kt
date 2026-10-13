package br.gov.caixa.loterias.apostas

import br.gov.caixa.loterias.core.domain.betting.LotteryGameParameters

/** Carries the validated contest across the simulator's independent parameter refresh. */
internal object BettingSimulationRoute {
    const val pattern = "betting/simulation/{modalityId}?contestTypeId={contestTypeId}&contestNumber={contestNumber}"

    fun from(game: LotteryGameParameters): String {
        val type = if (game.contestType.equals("ESPECIAL", ignoreCase = true)) 2 else 1
        return "betting/simulation/${game.modality.id}?contestTypeId=$type&contestNumber=${game.contestNumber}"
    }
}
