package br.gov.caixa.loterias.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeDrawResult
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.results.domain.model.DrawRegionalWinner
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [HOME_RESULT_TEST_SDK], qualifiers = "w480dp-h900dp")
class HomeDrawResultBehaviorTest {
    @get:Rule val compose = createComposeRule()

    @Test fun maisMilionariaKeepsCloversSeparateFromDrawnNumbers() {
        show(result("maismilionaria").copy(clovers = listOf(2, 1)))

        compose.onNodeWithText("Trevos").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("01").assertIsDisplayed()
        compose.onNodeWithText("02").assertIsDisplayed()
        compose.onNodeWithText(FIRST_DRAWN_NUMBER.toString()).performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(LAST_DRAWN_NUMBER.toString()).assertIsDisplayed()
    }

    @Test fun regionalWinnersLimitVisibleMunicipalitiesAndOpenTheFullContestList() {
        val opened = mutableListOf<String>()
        val handler = object : UriHandler {
            override fun openUri(uri: String) {
                opened.add(uri)
            }
        }
        show(
            result("MEGA SENA").copy(
                firstDrawRegionalWinners = (1..REGIONAL_WINNER_COUNT).map {
                    DrawRegionalWinner("Cidade $it - SP", "$it aposta regional")
                }
            ),
            handler
        )

        compose.onNodeWithText("Cidade 1 / SP").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Cidade 30 / SP").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("30 aposta regional").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Cidade 31 / SP").assertDoesNotExist()
        compose.onNodeWithText(FULL_WINNER_LIST_LINK).performScrollTo().performClick()
        compose.runOnIdle {
            assertEquals(
                listOf(
                    "https://loterias.caixa.gov.br/Paginas/Locais-Sorte.aspx?modalidade=MEGA%20SENA" +
                        "&concurso=1&titulo=Mega%20Sena%20Especial"
                ),
                opened
            )
        }
    }

    private fun show(
        result: DrawResult,
        handler: UriHandler = object : UriHandler {
            override fun openUri(uri: String) = error("Resultado sem lista regional não deve abrir links: $uri")
        },
    ) {
        val lottery = HomeLottery(
            2, "NORMAL", result.modality, "Mega Sena Especial", 1,
            BigDecimal.TEN, BigDecimal.ONE, "27/10/2026", "", true, HomeLotteryKind.LOTTERY,
            Color.Black, Color.Black, Color.White, 0, 0, 0
        )
        compose.setContent {
            CompositionLocalProvider(LocalUriHandler provides handler) {
                CaixaDSTheme { HomeDrawResult(result, lottery, Modifier.fillMaxSize()) }
            }
        }
    }

    private fun result(modality: String) = DrawResult(
        modality, 1, "27/10/2026", "", (FIRST_DRAWN_NUMBER..LAST_DRAWN_NUMBER).toList(), emptyList(), emptyList(),
        false, "", "", "", emptyList()
    )
}

private const val HOME_RESULT_TEST_SDK = 35
private const val FIRST_DRAWN_NUMBER = 11
private const val LAST_DRAWN_NUMBER = 16
private const val REGIONAL_WINNER_COUNT = 31
private const val FULL_WINNER_LIST_LINK = "Consulte aqui o restante da lista de municípios com ganhadores."
