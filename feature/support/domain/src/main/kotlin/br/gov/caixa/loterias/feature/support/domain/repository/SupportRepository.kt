package br.gov.caixa.loterias.feature.support.domain.repository

import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion

interface SupportRepository {
    suspend fun sections(): List<SupportSection>
    suspend fun questions(sectionId: Long): List<SupportQuestion>
    suspend fun terms(): String
    suspend fun acceptTerms()
}
