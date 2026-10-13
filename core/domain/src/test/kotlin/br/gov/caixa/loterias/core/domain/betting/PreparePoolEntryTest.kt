package br.gov.caixa.loterias.core.domain.betting

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.*
import br.gov.caixa.loterias.core.domain.onboarding.*
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class PreparePoolEntryTest {
    private var access: AppResult<AccountAccess> = AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
    private var accessCalls = 0
    private var reads = 0
    private var dismissed = false
    private val repository = object : OnboardingRepository {
        override fun isIntroductionCompleted(): Boolean = error("Unexpected introduction read")
        override fun completeIntroduction(): Unit = error("Unexpected write")
        override fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean): Unit = error("Entry must not dismiss tutorial")
        override fun isTutorialDismissed(tutorial: BettingTutorial): Boolean {
            assertEquals(BettingTutorial.POOL, tutorial)
            reads++
            return dismissed
        }
    }
    private val prepare = PreparePoolEntry(VerifyAccountAccessUseCase { accessCalls++; access }, repository)
    private val request = PoolEntryRequest(2, "ESPECIAL")

    @Test fun `only legacy dismissal decides tutorial after account approval`() = runTest {
        assertEquals(AppResult.Success(PoolEntryDecision.Ready(request, true)), prepare(request.copy(contestType = "especial")))
        dismissed = true
        assertEquals(AppResult.Success(PoolEntryDecision.Ready(request, false)), prepare(request))
        assertEquals(2, reads)
    }

    @Test fun `suspension prevents tutorial and marketplace`() = runTest {
        access = AppResult.Success(AccountAccess(PostLoginDestination.HOME, true, BigDecimal.ZERO))
        val error = (prepare(request) as AppResult.Failure).error as AppError.Validation
        assertTrue(error.reason.startsWith("Há uma suspensão temporária ativa para compras."))
        assertEquals(0, reads)
    }

    @Test fun `failure and account requirements do not consult or change preferences`() = runTest {
        access = AppResult.Failure(AppError.Network)
        assertEquals(access, prepare(request))
        PostLoginDestination.entries.filter { it != PostLoginDestination.HOME }.forEach {
            access = AppResult.Success(AccountAccess(it, false, BigDecimal.ZERO))
            assertEquals(AppResult.Success(PoolEntryDecision.CompleteAccount(it)), prepare(request))
        }
        assertEquals(0, reads)
    }

    @Test fun `invalid selection cannot start entry`() = runTest {
        listOf(request.copy(modalityId = 0), request.copy(modalityId = -1), request.copy(contestType = ""),
            request.copy(contestType = "OTHER")).forEach { assertTrue(prepare(it) is AppResult.Failure) }
        assertEquals(0, accessCalls)
        val normal = request.copy(contestType = "NORMAL")
        assertEquals(AppResult.Success(PoolEntryDecision.Ready(normal, true)), prepare(normal))
    }
}
