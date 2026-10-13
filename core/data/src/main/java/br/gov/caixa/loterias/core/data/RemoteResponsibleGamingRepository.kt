package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.domain.responsiblegaming.ResponsibleGamingRepository
import br.gov.caixa.loterias.core.domain.responsiblegaming.AccountSuspension
import br.gov.caixa.loterias.core.domain.responsiblegaming.SelfAssessmentAnswer
import br.gov.caixa.loterias.core.domain.responsiblegaming.SelfAssessmentQuestion
import br.gov.caixa.loterias.core.domain.responsiblegaming.SocialTransfers
import br.gov.caixa.loterias.core.network.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.core.network.SuspendAccountRequestDto
import java.math.BigDecimal

class RemoteResponsibleGamingRepository(
    private val remote: ResponsibleGamingRemoteDataSource,
) : ResponsibleGamingRepository {
    override suspend fun assessmentQuestions(): List<SelfAssessmentQuestion> =
        requireNotNull(remote.assessmentQuestions()) { "Autoavaliação sem payload" }
            .questions.orEmpty()
            .filter { it.active != false }
            .map {
                SelfAssessmentQuestion(
                    id = it.id ?: 0,
                    description = it.description.orEmpty().trim(),
                    order = it.order ?: Int.MAX_VALUE,
                )
            }

    override suspend fun submitAssessment(answers: List<SelfAssessmentAnswer>) {
        remote.submitAssessment(
            answers.map {
                SelfAssessmentAnswerDto(
                    questionId = it.questionId,
                    description = it.description,
                    affirmative = it.affirmative,
                )
            },
        )
    }

    override suspend fun socialTransfers(): SocialTransfers {
        val dto = requireNotNull(remote.socialTransfers()) { "Repassômetro sem payload" }
        return SocialTransfers(
            accumulatedRevenue = dto.accumulatedRevenue.orZero(),
            lastWeekRevenue = dto.lastWeekRevenue.orZero(),
            baseDate = dto.baseDate.orEmpty(),
            socialProgramsPercentage = dto.socialProgramsPercentage.orZero(),
            educationPercentage = dto.educationPercentage.orZero(),
            culturePercentage = dto.culturePercentage.orZero(),
            sportPercentage = dto.sportPercentage.orZero(),
            securityPercentage = dto.securityPercentage.orZero(),
            socialSecurityPercentage = dto.socialSecurityPercentage.orZero(),
            otherPercentage = dto.otherPercentage.orZero(),
        )
    }

    override suspend fun suspendAccount(periodHours: Int): AccountSuspension {
        val cpf = requireNotNull(remote.currentUserProfile()?.cpf)
            .filter(Char::isDigit)
            .also { require(it.length == 11) { "CPF do apostador indisponível" } }
        val response = requireNotNull(
            remote.suspendAccount(SuspendAccountRequestDto(cpf, periodHours)),
        ) { "Suspensão sem payload" }
        return AccountSuspension(
            active = response.active == true,
            duration = response.duration.orEmpty(),
        )
    }

    private fun BigDecimal?.orZero() = this ?: BigDecimal.ZERO
}
