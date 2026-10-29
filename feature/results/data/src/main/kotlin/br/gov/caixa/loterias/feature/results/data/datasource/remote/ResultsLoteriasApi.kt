package br.gov.caixa.loterias.feature.results.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.feature.results.data.dto.DrawResultDto
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface ResultsLoteriasApi {
    @GET("resultados/{modalidade}")
    suspend fun latestResult(@Path("modalidade") modality: String): ApiEnvelope<DrawResultDto>

    @GET("resultados/{modalidade}/{concurso}")
    suspend fun resultByContest(
        @Path("modalidade") modality: String,
        @Path("concurso") contest: Int,
    ): ApiEnvelope<DrawResultDto>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featureResultsApi(): ResultsLoteriasApi = ResultsLoteriasApiProjection(this)

private class ResultsLoteriasApiProjection(private val transport: LoteriasApi) : ResultsLoteriasApi {
    override suspend fun latestResult(@Path("modalidade") modality: String): ApiEnvelope<DrawResultDto> = transport.latestResult(modality = modality)

    override suspend fun resultByContest(
        @Path("modalidade") modality: String,
        @Path("concurso") contest: Int,
    ): ApiEnvelope<DrawResultDto> = transport.resultByContest(modality = modality, contest = contest)
}
