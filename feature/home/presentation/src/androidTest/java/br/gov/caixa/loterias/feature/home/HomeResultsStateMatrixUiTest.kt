package br.gov.caixa.loterias.feature.home

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.resolveCaixaDSLotteryStyle
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeResultContent
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.results.domain.model.*
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import java.io.File
import java.math.BigDecimal
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Captura massas sintéticas de TEST; dados de resultado de produção permanecem intactos. */
class HomeResultsStateMatrixUiTest {
    @get:Rule val compose = createComposeRule()
    private val state = mutableStateOf(ResultsUiState())
    private val lottery = mutableStateOf(lottery("megasena"))
    private var openedUri: String? = null

    private fun render(height: Int = 300) {
        compose.setContent {
            CaixaDSTheme(darkTheme = false) {
                CompositionLocalProvider(LocalUriHandler provides object : UriHandler {
                    override fun openUri(uri: String) { openedUri = uri }
                }) {
                    Box(Modifier.size(280.dp, height.dp).background(lottery.value.darkColor)) {
                        HomeResultContent(state.value, lottery.value, Modifier.fillMaxSize())
                    }
                }
            }
        }
    }

    @Test fun capturesEveryModalityResultLayoutAndScrollablePrizeTail() {
        render()
        val fixtures = listOf(
            result("megasena", listOf(42, 1, 5, 18, 22, 57)),
            result("quina", listOf(3, 8, 21, 45, 76)),
            result("lotofacil", (1..15).toList()),
            result("lotomania", (81..100).toList()),
            result("duplasena", (1..6).toList()).copy(
                secondDrawNumbers = (11..16).toList(), payouts = (1..8).map { DrawPayout("000$it", it.toLong() % 3, "1234.56") }),
            result("maismilionaria", listOf(1, 4, 9, 19, 24, 40)).copy(clovers = listOf(5, 2)),
            result("supersete", List(7) { it }).copy(firstDrawNumberColumns = List(7) { listOf(it) }),
            result("timemania", (1..7).toList()).copy(favoriteTeamReward = DrawSelectionReward("Flamengo/RJ", 1, "8")),
            result("diadesorte", (1..7).toList()).copy(luckyMonthReward = DrawSelectionReward("Outubro", 2, "2.5")),
            result("loteca", emptyList()).copy(lotecaMatches = sports()),
            result("lotogol", emptyList()).copy(lotogolMatches = sports()),
        )
        fixtures.forEach { result ->
            compose.runOnIdle { lottery.value = lottery(result.modality); state.value = ResultsUiState(result = result) }
            compose.onNodeWithText("Nº 3073 | 21 Outubro 2026").assertIsDisplayed()
            capture("${result.modality}-top")
            compose.onNodeWithText("Arrecadação total").performScrollTo().assertIsDisplayed()
            val scrollNode = compose.onNodeWithTag("home-result-scroll")
            scrollNode.performSemanticsAction(androidx.compose.ui.semantics.SemanticsActions.ScrollBy) { it(0f, 100_000f) }
            val viewport = scrollNode.getUnclippedBoundsInRoot()
            val tail = compose.onNodeWithTag("home-result-revenue").getUnclippedBoundsInRoot()
            val range = scrollNode.fetchSemanticsNode().config[androidx.compose.ui.semantics.SemanticsProperties.VerticalScrollAxisRange]
            if (range.maxValue() > 0f) {
                org.junit.Assert.assertEquals("${result.modality} final result gap", 16f, (viewport.bottom - tail.bottom).value, 1f)
            }
            capture("${result.modality}-revenue")
        }
    }

    @Test fun capturesLoadingErrorEmptyAndStaleModalityWithoutLeakingOldDraw() {
        render()
        listOf(
            "loading" to ResultsUiState(isLoading = true),
            "error" to ResultsUiState(errorMessage = "Não foi possível consultar o resultado."),
            "empty" to ResultsUiState(),
            "stale-modality" to ResultsUiState(result = result("quina", listOf(1, 2, 3))),
        ).forEach { (name, next) ->
            compose.runOnIdle { state.value = next }
            when (name) {
                "loading" -> compose.onNodeWithTag("home-result-loading").assertIsDisplayed()
                "error" -> compose.onNodeWithText("Não foi possível consultar o resultado.").assertIsDisplayed()
                else -> compose.onNodeWithText("Consultando último resultado…").assertIsDisplayed()
            }
            compose.onNodeWithTag("home-result-scroll").assertDoesNotExist()
            capture(name)
        }
    }

    @Test fun capturesWinnerVariantsRegionLimitAndPortalNavigation() {
        render()
        val next = result("megasena", (1..6).toList()).copy(
            payouts = listOf(DrawPayout("0001", 0, "9999"), DrawPayout("0002", 1, "2000"), DrawPayout("0003", 5, "100")),
            firstDrawRegionalWinners = (1..31).map { DrawRegionalWinner("Cidade $it - SP", "1 aposta ganhadora") },
        )
        compose.runOnIdle { state.value = ResultsUiState(result = next) }
        compose.onNodeWithText("Sem ganhadores").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Prêmio: R$ 9.999,00").assertDoesNotExist()
        capture("zero-winners")
        compose.onNodeWithText("Ganhadores por região").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Cidade 1 / SP").performScrollTo().assertIsDisplayed()
        capture("regions-first")
        val link = compose.onNodeWithText("Consulte aqui o restante da lista de municípios com ganhadores.")
        link.performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Cidade 31 / SP").assertDoesNotExist()
        capture("regions-cap30")
        link.performClick()
        compose.runOnIdle {
            assertTrue(openedUri.orEmpty().contains("concurso=3073"))
            assertTrue(openedUri.orEmpty().contains("titulo=Mega-Sena"))
        }
    }

