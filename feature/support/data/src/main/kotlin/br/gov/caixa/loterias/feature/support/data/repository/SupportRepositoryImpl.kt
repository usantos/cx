package br.gov.caixa.loterias.feature.support.data.repository

import br.gov.caixa.loterias.core.network.dto.support.SupportQuestionDto
import br.gov.caixa.loterias.core.network.dto.support.SupportSectionDto
import br.gov.caixa.loterias.feature.support.data.datasource.remote.SupportRemoteDataSource
import br.gov.caixa.loterias.feature.support.domain.model.SupportQuestion
import br.gov.caixa.loterias.feature.support.domain.model.SupportSection
import br.gov.caixa.loterias.feature.support.domain.repository.SupportRepository

class SupportRepositoryImpl(
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

fun SupportSectionDto.toDomain() = SupportSection(
    id = id ?: 0,
    title = name.orEmpty().trim(),
    questions = questions.orEmpty().map(SupportQuestionDto::toDomain),
)

fun SupportQuestionDto.toDomain() = SupportQuestion(
    id = id ?: 0,
    title = question.orEmpty().trim(),
    answer = answer.orEmpty().trim(),
)
