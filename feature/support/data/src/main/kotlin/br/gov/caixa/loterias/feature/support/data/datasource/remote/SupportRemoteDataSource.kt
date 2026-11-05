package br.gov.caixa.loterias.feature.support.data.datasource.remote

import br.gov.caixa.loterias.core.network.dto.support.SupportSectionDto
import br.gov.caixa.loterias.core.network.dto.support.TermsDto

interface SupportRemoteDataSource {
    suspend fun sections(): List<SupportSectionDto>?
    suspend fun questions(sectionId: Long): List<SupportSectionDto>?
    suspend fun terms(): TermsDto?
    suspend fun acceptTerms()
}
