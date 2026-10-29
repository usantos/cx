package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.presentation.rule.ServiceNoticeUiState
import br.gov.caixa.loterias.core.presentation.state.ServiceFailurePresentation
import br.gov.caixa.loterias.core.presentation.state.ServiceRedirectEvent
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.GeneratedBettingSelection
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListAction
import br.gov.caixa.loterias.feature.betting.presentation.state.RapidListUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingGameState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceRapidListState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Test

class BettingReducersTest {
    private val game = LotteryGameParameters(
        LotteryModality(2, "Mega", "MEGA_SENA"), 1, "", 2, 1, 2, 1, listOf(0), emptyList()
    )

    @Test fun `selection edits clear favorite acknowledgements in the reducer`() {
        val initial = BettingGameUiState(
            loading = false,
            game = game,
            numberQuantity = 1,
            favoriteSaved = true,
            favoriteExpanded = true,
            favoriteOverwriteConfirmation = true
        )
        val result = reduceBettingGameState(initial, BettingGameAction.NumberToggled(1, warn = true))
        assertEquals(setOf(1), result.selectedNumbers)
        assertFalse(result.favoriteSaved)
        assertFalse(result.favoriteExpanded)
        assertFalse(result.favoriteOverwriteConfirmation)
        assertEquals(emptySet<Int>(), initial.selectedNumbers)
    }

    @Test fun `durable publication replays the generated selection without producing new random values`() {
        val initial = BettingGameUiState(loading = false, game = game, numberQuantity = 1)
        val generated = GeneratedBettingSelection(
            setOf(1), 1, emptyList(), emptyMap(), null, "", null, emptySet(), false, null, null
        )
        val actions = listOf(BettingGameAction.ShakeSelectionGenerated(generated), BettingGameAction.NumberToggled(2, warn = true))
        val working = actions.fold(initial, ::reduceBettingGameState)
        val committed = reduceBettingGameState(initial, BettingGameAction.DraftCommitted(actions))
        assertEquals(working, committed)
        assertEquals(setOf(1, 2), committed.selectedNumbers)
        assertEquals(committed, reduceBettingGameState(initial, BettingGameAction.DraftCommitted(actions)))
    }

    @Test fun `service acknowledgement rejects stale identity and retains the prepared redirect identity`() {
        val current = ServiceNoticeUiState("Login")
        val stale = ServiceNoticeUiState("Login")
        val initial = RapidListUiState(serviceFailure = ServiceFailurePresentation(notice = current))
        val redirect = ServiceRedirectEvent(ServiceRedirect.LOGIN)
        val prepared = ServiceFailurePresentation(redirect = redirect)
        val rejected = reduceRapidListState(initial, RapidListAction.ServiceNoticeAccepted(stale, prepared))
        assertSame(current, rejected.serviceFailure.notice)
        val accepted = reduceRapidListState(initial, RapidListAction.ServiceNoticeAccepted(current, prepared))
        assertSame(redirect, accepted.serviceFailure.redirect)
    }
}
