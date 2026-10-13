package br.gov.caixa.loterias.feature.betting

import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import br.gov.caixa.loterias.core.common.AppResult
import br.gov.caixa.loterias.core.designsystem.CaixaDSTheme
import br.gov.caixa.loterias.core.domain.betting.*
import br.gov.caixa.loterias.core.domain.cart.ShoppingCart
import java.io.File
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/** Public snapshot fixtures only. Does not invoke primaryAction on a completed ticket. */
@RunWith(Parameterized::class)
class ExtendedInstalledPairsTest(private val caseName: String, private val snapshot: JSONObject) {
    @get:Rule val compose = createComposeRule()

    @Test fun publicManualSelectionSurpriseOptionsHelpAndComplementWithoutCartSubmission() {
        val sends=AtomicInteger(0)
        val game=game(snapshot)
        val vm=BettingGameViewModel(game.modality.id,GetBettingParametersUseCase {
            AppResult.Success(BettingParameters(BigDecimal("500"),BigDecimal("30"),50,listOf(game)))
        },object:AddBetToCartUseCase {
            override suspend fun invoke(draft:BetDraft):AppResult<ShoppingCart> {
                sends.incrementAndGet(); error("Pair fixtures must not submit a ticket")
            }
        })
        // Match the read-only production screenshot's existing cart summary (2 bets / R$ 12,00).
        // This is display state only: the test never submits a bet or mutates a cart.
        compose.setContent {
            CaixaDSTheme(darkTheme=false) {
                BettingGameRoute(
                    viewModel = vm,
                    onBack = {},
                    onAdded = {},
                    cartTotal = BigDecimal("12.00"),
                    cartBetCount = 2,
                )
            }
        }
        compose.waitUntil(10_000) { vm.state.value.game != null }
        capture("manual",vm,sends)
        compose.runOnIdle {
            when(game.modality.id) {
                7 -> repeat(7) { vm.toggleSuperSevenDigit(it,snapshot.optInt("selectedDigit",0)) }
                19 -> game.matches.forEach { vm.toggleLotecaOutcome(it.number,LotecaOutcome.HOME) }
                else -> (1..game.minimumSelection).forEach(vm::toggleNumber)
            }
        }
        capture("selected",vm,sends)
        if(game.acceptsMirror) {
            compose.runOnIdle { vm.toggleMirror() }
            capture("mirror-selected",vm,sends)
            compose.runOnIdle { vm.toggleMirror() }
        }
        if(game.modality.id in listOf(9,11,20)) {
            // The completed numeric ticket opens the complement only, never sends.
            compose.runOnIdle { vm.primaryAction() }
            capture("complement",vm,sends)
            compose.runOnIdle {
                when(game.modality.id) {
                    9 -> (1..game.minimumClovers).forEach(vm::toggleClover)
                    11 -> game.luckyMonths.firstOrNull { it.number == snapshot.optInt("selectedMonth",game.luckyMonths.firstOrNull()?.number ?: -1) }?.let(vm::selectLuckyMonth)
                    20 -> game.teams.firstOrNull()?.let(vm::selectTeam)
                }
            }
            capture("complement-selected",vm,sends)
            if(game.modality.id == 9) {
                compose.onNodeWithText("${game.minimumClovers} trevos").performClick()
                capture("clover-quantity",vm,sends)
                compose.onNodeWithTag("betting-option-cancel").performClick()
            }
            compose.runOnIdle { vm.returnToNumbers() }
        }
        if(game.modality.id != 19) {
            compose.onNodeWithText("${game.minimumSelection} números").performClick()
            capture("quantity",vm,sends)
            compose.onNodeWithTag("betting-option-cancel").performClick()
            compose.runOnIdle { vm.selectMode(BetEntryMode.SURPRISE) }
            capture("surprise",vm,sends)
            if(game.acceptsMirror) {
                compose.runOnIdle { vm.toggleMirror() }
                capture("surprise-mirror-selected",vm,sends)
            }
            compose.runOnIdle { vm.selectMode(BetEntryMode.MANUAL) }
            // Snapshot may omit recurrence when the installed list has not been recorded.
            if(snapshot.has("recurring")) {
                compose.onNodeWithText("Teimosinha").performClick()
                capture("recurrence",vm,sends)
                compose.onNodeWithTag("betting-option-cancel").performClick()
            }
        }
        compose.onNodeWithText("Como jogar").performClick()
        val expectedRules = when(game.modality.id) {
                2 -> "Escolha de 6 a 20 números entre os 60 disponíveis"
                1 -> "Escolha de 5 a 15 números entre os 80 disponíveis"
                5 -> "50 números que você não escolheu"
                8 -> "Escolha de 15 a 20 números entre os 25 números disponíveis"
                9 -> "Escolha de 6 a 12 números entre os 50 disponíveis"
                11 -> "Escolha de 7 a 15 números entre os 31 disponíveis"
                18 -> "em um ou nos dois sorteios"
                19 -> "pelo menos 1 palpite deve ser duplo"
                20 -> "Escolha 10 números entre os 80 disponíveis"
                7 -> "O Super sete tem 7 colunas"
                else -> error("Public fixture has no installed help expectation: ${game.modality.id}")
        }
        compose.onNodeWithText(expectedRules, substring = true).assertExists()
        capture("help",vm,sends)
        compose.onNodeWithText("Entendi").performClick()
        compose.runOnIdle { assertEquals(0,sends.get()) }
    }

