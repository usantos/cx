package br.gov.caixa.loterias.apostas

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSCampaign
import br.gov.caixa.loterias.core.designsystem.theme.LoteriasCaixaDSTheme
import br.gov.caixa.loterias.core.designsystem.tokens.CxHomeV2Colors
import br.gov.caixa.loterias.core.designsystem.tokens.LoteriasCaixaDSColorTokens
import br.gov.caixa.loterias.core.testing.delivery.setFeatureContent
import br.gov.caixa.loterias.feature.home.domain.repository.HomeLayoutRepository
import br.gov.caixa.loterias.feature.home.presentation.mapper.HomeV2Modality
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeV2Presentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLottery
import br.gov.caixa.loterias.feature.home.presentation.state.HomeLotteryKind
import br.gov.caixa.loterias.feature.home.ui.R as HomeUiR
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeV2UiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private var poolsEnabled = false
    private val calls = mutableListOf<String>()
    private val preferences = object : HomeLayoutRepository {
        var value: String? = null
        override suspend fun loadOrder() = value
        override suspend fun saveOrder(order: String) {
            value = order
        }
    }
    private val actions = object : HomeV2Presentation.Actions {
        override fun bet(modalidade: HomeLottery) {
            calls += "bet:${modalidade.contestNumber}"
        }
        override fun pool(modalidade: HomeLottery) {
            calls += "pool"
        }
        override fun results() {
            calls += "results"
        }
        override fun modalityResults(modalidade: HomeV2Modality) {
            calls += modalidade.resultCode
        }
        override fun games() {
            calls += "games"
        }
        override fun favorites() {
            calls += "favorites"
        }
        override fun menu() {
            calls += "menu"
        }
        override fun notifications() {
            calls += "notifications"
        }
        override fun cart() {
            calls += "cart"
        }
        override fun profile() {
            calls += "profile"
        }
        override fun canPool(modalidade: HomeV2Modality) = poolsEnabled
    }

    @Test fun poolDisabledDoesNotOfferPurchaseAndBetKeepsBackendContest() {
        show()
        compose.onAllNodesWithText("Compre seu bolão").assertCountEquals(0)
        compose.onNodeWithText("Aposte").performClick()
        compose.runOnIdle { assertEquals(listOf("bet:4321"), calls) }
        compose.onNodeWithText("Último resultado ›").performClick()
        compose.runOnIdle { assertEquals("megasena", calls.last()) }
    }

    @Test fun shortcutsPreserveCxDestinations() {
        show()
        compose.onNodeWithText("Estatísticas").performScrollTo().performClick()
        compose.onNodeWithText("Promoções").performScrollTo().performClick()
        compose.onNodeWithText("Carrinho").performClick()
        compose.runOnIdle { assertEquals(listOf("results", "notifications", "cart"), calls) }
    }

    @Test fun modalityExpansionUpdatesLabelAndKeepsResultsLabel() {
        val lotteries = listOf(
            "megasena", "maismilionaria", "loteca", "diadesorte",
            "duplasena", "lotomania", "quina", "timemania", "lotofacil", "supersete"
        )
            .map { lottery().copy(resultCode = it) }
        compose.setFeatureContent { HomeV2TestContent(lotteries, false, emptyMap(), preferences, actions) }

        compose.onAllNodesWithText("Lotofácil").assertCountEquals(0)
        compose.onAllNodesWithText("Super Sete").assertCountEquals(0)
        compose.onAllNodesWithText("Ver todas  ›")[0].performScrollTo().performClick()
        compose.onNodeWithText("Ver menos  ›").assertIsDisplayed()
        compose.onNodeWithText("Lotofácil").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Super Sete").assertIsDisplayed()

        compose.onAllNodesWithText("Ver todas  ›").assertCountEquals(1)
        compose.onNodeWithText("Ver menos  ›").performScrollTo().performClick()
        compose.onAllNodesWithText("Ver todas  ›").assertCountEquals(2)
        compose.onAllNodesWithText("Ver menos  ›").assertCountEquals(0)
        compose.onAllNodesWithText("Lotofácil").assertCountEquals(0)
        compose.onAllNodesWithText("Super Sete").assertCountEquals(0)
    }

    @Test fun homeTextGeometryMatchesCxUnderApplicationTheme() {
        poolsEnabled = true
        val hostTheme = mutableStateOf(false)
        val reference = HomeV2Presentation({
            br.gov.caixa.loterias.feature.home.presentation.viewmodel.HomePreferencesViewModel.DEFAULT_ORDER
        }, { _, _ -> }) { actions }
        val lottery = lottery()
        compose.setFeatureContent {
            if (hostTheme.value) {
                LoteriasCaixaDSTheme { HomeV2TestContent(listOf(lottery), false, emptyMap(), preferences, actions) }
            } else {
                // HomeV2View do CX fornece a tipografia padrão do Material3 em sua própria raiz.
                MaterialTheme(typography = Typography()) { reference.Home(listOf(lottery), false, emptyMap()) }
            }
        }
        val labels = listOf(
            "PRÓXIMO SORTEIO",
            "Prêmio estimado",
            "Escolha sua modalidade",
            "Jogue com amigos e aumente suas chances de ganhar."
        )
        val cxBounds = labels.associateWith { compose.onNodeWithText(it).fetchSemanticsNode().boundsInRoot }
        compose.runOnIdle { hostTheme.value = true }
        labels.forEach { label ->
            val actual = compose.onNodeWithText(label).fetchSemanticsNode().boundsInRoot
            val expected = cxBounds.getValue(label)
            assertEquals("CX text width: $label", expected.width, actual.width, 1f)
            assertEquals("CX text height: $label", expected.height, actual.height, 1f)
            assertEquals("CX text position: $label", expected.top, actual.top, 1f)
        }
    }

    @Test fun octoberRibbonOpensInfoWithoutPermanentHomeBanner() {
        compose.setFeatureContent {
            HomeV2TestContent(
                listOf(lottery()),
                false,
                emptyMap(),
                preferences,
                actions,
                campaign = LoteriasCaixaDSCampaign.OCTOBER_ROSE,
                megaSenaCampaign = LoteriasCaixaDSCampaign.DEFAULT
            )
        }
        compose.onAllNodesWithText("Outubro Rosa").assertCountEquals(0)
        compose.onNodeWithContentDescription("Outubro Rosa").assertIsDisplayed().performClick()
        compose.onNodeWithText("Outubro Rosa").assertIsDisplayed()
        compose.onNodeWithContentDescription("Outubro Rosa").performClick()
        compose.onAllNodesWithText("Outubro Rosa").assertCountEquals(0)
    }

    @Test fun octoberHeroButtonsUseLegacySurfaceRoles() {
        poolsEnabled = true
        compose.setFeatureContent {
            HomeV2TestContent(
                listOf(lottery()),
                false,
                emptyMap(),
                preferences,
                actions,
                campaign = LoteriasCaixaDSCampaign.OCTOBER_ROSE,
                megaSenaCampaign = LoteriasCaixaDSCampaign.OCTOBER_ROSE
            )
        }
        fun assertColors(label: String, background: Color, foreground: Color) {
            val pixels = compose.onAllNodesWithText(label)[0].captureToImage().toPixelMap()
            val colors = buildSet {
                for (y in 0 until pixels.height) for (x in 0 until pixels.width) add(pixels[x, y].toArgb())
            }
            assertTrue("$label background", background.toArgb() in colors)
            assertTrue("$label foreground", foreground.toArgb() in colors)
        }
        // HomeV2View do CX mantém brancas as superfícies das duas ações, independentemente da cor da campanha do cartão.
        assertColors("Aposte", LoteriasCaixaDSColorTokens.White, CxHomeV2Colors.CFF003B71)
        assertColors("Compre seu bolão", LoteriasCaixaDSColorTokens.White, CxHomeV2Colors.CFF003B71)
    }

    private fun show() {
        compose.setFeatureContent { HomeV2TestContent(listOf(lottery()), false, emptyMap(), preferences, actions) }
    }

    private fun lottery(): HomeLottery = HomeLottery(
        2, "NORMAL", "megasena", "Mega-Sena", 4321,
        BigDecimal("100000000"), BigDecimal("6"), "2026-10-15T21:00:00", "", true,
        HomeLotteryKind.LOTTERY, LoteriasCaixaDSColorTokens.MegaSenaLight,
        LoteriasCaixaDSColorTokens.MegaSenaDark, LoteriasCaixaDSColorTokens.White,
        HomeUiR.drawable.trevo_megasena_fundo_branco,
        HomeUiR.drawable.ic_calendario,
        HomeUiR.drawable.ic_calendario
    )
}
