package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.support.SupportQuestion
import br.gov.caixa.loterias.core.domain.support.SupportRepository
import br.gov.caixa.loterias.core.domain.support.SupportSection
import br.gov.caixa.loterias.core.network.SupportQuestionDto
import br.gov.caixa.loterias.core.network.SupportSectionDto

class RemoteSupportRepository(
    private val remote: SupportRemoteDataSource,
) : SupportRepository {
    override suspend fun sections(): List<SupportSection> =
        remote.sections().orEmpty().map(SupportSectionDto::toDomain)

    override suspend fun questions(sectionId: Long): List<SupportQuestion> =
        remote.questions(sectionId).orEmpty()
            .flatMap { it.questions.orEmpty() }
            .map(SupportQuestionDto::toDomain)

    override suspend fun terms(): String = remote.terms()?.content.orEmpty()

    override suspend fun acceptTerms() {
        remote.acceptTerms()
    }
}

internal fun SupportSectionDto.toDomain() = SupportSection(
    id = id ?: 0,
    title = name.orEmpty().trim(),
    questions = questions.orEmpty().map(SupportQuestionDto::toDomain),
)

internal fun SupportQuestionDto.toDomain() = SupportQuestion(
    id = id ?: 0,
    title = question.orEmpty().trim(),
    answer = answer.orEmpty().trim(),
)
