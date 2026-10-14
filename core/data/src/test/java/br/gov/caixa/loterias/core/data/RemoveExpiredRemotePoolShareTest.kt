package br.gov.caixa.loterias.core.data

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.core.domain.cart.CartItem
import br.gov.caixa.loterias.core.domain.cart.CartItemKind
import br.gov.caixa.loterias.core.domain.cart.CartPoolShare
import br.gov.caixa.loterias.core.network.AccountVerificationSession
import br.gov.caixa.loterias.core.network.CartDto
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RemoveExpiredRemotePoolShareTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val share = CartItem(19, CartItemKind.POOL_SHARE, "Cota", "", BigDecimal.ONE,
        poolShare = CartPoolShare("pool", 2, 10, "2026-10-14T12:00:00Z"))

    @Test fun `removal binds the displaying login and reservation id`() = runBlocking {
        val session = AccountVerificationSession({ "fixture" }, { true })
        var calls = 0
        val useCase = RemoveExpiredRemotePoolShare(BoundCartRemovalRemoteDataSource { id, tag ->
            calls++
            assertEquals(19L, id)
            assertSame(session, tag)
            CartDto(id = 7, total = BigDecimal.ZERO)
        }) { captured -> assertSame(identity, captured); session }
        val result = useCase(share, identity) as AppResult.Success
        assertEquals(1, calls)
        assertEquals(7L, result.value.id)
        assertTrue(result.value.items.isEmpty())
    }

    @Test fun `automatic expiry cannot delete an individual bet`() = runBlocking {
        var calls = 0
        val useCase = RemoveExpiredRemotePoolShare(BoundCartRemovalRemoteDataSource { _, _ ->
            calls++; error("Unexpected deletion")
        }) { error("Unexpected binding") }
        val result = useCase(share.copy(kind = CartItemKind.INDIVIDUAL_BET), identity)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        assertEquals(0, calls)
    }

    @Test fun `cancellation remains cancellation instead of a retryable failure`() = runBlocking {
        val cancelled = CancellationException("Fixture cancellation")
        val useCase = RemoveExpiredRemotePoolShare(BoundCartRemovalRemoteDataSource { _, _ -> throw cancelled }) {
            AccountVerificationSession({ "fixture" }, { true })
        }
        try {
            useCase(share, identity)
            fail("Cancellation must propagate")
        } catch (actual: CancellationException) {
            assertSame(cancelled, actual)
        }
    }
}
