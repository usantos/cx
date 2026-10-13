package br.gov.caixa.loterias.core.domain.support

data class SupportQuestion(
    val id: Long,
    val title: String,
    val answer: String,
)

data class SupportSection(
    val id: Long,
    val title: String,
    val questions: List<SupportQuestion>,
)

interface SupportRepository {
    suspend fun sections(): List<SupportSection>
    suspend fun questions(sectionId: Long): List<SupportQuestion>
    suspend fun terms(): String
    suspend fun acceptTerms()
}
