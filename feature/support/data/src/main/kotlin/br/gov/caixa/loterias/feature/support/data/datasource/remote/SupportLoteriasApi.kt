package br.gov.caixa.loterias.feature.support.data.datasource.remote

import br.gov.caixa.loterias.core.network.model.ApiEnvelope
import br.gov.caixa.loterias.core.network.api.LoteriasApi
import br.gov.caixa.loterias.feature.support.data.dto.SupportSectionDto
import br.gov.caixa.loterias.feature.support.data.dto.TermsDto
import retrofit2.http.*

/** Contrato HTTP pertencente a esta funcionalidade; anotações de transporte e valores padrão originais são preservados. */
interface SupportLoteriasApi {
    @GET("duvidas/secoes")
    suspend fun supportSections(): ApiEnvelope<List<SupportSectionDto>>

    @GET("duvidas")
    suspend fun supportQuestions(@Query("secao") sectionId: Long): ApiEnvelope<List<SupportSectionDto>>

    @GET("termos-de-uso/termo-vigente")
    suspend fun currentTerms(): ApiEnvelope<TermsDto>

    @POST("termos-de-uso/aceitar-termo-vigente")
    suspend fun acceptCurrentTerms(): ApiEnvelope<Unit>
}

/** O receptor é o retrato do cliente já selecionado para esta operação. */
internal fun LoteriasApi.featureSupportApi(): SupportLoteriasApi = SupportLoteriasApiProjection(this)

private class SupportLoteriasApiProjection(private val transport: LoteriasApi) : SupportLoteriasApi {
    override suspend fun supportSections(): ApiEnvelope<List<SupportSectionDto>> = transport.supportSections()

    override suspend fun supportQuestions(@Query("secao") sectionId: Long): ApiEnvelope<List<SupportSectionDto>> = transport.supportQuestions(sectionId = sectionId)

    override suspend fun currentTerms(): ApiEnvelope<TermsDto> = transport.currentTerms()

    override suspend fun acceptCurrentTerms(): ApiEnvelope<Unit> = transport.acceptCurrentTerms()
}
