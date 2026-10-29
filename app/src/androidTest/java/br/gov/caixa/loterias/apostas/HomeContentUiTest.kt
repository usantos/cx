package br.gov.caixa.loterias.apostas

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.designsystem.theme.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.configuration.model.HomeFeatureConfiguration
import br.gov.caixa.loterias.feature.betting.domain.model.BetPrice
import br.gov.caixa.loterias.feature.betting.domain.model.BettingParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryGameParameters
import br.gov.caixa.loterias.feature.betting.domain.model.LotteryModality
import br.gov.caixa.loterias.feature.betting.domain.model.PoolModalityAvailability
import br.gov.caixa.loterias.feature.home.presentation.components.infiniteCarouselInitialPage
import br.gov.caixa.loterias.feature.home.presentation.mapper.name
import br.gov.caixa.loterias.feature.home.presentation.mapper.toHomeLotteries
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeActions
import br.gov.caixa.loterias.feature.home.presentation.screen.HomeContent
import br.gov.caixa.loterias.feature.home.presentation.screen.HomePresentation
import br.gov.caixa.loterias.feature.home.presentation.state.HomeUiState
import br.gov.caixa.loterias.feature.results.presentation.state.ResultsUiState
import java.io.File
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeContentUiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val parameters = BettingParameters(BigDecimal.TEN, BigDecimal.ONE, 30,
        listOf(game(2, "Mega-Sena", "MEGA_SENA"), game(7, "Quina", "QUINA")),
        availablePoolModalities = listOf(PoolModalityAvailability(2, "MEGA_SENA", "NORMAL")))
    private val home = HomeUiState(loading = false, parameters = parameters,
        features = HomeFeatureConfiguration(poolSalesEnabled = true),
        cartTotal = BigDecimal("20.00"), cartItemCount = 2, unreadNotificationCount = 1)
    private val presentation = mutableStateOf(HomePresentation(false, null, false, 0.dp, 0f))
    private val requested = mutableStateOf(2)
    private val calls = mutableListOf<String>()

    @Test fun cardAndCartActionsKeepTheirAlignmentAndExpandedFooterGate() {
        show()
        val pool = compose.onNodeWithText("Compre seu bolão")
        val bet = compose.onNode(hasText("Aposte", substring = true) and hasClickAction())
        val cart = compose.onNodeWithText("Valor atual do carrinho")
        val rootBounds = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val poolBounds = pool.fetchSemanticsNode().boundsInRoot
        val betBounds = bet.fetchSemanticsNode().boundsInRoot
        val cartBounds = cart.fetchSemanticsNode().boundsInRoot
        assertEquals(rootBounds.center.x, poolBounds.center.x, 0f)
        assertEquals(rootBounds.center.x, betBounds.center.x, 0f)
        assertEquals(rootBounds.center.x, cartBounds.center.x, 0f)
        assertTrue(poolBounds.bottom < betBounds.top)
        assertTrue(betBounds.bottom < cartBounds.top)
        write("classic-action-bounds.txt", listOf(rootBounds, poolBounds, betBounds, cartBounds).joinToString("\n"))
        capture("classic-with-cart")
        pool.performClick(); bet.performClick(); cart.performClick()
        compose.onNodeWithContentDescription("Abrir menu").performClick()
        compose.onNodeWithContentDescription("Notificações").performClick()
        compose.onNodeWithContentDescription("Apresentar atalhos").performClick()
        compose.runOnIdle { assertEquals(listOf("pool:2", "bet:2", "cart", "menu", "notifications", "quick"), calls) }
        compose.runOnIdle { presentation.value = presentation.value.copy(expandedResultPage = infiniteCarouselInitialPage(2)) }
        compose.onNodeWithText("Compre seu bolão").assertDoesNotExist()
        compose.onNodeWithText("Valor atual do carrinho").assertDoesNotExist()
        compose.onAllNodes(hasText("Aposte", substring = true) and hasClickAction()).assertCountEquals(0)
        capture("classic-result-without-footer")
    }

    @Test fun requestedModalityRemainsSelectedAcrossPresentationRecomposition() {
        requested.value = 7
        show()
        val bet = compose.onNode(hasText("Aposte", substring = true) and hasClickAction())
        bet.performClick()
        compose.runOnIdle { presentation.value = presentation.value.copy(cloverRotation = 180f) }
        bet.performClick()
        compose.runOnIdle { assertEquals(listOf("bet:7", "bet:7"), calls) }
        capture("classic-selected-quina")
        compose.runOnIdle { requested.value = 2 }
        compose.waitForIdle()
        bet.performClick()
        compose.runOnIdle { assertEquals(listOf("bet:7", "bet:7", "bet:2"), calls) }
    }

    private fun show() {
        val lotteries = parameters.toHomeLotteries(false)
        compose.setContent { CaixaDSTheme {
            HomeContent(home, null, lotteries, ResultsUiState(), requested.value, presentation.value,
                HomeActions(retry = { calls += "retry" }, openMenu = { calls += "menu" },
                    openNotifications = { calls += "notifications" }, toggleCampaign = { calls += "campaign" },
                    toggleQuickAccess = { calls += "quick" }, readTicket = {}, responsibleGaming = {},
                    socialTransfers = {}, womenSupport = {}, toggleResult = {},
                    pool = { calls += "pool:${it.modalityId}" }, bet = { calls += "bet:${it.modalityId}" },
                    openCart = { calls += "cart" }))
        } }
        compose.waitForIdle()
    }

    private fun capture(name: String) {
        compose.mainClock.advanceTimeBy(1_000)
        compose.waitForIdle()
        // RippleDrawable usa o relógio da plataforma, mesmo quando o relógio do Compose está congelado.
        android.os.SystemClock.sleep(600)
        val file = target("$name.png")
        file.outputStream().use { check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)) }
    }

    private fun write(name: String, text: String) { target(name).writeText(text) }
    private fun target(name: String): File {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val phase = InstrumentationRegistry.getArguments().getString("homePhase") ?: "verification"
        return File(instrumentation.targetContext.getExternalFilesDir(null), "home-refactor/$phase/$name").also {
            check(it.parentFile!!.isDirectory || it.parentFile!!.mkdirs())
        }
    }

    private fun game(id: Int, name: String, code: String) = LotteryGameParameters(
        LotteryModality(id, name, code), 4321, "17/10/2026", 60, 6, 20, 10,
        emptyList(), listOf(BetPrice(6, 0, BigDecimal("5.00"))), estimatedPrize = BigDecimal("10000000"))
}