    private fun capture(state:String,vm:BettingGameViewModel,sends:AtomicInteger) {
        compose.waitForIdle()
        val instrumentation=InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync(); android.os.SystemClock.sleep(350); instrumentation.waitForIdleSync()
        val dir=File(requireNotNull(instrumentation.targetContext.externalCacheDir),"codex-parity").apply { mkdirs() }
        val name="compose-installed-${snapshot.getInt("id")}-${snapshot.getInt("contest") }-$state"
        val png=File(dir,"$name.png")
        val bitmap=requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        png.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG,100,it) }; bitmap.recycle()
        val resources=instrumentation.targetContext.resources
        File(dir,"$name.txt").writeText("fixture=true\nsource=observed-installed-public-snapshot\nnetworkSends=${sends.get()}\nstate=$state\nsnapshot=$snapshot\nselectedNumbers=${vm.state.value.selectedNumbers.sorted()}\nclovers=${vm.state.value.selectedClovers}\nmirror=${vm.state.value.generateMirror}\ndensityDpi=${resources.displayMetrics.densityDpi}\nfontScale=${resources.configuration.fontScale}\n")
        assertTrue(png.length()>0);assertEquals(0,sends.get())
    }

    companion object {
        /** Uses observed production public snapshots stored with the Android-test assets. */
        @JvmStatic @Parameterized.Parameters(name="{0}") fun snapshots():List<Array<Any>> {
            val json=InstrumentationRegistry.getArguments().getString("publicBettingFixtures")
                ?: InstrumentationRegistry.getInstrumentation().context.assets.open("public-betting-fixtures.json")
                    .bufferedReader(Charsets.UTF_8).use { it.readText() }
            val rows=JSONArray(json)
            return (0 until rows.length()).map { index ->
                val row = rows.getJSONObject(index)
                arrayOf<Any>("${row.getInt("id")}-${row.getInt("contest")}", row)
            }
        }

        private fun game(row:JSONObject):LotteryGameParameters {
            val id=row.getInt("id");val min=row.getInt("min");val max=row.getInt("max")
            val base=BigDecimal(row.getString("basePrice"))
            val explicitPrices=row.optJSONArray("prices")
            val prices=if(explicitPrices!=null)(0 until explicitPrices.length()).map { index ->
                val item=explicitPrices.getJSONObject(index)
                BetPrice(item.getInt("numbers"),item.optInt("clovers",0),BigDecimal(item.getString("amount")))
            } else (min..max).map { n -> BetPrice(n,if(id==9)2 else 0,base.multiply(BigDecimal.valueOf(combinations(n,min)))) }
            val recurrence=row.optJSONArray("recurring")
            val months=row.optJSONArray("months")
            val teams=row.optJSONArray("teams")
            val matches=row.optJSONArray("matches")
            fun team(item:JSONObject)=SportsTeam(item.getInt("number"),item.getString("name"),item.optString("state"),shieldBase64=item.optString("badge").takeIf { it.isNotEmpty() })
            return LotteryGameParameters(
                LotteryModality(id,row.getString("name"),row.getString("name")),row.getInt("contest"),row.getString("draw"),
                row.getInt("maxNumber"),min,max,row.optInt("maxSurprises",100),
                if(recurrence==null)listOf(0) else (0 until recurrence.length()).map(recurrence::getInt),prices,
                minimumClovers=if(id==9)2 else 0,maximumClovers=if(id==9)6 else 0,maximumCloverNumber=if(id==9)6 else 0,
                acceptsMirror=id==5,
                luckyMonths=if(months==null)emptyList() else (0 until months.length()).map { val item=months.getJSONObject(it);LuckyMonth(item.getInt("number"),item.getString("name"),item.getString("short")) },
                teams=if(teams==null)emptyList() else (0 until teams.length()).map { team(teams.getJSONObject(it)) },
                matches=if(matches==null)emptyList() else (0 until matches.length()).map { val item=matches.getJSONObject(it);SportsMatch(item.getInt("number"),team(item.getJSONObject("home")),team(item.getJSONObject("away"))) },
            )
        }
        private fun combinations(n:Int,k:Int):Long { var result=1L;(1..k).forEach { result=result*(n-k+it)/it };return result }
    }
}
