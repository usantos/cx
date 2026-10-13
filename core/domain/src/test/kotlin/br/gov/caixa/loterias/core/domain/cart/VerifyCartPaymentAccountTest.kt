package br.gov.caixa.loterias.core.domain.cart

import br.gov.caixa.loterias.core.common.AppError
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.domain.account.AccountAccess
import br.gov.caixa.loterias.core.domain.account.PostLoginDestination
import br.gov.caixa.loterias.core.domain.account.VerifyAccountAccessUseCase
import java.math.BigDecimal
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class VerifyCartPaymentAccountTest {
    @Test fun `guest requires login without calling protected account service`() = runTest {
        val useCase = VerifyCartPaymentAccount({ false }, VerifyAccountAccessUseCase { error("Guest must not query") })
        assertEquals(AppResult.Success(CartPaymentAccountDecision.LoginRequired), useCase())
    }

    @Test fun `session is read on every attempt rather than captured at construction`() = runTest {
        var authenticated = true
        var calls = 0
        val useCase = VerifyCartPaymentAccount({ authenticated }, VerifyAccountAccessUseCase {
            calls++; AppResult.Success(AccountAccess(PostLoginDestination.HOME, false, BigDecimal.TEN))
        })
        assertEquals(AppResult.Success(CartPaymentAccountDecision.Allowed), useCase())
        authenticated = false
        assertEquals(AppResult.Success(CartPaymentAccountDecision.LoginRequired), useCase())
        assertEquals(1, calls)
    }

    @Test fun `registration has legacy priority over suspension while other destinations stay blocked`() = runTest {
        for (destination in PostLoginDestination.entries) {
            for (suspended in listOf(false, true)) {
                val useCase = VerifyCartPaymentAccount({ true }, VerifyAccountAccessUseCase {
                    AppResult.Success(AccountAccess(destination, suspended, BigDecimal.TEN))
                })
                val expected = when {
                    destination == PostLoginDestination.REGISTRATION -> CartPaymentAccountDecision.CompleteAccount(destination)
                    suspended -> CartPaymentAccountDecision.Suspended
                    destination != PostLoginDestination.HOME -> CartPaymentAccountDecision.CompleteAccount(destination)
                    else -> CartPaymentAccountDecision.Allowed
                }
                assertEquals(AppResult.Success(expected), useCase())
            }
        }
    }

    @Test fun `account service failures do not become permission to pay`() = runTest {
        for (error in listOf(AppError.Network, AppError.Unexpected, AppError.Validation("Sessão expirada"))) {
            val useCase = VerifyCartPaymentAccount({ true }, VerifyAccountAccessUseCase { AppResult.Failure(error) })
            assertEquals(AppResult.Failure(error), useCase())
        }
    }
}
