package br.gov.caixa.loterias.core.domain.responsiblegaming

import br.gov.caixa.loterias.core.common.AppResult
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponsibleGamingUseCasesTest {
    @Test
    fun `questions are filtered sorted and submitted`() = runTest {
        val repository = FakeRepository(
            questions = listOf(
                SelfAssessmentQuestion(3, "Terceira", 3),
                SelfAssessmentQuestion(0, "Inválida", 0),
                SelfAssessmentQuestion(1, " Primeira ", 1),
                SelfAssessmentQuestion(2, "", 2),
            ),
        )
        assertEquals(
            AppResult.Success(listOf(
                SelfAssessmentQuestion(1, " Primeira ", 1),
                SelfAssessmentQuestion(3, "Terceira", 3),
            )),
            GetSelfAssessmentQuestions(repository)(),
        )

        val answers = listOf(SelfAssessmentAnswer(1, "Não", false))
        assertEquals(AppResult.Success(Unit), SubmitSelfAssessment(repository)(answers))
        assertEquals(answers, repository.submittedAnswers)
    }

    @Test
    fun `invalid question and answer sets become validation failures`() = runTest {
        val emptyQuestions = GetSelfAssessmentQuestions(FakeRepository())()
        assertTrue(emptyQuestions is AppResult.Failure)

        val useCase = SubmitSelfAssessment(FakeRepository())
        assertTrue(useCase(emptyList()) is AppResult.Failure)
        assertTrue(useCase(listOf(
            SelfAssessmentAnswer(1, "Sim", true),
            SelfAssessmentAnswer(1, "Não", false),
        )) is AppResult.Failure)
    }

    @Test
    fun `social transfers delegates and calculates category amount`() = runTest {
        val social = socialTransfers()
        assertEquals(AppResult.Success(social), GetSocialTransfers(FakeRepository(social = social))())
        assertEquals(BigDecimal("480"), social.amountFor(BigDecimal("48")))
    }

    @Test
    fun `account suspension accepts only legacy periods`() = runTest {
        val repository = FakeRepository()
        assertEquals(AppResult.Success(AccountSuspension(true, "24 horas")), SuspendAccount(repository)(24))
        assertEquals(24, repository.suspensionHours)
        listOf(0, 23, 49, 1000).forEach {
            assertTrue(SuspendAccount(repository)(it) is AppResult.Failure)
        }
    }

    private class FakeRepository(
        private val questions: List<SelfAssessmentQuestion> = emptyList(),
        private val social: SocialTransfers = socialTransfers(),
    ) : ResponsibleGamingRepository {
        var submittedAnswers: List<SelfAssessmentAnswer> = emptyList()
        var suspensionHours: Int? = null
        override suspend fun assessmentQuestions() = questions
        override suspend fun submitAssessment(answers: List<SelfAssessmentAnswer>) {
            submittedAnswers = answers
        }
        override suspend fun socialTransfers() = social
        override suspend fun suspendAccount(periodHours: Int): AccountSuspension {
            suspensionHours = periodHours
            return AccountSuspension(true, "$periodHours horas")
        }
    }

    private companion object {
        fun socialTransfers() = SocialTransfers(
            accumulatedRevenue = BigDecimal("1000"),
            lastWeekRevenue = BigDecimal("100"),
            baseDate = "04/09/2026",
            socialProgramsPercentage = BigDecimal("48"),
            educationPercentage = BigDecimal("20"),
            culturePercentage = BigDecimal("10"),
            sportPercentage = BigDecimal("5"),
            securityPercentage = BigDecimal("4"),
            socialSecurityPercentage = BigDecimal("3"),
            otherPercentage = BigDecimal("6"),
        )
    }
}
