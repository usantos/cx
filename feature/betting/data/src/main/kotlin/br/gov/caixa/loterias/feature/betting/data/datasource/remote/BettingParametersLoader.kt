package br.gov.caixa.loterias.feature.betting.data.datasource.remote

import br.gov.caixa.loterias.feature.betting.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import kotlinx.coroutines.CancellationException
import br.gov.caixa.loterias.feature.betting.data.mapper.toBettingParameters

internal class BettingParametersLoader(private val remote: BettingRemoteDataSource) {
    suspend fun parameters(): BettingParameters {
        val dto = requireNotNull(remote.bettingParameters()) { messageText("parametros_de_apostas_sem_payloa_8d006b") }
        val hasSportsContent = dto.parameters.orEmpty().any { parameter ->
            val game = parameter.game ?: parameter.nextGame
            !game?.teams.isNullOrEmpty() || !game?.matches.isNullOrEmpty()
        }
        val shields = if (hasSportsContent) {
            runCatching { remote.sportsTeamShields().orEmpty() }.getOrElse { failure ->
                if (failure is CancellationException) throw failure
                emptyList()
            }
                .associate { (it.team?.id?.toInt() ?: -1) to it.imageBase64 }
        } else {
            emptyMap()
        }
        return dto.toBettingParameters(shields)
    }
}
