package br.gov.caixa.loterias.feature.betting

import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameAction
import br.gov.caixa.loterias.feature.betting.presentation.state.BettingGameUiState
import br.gov.caixa.loterias.feature.betting.presentation.state.reduceBettingGameState
import java.util.Locale
import java.util.ResourceBundle
import org.junit.Assert.assertEquals
import org.junit.Test

class ReducerLocalizationReplayTest {
    @Test fun `an existing action replays its captured text after locale and bundle cache change`() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            ResourceBundle.clearCache()
            val state = BettingGameUiState()
            val action = BettingGameAction.ClosedContestRejected(CONTEST_NUMBER)
            val first = reduceBettingGameState(state, action)
            assertEquals("O registro de apostas individuais para o concurso 42 foi encerrado.", first.errorMessage)

            Locale.setDefault(Locale.FRANCE)
            ResourceBundle.clearCache()
            assertEquals(first, reduceBettingGameState(state, action))
        } finally {
            Locale.setDefault(original)
            ResourceBundle.clearCache()
        }
    }
}

private const val CONTEST_NUMBER = 42
