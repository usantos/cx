package br.gov.caixa.loterias.feature.results.data.datasource.remote

import br.gov.caixa.loterias.feature.results.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto
import br.gov.caixa.loterias.core.network.provider.LoteriasApiProvider
import br.gov.caixa.loterias.core.network.provider.BackendApiProvider
import br.gov.caixa.loterias.core.data.mapper.checkedPayload

class ResultsRemoteDataSourceImpl(private val apiProvider: LoteriasApiProvider, private val legacyApiProvider: BackendApiProvider? = null) :
    ResultsRemoteDataSource {
    override suspend fun sportsTeamShields(): Map<Int, String> = try {
        legacyApiProvider?.current()?.sportsTeams()?.checkedPayload().orEmpty()
            .mapNotNull { shield ->
                val id = shield.team?.id?.toInt() ?: return@mapNotNull null
                val image = shield.imageBase64?.takeIf(String::isNotBlank) ?: return@mapNotNull null
                id to image
            }.toMap()
    } catch (failure: retrofit2.HttpException) {
        // Os repositórios consomem falhas de IO independentes de transporte para este catálogo opcional.
        throw java.io.IOException("Não foi possível carregar os escudos dos times", failure)
    }

    override suspend fun latestResult(modality: String) = apiProvider.current().featureResultsApi().latestResult(modality).payload

    override suspend fun resultByContest(modality: String, contest: Int) =
        apiProvider.current().featureResultsApi().resultByContest(modality, contest).payload

    override suspend fun resultsForContests(code: String, contests: List<Int>): List<DrawResultDto> = requireNotNull(
        requireNotNull(legacyApiProvider).current().featureResultsApi().resultsForContests(code, contests).checkedPayload()
    ) {
        messageText("resultados_sem_payload_cfdd7a")
    }
}
