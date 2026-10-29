package br.gov.caixa.loterias.core.domain.betting

import org.junit.Assert.*
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AccountAccess
import br.gov.caixa.loterias.feature.account.domain.model.PostLoginDestination
import br.gov.caixa.loterias.feature.account.domain.usecase.VerifyAccountAccessUseCase
import br.gov.caixa.loterias.feature.onboarding.domain.model.BettingTutorial
import br.gov.caixa.loterias.feature.onboarding.domain.repository.OnboardingRepository
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Test
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryDecision
import br.gov.caixa.loterias.feature.betting.domain.model.PoolEntryRequest
import br.gov.caixa.loterias.feature.betting.domain.usecase.PreparePoolEntry

class PreparePoolEntryTest {
    private var access: AppResult<AccountAccess> = AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.ZERO))
    private var accessCalls = 0
    private var reads = 0
    private var dismissed = false
    private val repository = object : OnboardingRepository {
        override suspend fun isIntroductionCompleted(): Boolean = error("Unexpected introduction read")
        override suspend fun completeIntroduction(): Unit = error("Unexpected write")
        override suspend fun setTutorialDismissed(tutorial: BettingTutorial, dismissed: Boolean): Unit =
            error("Entry must not dismiss tutorial")
        override suspend fun isTutorialDismissed(tutorial: BettingTutorial): Boolean {
            assertEquals(BettingTutorial.POOL, tutorial)
            reads++
            return dismissed
        }
    }
    private val prepare = PreparePoolEntry(
        VerifyAccountAccessUseCase {
            accessCalls++
            access
        },
        repository
    )
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
        listOf(
            request.copy(modalityId = 0),
            request.copy(modalityId = -1),
            request.copy(contestType = ""),
            request.copy(contestType = "OTHER")
        ).forEach { assertTrue(prepare(it) is AppResult.Failure) }
        assertEquals(0, accessCalls)
        val normal = request.copy(contestType = "NORMAL")
        assertEquals(AppResult.Success(PoolEntryDecision.Ready(normal, true)), prepare(normal))
    }
}
