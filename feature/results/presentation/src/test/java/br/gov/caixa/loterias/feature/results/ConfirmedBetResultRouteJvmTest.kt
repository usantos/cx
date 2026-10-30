// Literais numéricos descrevem geometria fixa dos testes, IDs dos dados de teste, tempos e valores esperados.
@file:Suppress("MagicNumber")

package br.gov.caixa.loterias.feature.results

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import br.gov.caixa.loterias.core.common.coroutines.FixedCoroutineDispatchers
import br.gov.caixa.loterias.core.common.result.AppError
import br.gov.caixa.loterias.core.designsystem.testing.captureToImage
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSCampaignTheme
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.service.model.ServiceFailure
import br.gov.caixa.loterias.core.domain.service.model.ServiceNotice
import br.gov.caixa.loterias.core.domain.service.model.ServiceNoticeReason
import br.gov.caixa.loterias.core.domain.service.model.ServiceRedirect
import br.gov.caixa.loterias.core.domain.service.model.ServiceResult
import br.gov.caixa.loterias.feature.betting.domain.model.LotecaMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LotogolMatchSelection
import br.gov.caixa.loterias.feature.betting.domain.model.LuckyMonth
import br.gov.caixa.loterias.feature.betting.domain.model.SportsMatch
import br.gov.caixa.loterias.feature.betting.domain.model.SportsTeam
import br.gov.caixa.loterias.feature.results.domain.model.DrawPayout
import br.gov.caixa.loterias.feature.results.domain.model.DrawRegionalWinner
import br.gov.caixa.loterias.feature.results.domain.model.DrawResult
import br.gov.caixa.loterias.feature.results.domain.model.DrawSelectionReward
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsMatch
import br.gov.caixa.loterias.feature.results.domain.model.DrawSportsTeam
import br.gov.caixa.loterias.feature.results.domain.model.UpcomingDrawContest
import br.gov.caixa.loterias.feature.results.domain.usecase.GetConfirmedBetResultsUseCase
import br.gov.caixa.loterias.feature.results.presentation.route.ConfirmedBetResultRoute
import br.gov.caixa.loterias.feature.results.presentation.viewmodel.ConfirmedBetResultsViewModel
import br.gov.caixa.loterias.feature.tickets.domain.model.ConfirmedBet
import java.math.BigDecimal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = android.app.Application::class, qualifiers = "w411dp-h891dp-xhdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ConfirmedBetResultRouteJvmTest {
    @get:Rule
    val compose = createComposeRule()
    private val bet =
        ConfirmedBet(1, "Mega-Sena", 2000, "22/10/2026", "Premiada", listOf(1, 9), prize = BigDecimal("50"), clovers = listOf(1, 2))
    private val result = DrawResult(
        "Mega-Sena", 2000, "22/10/2026", "São Paulo", listOf(1, 2, 3, 4, 5, 6), listOf(1, 8), listOf(1, 3),
        true, "1000000", "200000", "Confira seu bilhete", listOf(DrawPayout("Sena", 1, "50")),
        secondDrawPayouts = listOf(DrawPayout("Segunda faixa", 0, "")),
        favoriteTeamReward = DrawSelectionReward("Brasil", 1, "10"), luckyMonthReward = DrawSelectionReward("Janeiro", 0, ""),
        firstDrawRegionalWinners = listOf(DrawRegionalWinner("São Paulo/SP", "1 aposta")),
        secondDrawRegionalWinners = listOf(DrawRegionalWinner("Recife/PE", "2 apostas")),
        nextContest = UpcomingDrawContest(2001, "25/10/2026", "1000000"),
        nextFinalZeroContest = UpcomingDrawContest(2010, "30/10/2026", "2000000"),
        nextFinalFiveContest = UpcomingDrawContest(2005, "28/10/2026", "3000000"),
        nextSpecialContest = UpcomingDrawContest(2100, "31/12/2026", "4000000"), specialContest = true
    )

    @Test
    fun confirmedResultShowsHitSummaryPayoutsUpcomingContestsAndRedeemAction() {
        var backs = 0
        var complete = 0
        var redeem = 0
        compose.setContent {
            val model = remember { model(bet) { ServiceResult.Success(listOf(result)) } }
            CaixaDSTheme {
                ConfirmedBetResultRoute(bet, model, { backs++ }, { complete++ }, onRedeemPrize = { redeem++ })
            }
        }
        compose.onNodeWithText("Mega da Virada • Concurso 2000").assertIsDisplayed()
        listOf(
            "Trevos sorteados",
            "Segundo sorteio",
            "Sua aposta",
            "1 acerto no primeiro sorteio • Premiada",
            "Premiação do 1º sorteio",
            "Premiação do 2º sorteio",
            "Ganhadores por região",
            "Ganhadores por região do 2º sorteio"
        )
            .forEach { compose.onNodeWithText(it).performScrollTo().assertIsDisplayed() }
        listOf(
            "Próximo concurso: dois mil e um. vinte e cinco de outubro de dois mil e vinte e seis. " +
                "Estimativa de prêmio: um milhão de reais.",
            "Próximo concurso final zero: dois mil e dez. trinta de outubro de dois mil e vinte e seis. " +
                "Estimativa de prêmio: dois milhões de reais.",
            "Próximo concurso final cinco: dois mil e cinco. vinte e oito de outubro de dois mil e vinte e seis. " +
                "Estimativa de prêmio: três milhões de reais.",
            "Próximo concurso especial: dois mil e cem. trinta e um de dezembro de dois mil e vinte e seis. " +
                "Estimativa de prêmio: quatro milhões de reais."
        ).forEach { compose.onNodeWithContentDescription(it).performScrollTo().assertIsDisplayed() }
        compose.onNodeWithText("Resgatar prêmio").performScrollTo().performClick()
        compose.onNodeWithText("Voltar às apostas").performClick()
        compose.onNodeWithContentDescription("Voltar").performClick()
        compose.runOnIdle {
            assertEquals(1, redeem)
            assertEquals(1, complete)
            assertEquals(1, backs)
        }
    }

    @Test
    fun loadingThenUnavailableAndNetworkErrorKeepTheirRetryAndRecovery() {
        val pending = CompletableDeferred<ServiceResult<List<DrawResult>>>()
        var calls = 0
        compose.setContent {
            val model =
                remember {
                    model(bet) {
                        when (++calls) {
                            1 -> pending.await()
                            2 -> ServiceResult.Failed(AppError.Network)
                            else -> ServiceResult.Success(listOf(result))
                        }
                    }
                }
            CaixaDSTheme { ConfirmedBetResultRoute(bet, model, {}, {}) }
        }
        compose.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).assertIsDisplayed()
        compose.runOnIdle { pending.complete(ServiceResult.Success(emptyList())) }
        compose.onNodeWithText("Resultado ainda não disponível").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Não foi possível acessar o serviço. Verifique sua conexão.").assertIsDisplayed()
        compose.onNodeWithText("Tentar novamente").performClick()
        compose.onNodeWithText("Sua aposta").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { assertEquals(3, calls) }
    }

    @Test
    fun serviceNoticeAndRedirectPreserveTypedServiceDecision() {
        val current =
            mutableStateOf<ServiceResult<List<DrawResult>>>(
                ServiceResult.Rejected(
                    ServiceFailure.NoticeRequired(ServiceNotice(ServiceNoticeReason.BUSINESS_RULE, "Aguarde a apuração"))
                )
            )
        lateinit var viewModel: ConfirmedBetResultsViewModel
        val redirects = mutableListOf<ServiceRedirect>()
        compose.setContent {
            viewModel = remember { model(bet) { current.value } }
            CaixaDSTheme { ConfirmedBetResultRoute(bet, viewModel, {}, {}, redirects::add) }
        }
        compose.onNodeWithText("Aguarde a apuração").assertIsDisplayed()
        compose.runOnIdle {
            current.value = ServiceResult.Rejected(ServiceFailure.RedirectRequired(ServiceRedirect.ACCEPT_TERMS))
            viewModel.retry()
        }
        compose.runOnIdle { assertEquals(listOf(ServiceRedirect.ACCEPT_TERMS), redirects) }
    }

    @Test
    fun campaignAndColumnBetsKeepColumnHitsTeamMonthAndRedeemedPrizeRules() {
        val selected = bet.copy(
            modality = "Super Sete",
            modalityId = 7,
            numbers = emptyList(),
            numberColumns = listOf(listOf(1), listOf(2)),
            favoriteTeam = SportsTeam(1, "Brasil"),
            luckyMonth = LuckyMonth(1, "Janeiro"),
            redeemedPrize = true
        )
        val drawn = result.copy(
            modality = "Super Sete",
            firstDrawNumbers = emptyList(),
            firstDrawNumberColumns = listOf(listOf(1), listOf(8)),
            secondDrawNumbers = emptyList(),
            clovers = emptyList(),
            specialContest = false,
            favoriteTeamReward = null,
            luckyMonthReward = null
        )
        compose.setContent {
            val model = remember { model(selected) { ServiceResult.Success(listOf(drawn)) } }
            CaixaDSTheme {
                CaixaDSCampaignTheme(CaixaDSCampaign.OCTOBER_ROSE, CaixaDSCampaign.OCTOBER_ROSE) {
                    ConfirmedBetResultRoute(selected, model, {}, {}, onRedeemPrize = { error("Redeemed prize cannot be redeemed again") })
                }
            }
        }
        compose.onNodeWithText("Time do coração: Brasil").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Mês de sorte: Janeiro").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("1 acerto no primeiro sorteio • Premiada").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Resgatar prêmio").assertDoesNotExist()
    }

    @Test
    fun sportsBetsRenderChosenOutcomesAndScoresInsteadOfNumericHitCount() {
        val match = SportsMatch(1, SportsTeam(1, "Casa"), SportsTeam(2, "Visitante"))
        val selected =
            mutableStateOf(bet.copy(modality = "Loteca", lotecaSelections = listOf(LotecaMatchSelection(match, true, true, false))))
        val drawn = result.copy(
            modality = "Loteca",
            lotecaMatches = listOf(DrawSportsMatch(1, DrawSportsTeam("Casa", won = true), DrawSportsTeam("Visitante"))),
            secondDrawNumbers = emptyList(),
            clovers = emptyList()
        )
        compose.setContent {
            val current = selected.value
            val model = remember(current) { model(current) { ServiceResult.Success(listOf(drawn)) } }
            CaixaDSTheme { ConfirmedBetResultRoute(current, model, {}, {}) }
        }
        compose.onNodeWithText("1. Casa × Visitante: Casa / Empate").performScrollTo().assertIsDisplayed()
        compose.runOnIdle {
            selected.value = bet.copy(modality = "Lotogol", lotogolSelections = listOf(LotogolMatchSelection(match, 4, 2)))
        }
        compose.onNodeWithText("Casa 4 ou mais × 2 Visitante").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun megaCampaignRecolorsConfirmedResultWithoutChangingItsContent() {
        val campaign = mutableStateOf(CaixaDSCampaign.DEFAULT)
        compose.setContent {
            val model = remember { model(bet) { ServiceResult.Success(listOf(result)) } }
            CaixaDSTheme {
                CaixaDSCampaignTheme(campaign.value, campaign.value) { ConfirmedBetResultRoute(bet, model, {}, {}) }
            }
        }
        val title = compose.onNodeWithText("Mega da Virada • Concurso 2000")
        val ordinary = title.captureToImage().asAndroidBitmap()
        compose.runOnIdle { campaign.value = CaixaDSCampaign.OCTOBER_ROSE }
        title.assertIsDisplayed()
        assertFalse(
            "Campaign presentation must change the result header palette",
            ordinary.sameAs(title.captureToImage().asAndroidBitmap())
        )
        compose.onNodeWithText("Sua aposta").performScrollTo().assertIsDisplayed()
    }

    private fun model(bet: ConfirmedBet, response: suspend () -> ServiceResult<List<DrawResult>>) = ConfirmedBetResultsViewModel(
        bet,
        GetConfirmedBetResultsUseCase {
            response()
        },
        FixedCoroutineDispatchers(Dispatchers.Main.immediate)
    )
}
