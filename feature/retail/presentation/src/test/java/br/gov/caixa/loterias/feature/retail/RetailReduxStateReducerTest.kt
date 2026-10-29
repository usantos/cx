package br.gov.caixa.loterias.feature.retail

import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateStatus
import br.gov.caixa.loterias.feature.retail.presentation.state.LocationGateUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionStateAction
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRedemptionUiState
import br.gov.caixa.loterias.feature.retail.presentation.state.PrizeRegistrationNotice
import br.gov.caixa.loterias.feature.retail.presentation.state.reduceLocationGateState
import br.gov.caixa.loterias.feature.retail.presentation.state.reducePrizeRedemptionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class RetailReduxStateReducerTest {
    @Test fun `retry resets gate and advances the check attempt`() {
        val state =
            reduceLocationGateState(LocationGateUiState(LocationGateStatus.ERROR, PREVIOUS_CHECK_ATTEMPT), LocationGateStateAction.Retry)
        assertEquals(LocationGateStatus.CHECKING, state.status)
        assertEquals(NEXT_CHECK_ATTEMPT, state.checkAttempt)
    }

    @Test fun `registration consume preserves identity and blocks until completion`() {
        val notice = PrizeRegistrationNotice("registration", "person@example.test")
        val initial = PrizeRedemptionUiState(registrationNotice = notice, loading = false)
        assertSame(
            initial,
            reducePrizeRedemptionState(
                initial,
                PrizeRedemptionStateAction.RegistrationNoticeConsumed(notice.copy())
            )
        )
        val consumed = reducePrizeRedemptionState(initial, PrizeRedemptionStateAction.RegistrationNoticeConsumed(notice))
        assertEquals(null, consumed.registrationNotice)
        assertTrue(consumed.registrationPending)
        assertFalse(reducePrizeRedemptionState(consumed, PrizeRedemptionStateAction.RegistrationFinished).registrationPending)
    }

    @Test fun `timer tick preserves the current pix ownership choice`() {
        val chosen = reducePrizeRedemptionState(PrizeRedemptionUiState(), PrizeRedemptionStateAction.PixOwnershipToggled)
        val ticked = reducePrizeRedemptionState(chosen, PrizeRedemptionStateAction.TimerTicked(REMAINING_SECONDS))
        assertTrue(ticked.pixOwnershipAccepted)
        assertEquals(REMAINING_SECONDS, ticked.remainingSeconds)
    }
}

private const val PREVIOUS_CHECK_ATTEMPT = 3L
private const val NEXT_CHECK_ATTEMPT = 4L
private const val REMAINING_SECONDS = 12
