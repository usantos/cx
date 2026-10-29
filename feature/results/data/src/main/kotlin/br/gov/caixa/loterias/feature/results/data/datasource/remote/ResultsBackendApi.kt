package br.gov.caixa.loterias.feature.results.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.BackendApi
import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface ResultsBackendApi {
    /** Chaves concursos repetidas são exigidas pelo contrato instalado de faixa de resultados. */
    @GET("resultados")
    suspend fun resultsForContests(
        @Query("modalidade") modality: String,
        @Query("concursos") contests: List<Int>,
    ): ApiEnvelope<List<DrawResultDto>>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun BackendApi.featureResultsApi(): ResultsBackendApi = ResultsBackendApiProjection(this)

private class ResultsBackendApiProjection(private val transport: BackendApi) : ResultsBackendApi {
    override suspend fun resultsForContests(
        @Query("modalidade") modality: String,
        @Query("concursos") contests: List<Int>,
    ): ApiEnvelope<List<DrawResultDto>> = transport.resultsForContests(modality = modality, contests = contests)
}
