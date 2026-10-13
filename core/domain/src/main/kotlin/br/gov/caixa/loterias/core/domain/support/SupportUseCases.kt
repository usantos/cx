package br.gov.caixa.loterias.core.domain.support

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf

fun interface GetSupportSectionsUseCase {
    suspend operator fun invoke(): AppResult<List<SupportSection>>
}

class GetSupportSections(
    private val repository: SupportRepository,
) : GetSupportSectionsUseCase {
    override suspend fun invoke() = appResultOf { repository.sections() }
}

fun interface GetSupportQuestionsUseCase {
    suspend operator fun invoke(sectionId: Long): AppResult<List<SupportQuestion>>
}

class GetSupportQuestions(
    private val repository: SupportRepository,
) : GetSupportQuestionsUseCase {
    override suspend fun invoke(sectionId: Long) = appResultOf { repository.questions(sectionId) }
}

fun interface GetTermsUseCase {
    suspend operator fun invoke(): AppResult<String>
}

class GetTerms(
    private val repository: SupportRepository,
) : GetTermsUseCase {
    override suspend fun invoke() = appResultOf { repository.terms() }
}

fun interface AcceptTermsUseCase {
    suspend operator fun invoke(): AppResult<Unit>
}

class AcceptTerms(
    private val repository: SupportRepository,
) : AcceptTermsUseCase {
    override suspend fun invoke() = appResultOf { repository.acceptTerms() }
}
