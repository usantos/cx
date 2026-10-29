package br.gov.caixa.loterias.feature.responsiblegaming.data.repository

import br.gov.caixa.loterias.feature.responsiblegaming.data.localizedMessage as messageText

import br.gov.caixa.loterias.feature.responsiblegaming.domain.repository.ResponsibleGamingRepository
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.AccountSuspension
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentAnswer
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SelfAssessmentQuestion
import br.gov.caixa.loterias.feature.responsiblegaming.domain.model.SocialTransfers
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SelfAssessmentAnswerDto
import br.gov.caixa.loterias.feature.responsiblegaming.data.dto.SuspendAccountRequestDto
import java.math.BigDecimal
import br.gov.caixa.loterias.feature.responsiblegaming.data.datasource.remote.ResponsibleGamingRemoteDataSource

class ResponsibleGamingRepositoryImpl(private val remote: ResponsibleGamingRemoteDataSource) : ResponsibleGamingRepository {
    override suspend fun assessmentQuestions(): List<SelfAssessmentQuestion> =
        requireNotNull(remote.assessmentQuestions()) { messageText("autoavaliacao_sem_payload_33d037") }
            .questions.orEmpty()
            .filter { it.active != false }
            .map {
                SelfAssessmentQuestion(
                    id = it.id ?: 0,
                    description = it.description.orEmpty().trim(),
                    order = it.order ?: Int.MAX_VALUE
                )
            }

    override suspend fun submitAssessment(answers: List<SelfAssessmentAnswer>) {
        remote.submitAssessment(
            answers.map {
                SelfAssessmentAnswerDto(
                    questionId = it.questionId,
                    description = it.description,
                    affirmative = it.affirmative
                )
            }
        )
    }

    override suspend fun socialTransfers(): SocialTransfers {
        val dto = requireNotNull(remote.socialTransfers()) { messageText("repassometro_sem_payload_d61f0b") }
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
            otherPercentage = dto.otherPercentage.orZero()
        )
    }

    override suspend fun suspendAccount(periodHours: Int): AccountSuspension {
        val cpf = requireNotNull(remote.currentUserProfile()?.cpf)
            .filter(Char::isDigit)
            .also { require(it.length == 11) { messageText("cpf_do_apostador_indisponivel_1e300c") } }
        val response = requireNotNull(
            remote.suspendAccount(SuspendAccountRequestDto(cpf, periodHours))
        ) { messageText("suspensao_sem_payload_9b402b") }
        return AccountSuspension(
            active = response.active == true,
            duration = response.duration.orEmpty()
        )
    }

    private fun BigDecimal?.orZero() = this ?: BigDecimal.ZERO
}
