package br.gov.caixa.loterias.feature.support.data.datasource.remote

import br.gov.caixa.loterias.feature.support.data.dto.SupportSectionDto
import br.gov.caixa.loterias.feature.support.data.dto.TermsDto

interface SupportRemoteDataSource {
    suspend fun sections(): List<SupportSectionDto>?
    suspend fun questions(sectionId: Long): List<SupportSectionDto>?
    suspend fun terms(): TermsDto?
    suspend fun acceptTerms()
}
