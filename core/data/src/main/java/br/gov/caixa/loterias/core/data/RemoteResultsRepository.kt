package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.results.DrawPayout
import br.gov.caixa.loterias.core.domain.results.DrawResult
import br.gov.caixa.loterias.core.domain.results.ResultsRepository
import br.gov.caixa.loterias.core.network.DrawResultDto
import com.google.gson.JsonElement

class RemoteResultsRepository(
    private val remote: ResultsRemoteDataSource,
) : ResultsRepository {
    override suspend fun latest(modality: String): DrawResult {
        require(modality.isNotBlank()) { "Modalidade obrigatória" }
        return requireNotNull(remote.latestResult(modality)) { "Resultado sem payload" }.toDomain()
    }

    override suspend fun byContest(modality: String, contest: Int): DrawResult {
        require(modality.isNotBlank()) { "Modalidade obrigatória" }
        require(contest > 0) { "Concurso inválido" }
        return requireNotNull(remote.resultByContest(modality, contest)) { "Resultado sem payload" }.toDomain()
    }

    private fun DrawResultDto.toDomain(): DrawResult {
        val location = listOfNotNull(
            city?.name?.trim()?.takeIf(String::isNotEmpty),
            (city?.state?.abbreviation ?: state?.abbreviation)?.trim()?.takeIf(String::isNotEmpty),
        ).joinToString(" - ")
        return DrawResult(
            modality = contest?.modality.orEmpty(),
            contest = contest?.number ?: 0,
            drawDate = contest?.drawDate.orEmpty(),
            drawLocation = location,
            firstDrawNumbers = firstDrawNumbers.flattenNumbers(),
            secondDrawNumbers = secondDrawNumbers.orEmpty(),
            clovers = clovers.orEmpty(),
            accumulated = accumulated == true,
            accumulatedNextContest = accumulatedNextContest.orEmpty(),
            totalRevenue = totalRevenue.orEmpty(),
            notice = notice.orEmpty(),
            payouts = payouts.orEmpty().map {
                DrawPayout(it.description.orEmpty(), it.winners ?: 0L, it.value.orEmpty())
            },
        )
    }

    private fun JsonElement?.flattenNumbers(): List<Int> = when {
        this == null || isJsonNull -> emptyList()
        isJsonArray -> asJsonArray.flatMap { it.flattenNumbers() }
        isJsonPrimitive && asJsonPrimitive.isNumber -> listOf(asInt)
        else -> emptyList()
    }
}
