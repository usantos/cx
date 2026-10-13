package br.gov.caixa.loterias.apostas

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.gov.caixa.loterias.core.designsystem.CaixaDSColorTokens
import br.gov.caixa.loterias.core.domain.home.HomeLayoutPreferences
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class HomeV2UiTest {
    @get:Rule val compose = createAndroidComposeRule<HomeDrawerTestActivity>()
    private val calls = mutableListOf<String>()
    private val preferences = object : HomeLayoutPreferences {
        var value: String? = null
        override fun loadOrder() = value
        override fun saveOrder(order: String) { value = order }
    }
    private val actions = object : HomeV2Presentation.Actions {
        override fun bet(modalidade: HomeLottery) { calls += "bet:${modalidade.contestNumber}" }
        override fun pool(modalidade: HomeLottery) { calls += "pool" }
        override fun results() { calls += "results" }
        override fun modalityResults(modalidade: HomeV2Modality) { calls += modalidade.resultCode }
        override fun games() { calls += "games" }
        override fun favorites() { calls += "favorites" }
        override fun menu() { calls += "menu" }
        override fun notifications() { calls += "notifications" }
        override fun cart() { calls += "cart" }
        override fun profile() { calls += "profile" }
        override fun canPool(modalidade: HomeV2Modality) = false
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

    private fun show() {
        val lottery = HomeLottery(2, "NORMAL", "megasena", "Mega-Sena", 4321,
            BigDecimal("100000000"), BigDecimal("6"), "2026-10-15T21:00:00", "", true,
            HomeLotteryKind.LOTTERY, CaixaDSColorTokens.LegacyMegaSenaLight,
            CaixaDSColorTokens.LegacyMegaSenaDark, CaixaDSColorTokens.White,
            R.drawable.trevo_megasena_fundo_branco, R.drawable.ic_calendario, R.drawable.ic_calendario)
        compose.setContent { HomeV2Content(listOf(lottery), false, emptyMap(), preferences, actions) }
    }
}
