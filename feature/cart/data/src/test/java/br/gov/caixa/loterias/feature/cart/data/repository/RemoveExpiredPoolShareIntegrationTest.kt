package br.gov.caixa.loterias.feature.cart.data.repository

import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.common.result.AppResult
import br.gov.caixa.loterias.feature.account.domain.model.AuthenticatedSessionIdentity
import br.gov.caixa.loterias.feature.cart.domain.model.CartItem
import br.gov.caixa.loterias.feature.cart.domain.model.CartItemKind
import br.gov.caixa.loterias.feature.cart.domain.model.CartPoolShare
import br.gov.caixa.loterias.core.network.api.AccountVerificationSession
import br.gov.caixa.loterias.feature.cart.data.dto.CartDto
import java.math.BigDecimal
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import br.gov.caixa.loterias.feature.cart.data.datasource.remote.BoundCartRemovalRemoteDataSource
import br.gov.caixa.loterias.feature.cart.data.repository.ExpiredPoolShareRepositoryImpl
import br.gov.caixa.loterias.feature.cart.domain.usecase.RemoveExpiredPoolShare
import java.io.IOException

class RemoveExpiredPoolShareIntegrationTest {
    private val identity = AuthenticatedSessionIdentity("a".repeat(64))
    private val share = CartItem(
        19,
        CartItemKind.POOL_SHARE,
        "Cota",
        "",
        BigDecimal.ONE,
        poolShare = CartPoolShare("pool", 2, 10, "2026-10-14T12:00:00Z")
    )

    @Test fun `removal binds the displaying login and reservation id`() = runBlocking {
        val session = AccountVerificationSession({ "fixture" }, { true })
        var calls = 0
        val useCase = createUseCase(
            BoundCartRemovalRemoteDataSource { id, tag ->
                calls++
                assertEquals(19L, id)
                assertSame(session, tag)
                CartDto(id = 7, total = BigDecimal.ZERO)
            }
        ) { captured ->
            assertSame(identity, captured)
            session
        }
        val result = useCase(share, identity) as AppResult.Success
        assertEquals(1, calls)
        assertEquals(7L, result.value.id)
        assertTrue(result.value.items.isEmpty())
    }

    @Test fun `automatic expiry cannot delete an individual bet`() = runBlocking {
        var calls = 0
        val useCase = createUseCase(
            BoundCartRemovalRemoteDataSource { _, _ ->
                calls++
                error("Unexpected deletion")
            }
        ) { error("Unexpected binding") }
        val result = useCase(share.copy(kind = CartItemKind.INDIVIDUAL_BET), identity)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
        assertEquals(0, calls)
    }

    @Test fun `cancellation remains cancellation instead of a retryable failure`() = runBlocking {
        val cancelled = CancellationException("Fixture cancellation")
        val useCase = createUseCase(BoundCartRemovalRemoteDataSource { _, _ -> throw cancelled }) {
            AccountVerificationSession({ "fixture" }, { true })
        }
        try {
            useCase(share, identity)
            fail("Cancellation must propagate")
        } catch (actual: CancellationException) {
            assertSame(cancelled, actual)
        }
    }

    @Test fun `binding failure is reported without sending a request`() = runBlocking {
        var calls = 0
        val useCase = createUseCase(
            BoundCartRemovalRemoteDataSource { _, _ ->
                calls++
                error("Request must not be sent without a session")
            }
        ) { throw IOException("Binding unavailable") }
        val result = useCase(share, identity)
        assertEquals(AppResult.Failure(AppError.Network), result)
        assertEquals(0, calls)
    }

    private fun createUseCase(
        remote: BoundCartRemovalRemoteDataSource,
        bind: (AuthenticatedSessionIdentity) -> AccountVerificationSession,
    ) = RemoveExpiredPoolShare(ExpiredPoolShareRepositoryImpl(remote, bind))
}
