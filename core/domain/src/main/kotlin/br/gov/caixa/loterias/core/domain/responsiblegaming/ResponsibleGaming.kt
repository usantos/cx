package br.gov.caixa.loterias.core.domain.responsiblegaming

import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.common.appResultOf
import java.math.BigDecimal

data class SelfAssessmentQuestion(
    val id: Int,
    val description: String,
    val order: Int,
)

data class SelfAssessmentAnswer(
    val questionId: Int,
    val description: String,
    val affirmative: Boolean,
)

data class SocialTransfers(
    val accumulatedRevenue: BigDecimal,
    val lastWeekRevenue: BigDecimal,
    val baseDate: String,
    val socialProgramsPercentage: BigDecimal,
    val educationPercentage: BigDecimal,
    val culturePercentage: BigDecimal,
    val sportPercentage: BigDecimal,
    val securityPercentage: BigDecimal,
    val socialSecurityPercentage: BigDecimal,
    val otherPercentage: BigDecimal,
) {
    fun amountFor(percentage: BigDecimal): BigDecimal =
        accumulatedRevenue.multiply(percentage).divide(BigDecimal.valueOf(100))
}

data class AccountSuspension(
    val active: Boolean,
    val duration: String,
)

interface ResponsibleGamingRepository {
    suspend fun assessmentQuestions(): List<SelfAssessmentQuestion>
    suspend fun submitAssessment(answers: List<SelfAssessmentAnswer>)
    suspend fun socialTransfers(): SocialTransfers
    suspend fun suspendAccount(periodHours: Int): AccountSuspension
}

fun interface GetSelfAssessmentQuestionsUseCase {
    suspend operator fun invoke(): AppResult<List<SelfAssessmentQuestion>>
}

class GetSelfAssessmentQuestions(
    private val repository: ResponsibleGamingRepository,
) : GetSelfAssessmentQuestionsUseCase {
    override suspend fun invoke() = appResultOf {
        repository.assessmentQuestions()
            .filter { it.id > 0 && it.description.isNotBlank() }
            .sortedWith(compareBy(SelfAssessmentQuestion::order, SelfAssessmentQuestion::id))
            .also { require(it.isNotEmpty()) { "Autoavaliação sem perguntas" } }
    }
}

fun interface SubmitSelfAssessmentUseCase {
    suspend operator fun invoke(answers: List<SelfAssessmentAnswer>): AppResult<Unit>
}

class SubmitSelfAssessment(
    private val repository: ResponsibleGamingRepository,
) : SubmitSelfAssessmentUseCase {
    override suspend fun invoke(answers: List<SelfAssessmentAnswer>) = appResultOf {
        require(answers.isNotEmpty()) { "Respostas obrigatórias" }
        require(answers.map(SelfAssessmentAnswer::questionId).distinct().size == answers.size) {
            "Perguntas duplicadas"
        }
        repository.submitAssessment(answers)
    }
}

fun interface GetSocialTransfersUseCase {
    suspend operator fun invoke(): AppResult<SocialTransfers>
}

class GetSocialTransfers(
    private val repository: ResponsibleGamingRepository,
) : GetSocialTransfersUseCase {
    override suspend fun invoke() = appResultOf { repository.socialTransfers() }
}

fun interface SuspendAccountUseCase {
    suspend operator fun invoke(periodHours: Int): AppResult<AccountSuspension>
}

class SuspendAccount(
    private val repository: ResponsibleGamingRepository,
) : SuspendAccountUseCase {
    override suspend fun invoke(periodHours: Int) = appResultOf {
        require(periodHours in setOf(24, 48, 168, 360, 720)) { "Período de suspensão inválido" }
        repository.suspendAccount(periodHours)
    }
}
