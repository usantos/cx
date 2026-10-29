package br.gov.caixa.loterias.feature.home

import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.feature.home.presentation.state.HomeNotificationAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeStateAction
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.home.presentation.state.reduceHomeState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeReduxStateReducerTest {
    @Test fun `dismiss requires the retained notice identity`() {
        val retained = ServiceNoticeUiState("same message")
        val initial = HomeUiState(notificationNotice = retained)
        assertSame(initial, reduceHomeState(initial, HomeNotificationAction.NoticeDismissed(ServiceNoticeUiState("same message"))))
        assertEquals(null, reduceHomeState(initial, HomeNotificationAction.NoticeDismissed(retained)).notificationNotice)
    }

    @Test fun `stale cart failure leaves newer session loading untouched`() {
        val initial = HomeUiState(loadingCart = true, cartItemCount = CART_ITEM_COUNT, headerActionsRevealed = true)
        assertSame(initial, reduceHomeState(initial, HomeStateAction.CartFailed(requestCurrent = false)))
        val completed = reduceHomeState(initial, HomeStateAction.CartFailed(requestCurrent = true))
        assertFalse(completed.loadingCart)
        assertEquals(CART_ITEM_COUNT, completed.cartItemCount)
        assertTrue(completed.headerActionsRevealed)
    }
}

private const val CART_ITEM_COUNT = 4