    @Test fun sportsPagerMovesBetweenMatchesAndPreservesCountryLabels() {
        render(height = 500)
        compose.runOnIdle {
            lottery.value = lottery("loteca")
            state.value = ResultsUiState(result = result("loteca", emptyList()).copy(lotecaMatches = sports()))
        }
        compose.onNodeWithText("1 de 2").assertIsDisplayed()
        compose.onNodeWithText("/ARG", substring = true).assertExists()
        capture("loteca-page1")
        compose.onNodeWithTag("home-result-sports-pager").performTouchInput { swipeLeft() }
        compose.onNodeWithText("2 de 2").assertIsDisplayed()
        capture("loteca-page2")
        compose.runOnIdle { state.value = state.value.copy(result = state.value.result!!.copy(contest = 3074)) }
        compose.onNodeWithText("1 de 2").assertIsDisplayed()
        capture("loteca-new-contest-resets-page")
    }

    @Test fun missingMonthUsesLegacyQuestionMarksAndCombinedDuplaBandsRemainVisible() {
        render()
        compose.runOnIdle {
            lottery.value = lottery("diadesorte")
            state.value = ResultsUiState(result = result("diadesorte", (1..7).toList()))
        }
        compose.onNodeWithText("???").assertIsDisplayed()
        capture("dia-missing-month")
        compose.runOnIdle {
            lottery.value = lottery("duplasena")
            state.value = ResultsUiState(result = result("duplasena", (1..6).toList()).copy(
                secondDrawNumbers = (11..16).toList(),
                payouts = listOf(DrawPayout("0001", 1, "100")), secondDrawPayouts = listOf(DrawPayout("0005", 2, "50")),
                secondDrawRegionalWinners = listOf(DrawRegionalWinner("Outra cidade - RJ", "2 apostas ganhadoras")),
            ))
        }
        compose.onNodeWithText("Premiação – 2º sorteio").performScrollTo().assertIsDisplayed()
        capture("dupla-explicit-second-payouts")
        compose.onNodeWithText("Ganhadores por região – 2º sorteio").performScrollTo().assertIsDisplayed()
        capture("dupla-second-regions")
    }

    @Test fun capturesEmptyPayoutAndCampaignMegaResultWithoutInventingRewardRows() {
        render()
        compose.runOnIdle {
            state.value = ResultsUiState(result = result("megasena", (1..6).toList()).copy(payouts = emptyList()))
        }
        compose.onNodeWithText("Premiação").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Sem ganhadores").assertDoesNotExist()
        capture("empty-payouts")
        compose.runOnIdle {
            val style = resolveCaixaDSLotteryStyle(2, "megasena", br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign.OCTOBER_ROSE)
            lottery.value = lottery("megasena").copy(color = style.light, darkColor = style.dark, headerContentColor = style.content, octoberRoseTheme = true)
            state.value = ResultsUiState(result = result("megasena", (1..6).toList()).copy(specialContest = true, specialName = "Mega da Virada"))
        }
        compose.onNodeWithText("Nº 3073 | 21 Outubro 2026").performScrollTo().assertIsDisplayed()
        capture("mega-outubro-rosa")
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val file = File(context.getExternalFilesDir(null), "home-results-matrix/$name.png")
        check(file.parentFile!!.isDirectory || file.parentFile!!.mkdirs())
        file.outputStream().use { check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)) }
        // O AGP pode desinstalar o alvo após a instrumentação; preserve os artefatos de captura nomeados.
        check(name.matches(Regex("[a-z0-9-]+")))
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "cp ${file.absolutePath} /sdcard/Download/simlo-home-results-$name.png",
        ).use { android.os.ParcelFileDescriptor.AutoCloseInputStream(it).readBytes() }
    }

    private fun lottery(code: String): HomeLottery {
        val style = resolveCaixaDSLotteryStyle(0, code)
        return HomeLottery(2, "NORMAL", code, if (code == "megasena") "Mega-Sena" else code, 3074,
            BigDecimal.ZERO, BigDecimal.ONE, "22/10/2026", "", true, HomeLotteryKind.LOTTERY,
            style.light, style.dark, style.content, R.drawable.legacy_home_trevo_megasena_fundo_escuro,
            R.drawable.icon_data_megasena, R.drawable.icon_horario_megasena)
    }
    private fun result(code: String, numbers: List<Int>) = DrawResult(
        code, 3073, "21 outubro 2026", "", numbers, emptyList(), emptyList(), false, "", "1000000", "",
        listOf(DrawPayout("0001", 0, "1000"), DrawPayout("0002", 1, "200")),
    )
    private fun sports() = listOf(
        DrawSportsMatch(1, DrawSportsTeam("Argentina", "", "2", true, countryCode = "ARG", nationalTeam = true),
            DrawSportsTeam("Brasil", "", "1", false, countryCode = "BRA", nationalTeam = true), draw = false),
        DrawSportsMatch(2, DrawSportsTeam("Casa", "SP", "0", false, countryCode = "BRA"),
            DrawSportsTeam("Fora", "RJ", "0", false, countryCode = "BRA"), draw = true),
    )
}
