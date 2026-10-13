package br.gov.caixa.loterias.core.domain.account

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ReadLatestNotificationTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val notification = AccountNotification(7, null, null, null, null)

    @Test fun `close captures owner then reads remotely and only then clears exact notification`() = runTest {
        val calls = mutableListOf<String>()
        val session = session({ calls += "capture"; identity }, { owner, value ->
            assertSame(identity, owner); assertSame(notification, value); calls += "clear"; true
        })
        val useCase = ReadLatestNotification(session) { owner, id ->
            assertSame(identity, owner); assertEquals(7L, id); calls += "read"
            NotificationReadResult.Confirmed
        }
        assertEquals(AppResult.Success(NotificationReadResult.Confirmed), useCase(notification))
        assertEquals(listOf("capture", "read", "clear"), calls)
    }

    @Test fun `missing id dismisses without request while invalid id and other session never clear`() = runTest {
        val valid = ReadLatestNotification(session({ identity }, { _, _ -> error("No clear") })) { _, _ -> error("No request") }
        assertEquals(AppResult.Success(NotificationReadResult.Confirmed), valid(notification.copy(id = null)))
        assertTrue(valid(notification.copy(id = 0)) is AppResult.Failure)
        val stale = ReadLatestNotification(session({ null }, { _, _ -> error("No clear") })) { _, _ -> error("No request") }
        assertEquals(AppResult.Failure(AppError.Unexpected), stale(notification))
    }

    @Test fun `failed or cancelled read never clears and replacement during request cannot be cleared`() = runTest {
        val session = session({ identity }, { _, _ -> error("No clear") })
        assertEquals(AppResult.Failure(AppError.Network), ReadLatestNotification(session) { _, _ -> throw java.io.IOException() }(notification))
        val cancelled = CancellationException("cancel")
        assertSame(cancelled, runCatching { ReadLatestNotification(session) { _, _ -> throw cancelled }(notification) }.exceptionOrNull())
        val replaced = ReadLatestNotification(session({ identity }, { _, _ -> false })) { _, _ -> NotificationReadResult.Confirmed }
        assertEquals(AppResult.Failure(AppError.Unexpected), replaced(notification))
    }

    @Test fun `business or redirect rejection is returned without clearing the notification`() = runTest {
        val rejection = NotificationReadResult.Rejected(br.gov.caixa.loterias.core.domain.service.ServiceFailure.Unchanged)
        val useCase = ReadLatestNotification(session({ identity }, { _, _ -> error("Must retain notification") })) { _, _ -> rejection }
        assertEquals(AppResult.Success(rejection), useCase(notification))
    }

    private fun session(capture: () -> AuthenticatedSessionIdentity?, clear: (AuthenticatedSessionIdentity, AccountNotification) -> Boolean) =
        object : LatestNotificationSession {
            override fun capture(notification: AccountNotification) = capture()
            override fun clear(identity: AuthenticatedSessionIdentity, notification: AccountNotification) = clear(identity, notification)
        }
}
