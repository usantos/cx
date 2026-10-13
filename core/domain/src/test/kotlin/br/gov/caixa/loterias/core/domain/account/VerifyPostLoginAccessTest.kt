package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import java.io.IOException
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class VerifyPostLoginAccessTest {
    @Test fun `blocked account verification precedes registration for every destination`() = runTest {
        PostLoginDestination.entries.forEach { destination ->
            val calls = mutableListOf<String>()
            val account = AccountAccess(destination, true, BigDecimal.TEN, AccountStatus.PARTIALLY_BLOCKED)
            val useCase = VerifyPostLoginAccess(
                { calls += "blocked" },
                { calls += "account"; account },
                Sessions(),
            )
            assertEquals(PostLoginAccessResult.Verified(account), useCase())
            assertEquals(listOf("blocked", "account"), calls)
        }
    }

    @Test fun `failed blocked check never proceeds to registration or exposes the failure detail`() = runTest {
        listOf(IOException("private"), IllegalArgumentException("private"), IllegalStateException("private")).forEach { failure ->
            val useCase = VerifyPostLoginAccess({ throw failure }, { error("Must not verify account") }, Sessions())
            assertEquals(PostLoginAccessResult.BlockedAccountCheckFailed, useCase())
        }
    }

    @Test fun `account verification errors are not misclassified as blocked check failures`() = runTest {
        listOf(IOException() to AppError.Network, IllegalStateException() to AppError.Unexpected,
            IllegalArgumentException("Invalid account") to AppError.Validation("Invalid account")).forEach { (exception, failure) ->
            assertEquals(PostLoginAccessResult.AccountCheckFailed(failure),
                VerifyPostLoginAccess({}, { throw exception }, Sessions())())
        }
    }

    @Test fun `cancellation in either step propagates without performing the next step`() = runTest {
        val cancellation = CancellationException("cancelled")
        for (cancelBlocked in listOf(true, false)) {
            var checks = 0
            val useCase = VerifyPostLoginAccess(
                { if (cancelBlocked) throw cancellation },
                { checks++; throw cancellation },
                Sessions(),
            )
            assertSame(cancellation, runCatching { useCase() }.exceptionOrNull())
            assertEquals(if (cancelBlocked) 0 else 1, checks)
        }
    }

    @Test fun `missing or already invalidated identity prevents both requests`() = runTest {
        for (missing in listOf(true, false)) {
            val sessions = Sessions().apply { available = !missing; valid = false }
            val result = VerifyPostLoginAccess({ error("No check") }, { error("No account") }, sessions)()
            assertEquals(expired, result)
        }
    }

    @Test fun `replacement during blocked check never starts registration or clears a newer login`() = runTest {
        for (fail in listOf(true, false)) {
            val sessions = Sessions()
            val result = VerifyPostLoginAccess({
                sessions.valid = false
                if (fail) throw IOException("Old request failed")
            }, { error("No account") }, sessions)()
            assertEquals(expired, result)
        }
    }

    @Test fun `registration uses captured identity and replacement discards either success or failure`() = runTest {
        for (fail in listOf(true, false)) {
            val sessions = Sessions()
            val result = VerifyPostLoginAccess({}, { identity ->
                assertSame(sessions.identity, identity)
                sessions.valid = false
                if (fail) throw IOException("Old registration failed")
                AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN)
            }, sessions)()
            assertEquals(expired, result)
        }
    }

    private val expired = PostLoginAccessResult.AccountCheckFailed(
        AppError.Validation("A sessão foi encerrada ou substituída. Entre novamente."),
    )

    private class Sessions : AuthenticatedSessionIdentityProvider {
        val identity = AuthenticatedSessionIdentity("a".repeat(64))
        var available = true
        var valid = true
        override fun captureIdentity() = if (available) identity else null
        override fun isCurrent(identity: AuthenticatedSessionIdentity) = valid && this.identity === identity
    }
}
