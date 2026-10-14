package br.gov.caixa.loterias.apostas

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.results.*
import br.gov.caixa.loterias.feature.results.ResultsScreen
import br.gov.caixa.loterias.feature.results.ResultsUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PublicResultsParityUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()

    @Test fun publicResultDoesNotInsertHomeContestSummaryIntoPrizeDetails() {
        val result = DrawResult("MEGA_SENA", 3069, "11 out 2026", "SÃO PAULO - SP",
            listOf(5, 19, 22, 44, 47, 57), emptyList(), emptyList(), true,
            "150000000", "98228184", "Aviso do concurso",
            listOf(DrawPayout("0001", 0, "0"), DrawPayout("0002", 150, "26095.23")),
            nextFinalZeroContest = UpcomingDrawContest(3070, "14 out 2026", "137920959.97"))
        compose.setContent {
            CaixaDSTheme {
                ResultsScreen(ResultsUiState(modality = "megasena", result = result, latestContest = 3069),
                    onBack = {}, onSelectModality = {}, onContestChange = {}, onSearch = {}, onRetry = {},
                    onPreviousContest = {}, onNextContest = {}, showDetail = true)
            }
        }
        compose.onNodeWithText("Concurso nº 3069").assertExists()
        compose.onNodeWithText("Arrecadação total").assertExists()
        compose.onNodeWithText("150 apostas ganhadoras").assertExists()
        compose.onNodeWithText("Acumulou").assertDoesNotExist()
        compose.onNodeWithText("Local do sorteio: SÃO PAULO - SP").assertDoesNotExist()
        compose.onNodeWithText("Aviso do concurso").assertDoesNotExist()
        compose.onNodeWithText("Próximo concurso final zero: 3070").assertDoesNotExist()
    }
}
